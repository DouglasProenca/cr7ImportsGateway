package com.sistema.cr7ImportsGateway.security.jwt;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class JwtTokenFilter implements GatewayFilter, Ordered {

	private final JwtTokenProvider jwtTokenProvider;
	private static final String AUTHORIZATION_HEADER = "Authorization";

	@Override
	public int getOrder() {
		return -2;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		List<String> WhiteList = Arrays.asList(
				// Swagger
				"/v3/api-docs", 
				"/v3/api-docs/swagger-config", 
				"/swagger-ui.html", 
				"/swagger-ui",
				// Token CR7 Imports
				"/apicr7imports/acesso/login");

		Predicate<ServerHttpRequest> isApiSecured = r -> WhiteList.stream().noneMatch(uri -> r.getURI().getPath().contains(uri));

		if (isApiSecured.test(exchange.getRequest())) {
			if (!exchange.getRequest().getHeaders().containsKey(AUTHORIZATION_HEADER)) {
				ServerHttpResponse response = exchange.getResponse();
				response.setStatusCode(HttpStatus.UNAUTHORIZED);
				return response.setComplete();
			}

			final String token = exchange.getRequest().getHeaders().getOrEmpty(AUTHORIZATION_HEADER).get(0).replace("Bearer ", "");
			
			try {
				jwtTokenProvider.isValid(token);
			} catch (Exception e) {
				ServerHttpResponse response = exchange.getResponse();
				response.setStatusCode(HttpStatus.UNAUTHORIZED);
				return response.setComplete();
			}
			exchange.getRequest().mutate().header("LOGGED_USER_IDENTIFIER", jwtTokenProvider.getIdentifierFromToken(token)).build();
		}
		return chain.filter(exchange);
	}

}
