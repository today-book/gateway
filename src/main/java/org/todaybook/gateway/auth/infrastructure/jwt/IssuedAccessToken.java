package org.todaybook.gateway.auth.infrastructure.jwt;

public record IssuedAccessToken(String token, long expiresInSeconds) {}
