package org.todaybook.gateway.auth.infrastructure.refresh;

public record RotatedRefreshToken(String userId, String token, long expiresInSeconds) {}
