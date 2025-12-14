package org.todaybook.gateway.auth.infrastructure.webclient;

import java.util.List;

public record UserSummary(String userId, String nickname, List<UserRole> roles) {}
