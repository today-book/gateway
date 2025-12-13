package org.todaybook.gateway.auth.Infrastructure.jwt;

import java.util.List;

public record JwtTokenCreateCommand(String userId, String nickname, List<String> roles) {}
