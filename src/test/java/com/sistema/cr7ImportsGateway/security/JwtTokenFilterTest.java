package com.sistema.cr7ImportsGateway.security;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.http.server.reactive.ServerHttpResponse;

import com.sistema.cr7ImportsGateway.security.jwt.JwtTokenFilter;
import com.sistema.cr7ImportsGateway.security.jwt.JwtTokenProvider;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class JwtTokenFilterTest {
	
	@Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    GatewayFilterChain chain;

    @InjectMocks
    JwtTokenFilter jwtTokenFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnUnauthorizedWhenNoAuthorizationHeader() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/secure-endpoint").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<Void> result = jwtTokenFilter.filter(exchange, chain);

        StepVerifier.create(result).verifyComplete();

        ServerHttpResponse response = exchange.getResponse();
        assert response.getStatusCode() == HttpStatus.UNAUTHORIZED;
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenInvalid() throws Exception {
        MockServerHttpRequest request = MockServerHttpRequest.get("/secure-endpoint")
                .header("Authorization", "Bearer invalidToken")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        doThrow(new RuntimeException("Invalid token")).when(jwtTokenProvider).isValid("invalidToken");

        Mono<Void> result = jwtTokenFilter.filter(exchange, chain);

        StepVerifier.create(result).verifyComplete();

        ServerHttpResponse response = exchange.getResponse();
        assert response.getStatusCode() == HttpStatus.UNAUTHORIZED;
    }

    @Test
    void shouldProceedWhenTokenValid() throws Exception {
        MockServerHttpRequest request = MockServerHttpRequest.get("/secure-endpoint")
                .header("Authorization", "Bearer validToken")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        doThrow(new RuntimeException("Invalid token")).when(jwtTokenProvider).isValid("invalidToken");
        when(jwtTokenProvider.getIdentifierFromToken("validToken")).thenReturn("user123");
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        Mono<Void> result = jwtTokenFilter.filter(exchange, chain);

        StepVerifier.create(result).verifyComplete();

        // garante que o header foi adicionado
        assert exchange.getRequest().getHeaders().getFirst("LOGGED_USER_IDENTIFIER").equals("user123");
    }

    @Test
    void shouldBypassWhiteListUrls() throws Exception {
        MockServerHttpRequest request = MockServerHttpRequest.get("/swagger-ui").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(chain.filter(exchange)).thenReturn(Mono.empty());

        Mono<Void> result = jwtTokenFilter.filter(exchange, chain);

        StepVerifier.create(result).verifyComplete();

        // não deve chamar jwtTokenProvider
        verify(jwtTokenProvider, never()).isValid(anyString());
    }
}
