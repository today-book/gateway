package org.todaybook.gateway.auth.infrastructure.refresh;

public record IssuedRefreshToken(String token, long expiresInSeconds) {}
