package com.sistema.cr7ImportsGateway.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistema.cr7ImportsGateway.exception.CustomErrorResponse;

public class ErrorReturnTransferTest {

	ErrorReturnTransfer errorReturnTransfer;
	ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		errorReturnTransfer = new ErrorReturnTransfer();
		objectMapper = new ObjectMapper();
	}

	@Test
	void shouldReturnCustomErrorWhenJsonIsValid() throws Exception {
		CustomErrorResponse response = new CustomErrorResponse(401, "Invalid token","401");
		String json = objectMapper.writeValueAsString(response);

		byte[] result = errorReturnTransfer.process(objectMapper, json);
		String strResult = new String(result, StandardCharsets.UTF_8);

		assertTrue(strResult.contains("\"error\": \"401\""));
		assertTrue(strResult.contains("\"message\": \"Invalid token\""));
	}

	@Test
	void shouldReturnGenericErrorWhenJsonIsInvalid() {
		String invalidJson = "not-a-json";

		byte[] result = errorReturnTransfer.process(objectMapper, invalidJson);
		String strResult = new String(result, StandardCharsets.UTF_8);

		assertTrue(strResult.contains("\"error\": \"500.000\""));
		assertTrue(strResult.contains("\"message\": \"Untracked internal error.\""));
	}
}
