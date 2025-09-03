package com.sistema.cr7ImportsGateway;
import com.sistema.cr7ImportsGateway.security.jwt.JwtTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import reactor.core.publisher.Mono;

@EnableDiscoveryClient
@SpringBootApplication
public class Cr7ImportsGatewayApplication {
	
	@Autowired
	private JwtTokenFilter filter;

	public static void main(String[] args) {
		new SpringApplicationBuilder(Cr7ImportsGatewayApplication.class)
		.web(WebApplicationType.REACTIVE)
		.run(args);
	}

	@Bean
	KeyResolver userKeyResolver() {
		return exchange -> Mono.just("1");
	}

	@Bean
	RouteLocator myRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
			    .route("apicr7imports", r -> r
			        .path("/apicr7imports/**")
			        .filters(f -> f.filter(filter)) 
			        .uri("http://localhost:8080"))   
			    .build();
	}
	
}
