package com.sistema.cr7ImportsGateway.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@Data
@ConfigurationProperties(prefix = "security.jwt.token")
public class SecurityJwtConfigProperties {

    Integer expire_length;
	String secret_key;
		
}
