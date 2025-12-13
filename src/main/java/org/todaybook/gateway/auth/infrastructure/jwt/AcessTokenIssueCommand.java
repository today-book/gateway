package org.todaybook.gateway.auth.infrastructure.jwt;

import java.util.List;

public record AcessTokenIssueCommand(String userId, String nickname, List<String> roles) {}
