package org.todaybook.gateway.auth.infrastructure.webclient;

public record UserOauthCreateRequest(String provider, String providerUserId, String nickname) {}
