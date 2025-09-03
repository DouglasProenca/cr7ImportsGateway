package com.sistema.cr7ImportsGateway.security.jwt;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.sistema.cr7ImportsGateway.properties.SecurityJwtConfigProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class JwtTokenProvider {

	private final SecurityJwtConfigProperties securityProperties;
	private final RedisTemplate<String, String> redisTemplate;

	public void isValid(String jwt) throws Exception   {
		try {
			Claims claims = Jwts.parser()
                              .setSigningKey(securityProperties.getSecret_key())
                              .parseClaimsJws(jwt.trim())
                              .getBody();
			
			if (redisTemplate.opsForValue().get(claims.getSubject()) == null) throw new Exception("Token não existe no redis.");
			
		} catch (ExpiredJwtException e) {
			log.error("Token expirado: ", e);
			throw new Exception("Token expirado: {}", e);
		} catch (JwtException e) {
			    throw new Exception("Token inválido", e);
		} catch (Exception e) {
			log.error("Token inválido: ", e);
			throw new Exception("Token inválido: {}");
		}
	}
	
	public String getIdentifierFromToken(String jwt) {
		return Jwts.parser().setSigningKey(securityProperties.getSecret_key()).parseClaimsJws(jwt).getBody().getSubject();
	}

}
