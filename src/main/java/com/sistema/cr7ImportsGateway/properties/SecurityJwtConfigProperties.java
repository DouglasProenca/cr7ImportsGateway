package com.sistema.cr7ImportsGateway.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "security.jwt.token")
public class SecurityJwtConfigProperties {

	String secret_key;
		
}
