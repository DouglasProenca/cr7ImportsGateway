package com.sistema.cr7ImportsGateway.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.sistema.cr7ImportsGateway.mocks.SecurityJwtConfigPropertiesMock;
import com.sistema.cr7ImportsGateway.properties.SecurityJwtConfigProperties;
import com.sistema.cr7ImportsGateway.security.jwt.JwtTokenProvider;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtTokenProviderTest {

	SecurityJwtConfigPropertiesMock securityJwtConfigPropertiesMock;
	JwtTokenProvider jwtTokenProvider;
	String validToken;
    String expiredToken;
    
    @Mock
    ValueOperations<String, String> valueOps;
    
    @Mock
    RedisTemplate<String, String> redisTemplate;
    
    @Mock
    SecurityJwtConfigProperties securityProperties;
	
	@SuppressWarnings("deprecation")
	@BeforeEach
	void setUpMocks() throws Exception {
		securityJwtConfigPropertiesMock = new SecurityJwtConfigPropertiesMock();
		MockitoAnnotations.initMocks(this);

		when(securityProperties.getSecret_key()).thenReturn(securityJwtConfigPropertiesMock.mockEntity().getSecret_key());
		when(redisTemplate.opsForValue()).thenReturn(valueOps);
		
		jwtTokenProvider = new JwtTokenProvider(securityProperties, redisTemplate);

		
		 // token válido
        validToken = Jwts.builder()
                .setSubject("user123")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60)) // expira em 1 min
                .signWith(SignatureAlgorithm.HS256, securityProperties.getSecret_key())
                .compact();
        
        // token expirado
        expiredToken = Jwts.builder()
                .setSubject("user123")
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 10))
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60)) // expirado há 1 min
                .signWith(SignatureAlgorithm.HS256, securityProperties.getSecret_key())
                .compact();

	}

	@Test
	void shouldValidateTokenWhenExistsInRedis() throws Exception {
	        when(valueOps.get("user123")).thenReturn("someValue");

	        assertDoesNotThrow(() -> jwtTokenProvider.isValid(validToken));
	}
	
	@Test
    void shouldThrowExceptionWhenTokenNotInRedis() {
        when(valueOps.get("user123")).thenReturn(null);

        Exception ex = assertThrows(Exception.class, () -> jwtTokenProvider.isValid(validToken));
        assertEquals("Token inválido: {}", ex.getMessage());
    }
	
	@Test
	void shouldThrowExceptionWhenTokenExpired() {
	    Exception ex = assertThrows(Exception.class, () -> jwtTokenProvider.isValid(expiredToken));
	    assertTrue(ex.getMessage().contains("Token expirado"));
	}

	@Test
	void shouldGetIdentifierFromToken() {
	    String subject = jwtTokenProvider.getIdentifierFromToken(validToken);
	    assertEquals("user123", subject);
	}
}