package org.todaybook.gateway.auth.application.dto;

import java.util.List;
import org.todaybook.gateway.auth.infrastructure.webclient.UserRole;
import org.todaybook.gateway.auth.infrastructure.webclient.UserSummary;

public record AuthenticatedUser(String userId, String nickname, List<UserRole> roles) {

  public static AuthenticatedUser from(UserSummary userSummary) {
    return new AuthenticatedUser(userSummary.userId(), userSummary.nickname(), userSummary.roles());
  }
}
