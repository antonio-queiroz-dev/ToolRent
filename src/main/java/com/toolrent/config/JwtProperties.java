package com.toolrent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "toolrent.jwt")
public record JwtProperties(String secret, Duration expiration) {
}
