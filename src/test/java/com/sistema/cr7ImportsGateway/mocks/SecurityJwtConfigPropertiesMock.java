package com.sistema.cr7ImportsGateway.mocks;

import com.sistema.cr7ImportsGateway.properties.SecurityJwtConfigProperties;

public class SecurityJwtConfigPropertiesMock {
	
	public SecurityJwtConfigProperties mockEntity() {
		SecurityJwtConfigProperties securityJwtConfigProperties = new SecurityJwtConfigProperties();
		securityJwtConfigProperties.setSecret_key("ChaveGrandeParaTesteVambora");
		return securityJwtConfigProperties;
	}
}
