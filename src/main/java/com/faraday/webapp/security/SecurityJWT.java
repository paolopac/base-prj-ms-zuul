package com.faraday.webapp.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;


@Component
@ConfigurationProperties("security")
@Data
public class SecurityJWT
{
	private String url;
	private String header;
	private String prefix;
	private int expiration;
	private String secret;
}
