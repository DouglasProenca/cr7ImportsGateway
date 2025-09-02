package com.sistema.cr7ImportsGateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class Cr7ImportsGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(Cr7ImportsGatewayApplication.class, args);
	}

}
