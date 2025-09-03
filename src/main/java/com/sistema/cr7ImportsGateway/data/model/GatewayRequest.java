package com.sistema.cr7ImportsGateway.data.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity(name = "gateway_request")
public class GatewayRequest {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
	@Column(name = "metodo")
    String method;
	@Column(name = "endereco") 
    String path;
    String query;
    String headers;
    @Column(name = "data_criacao")
    LocalDateTime timestamp;
    Integer status;
}
