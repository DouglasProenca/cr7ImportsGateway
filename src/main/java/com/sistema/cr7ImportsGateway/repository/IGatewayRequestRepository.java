package com.sistema.cr7ImportsGateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistema.cr7ImportsGateway.data.model.GatewayRequest;

public interface IGatewayRequestRepository extends JpaRepository<GatewayRequest, Integer> {

}
