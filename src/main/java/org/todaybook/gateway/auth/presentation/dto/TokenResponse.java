package org.todaybook.gateway.auth.presentation.dto;

import org.todaybook.gateway.auth.application.dto.IssuedToken;

public record TokenResponse(String accessToken, String tokenType, long expiresIn) {

  public static TokenResponse from(IssuedToken issuedToken) {
    return new TokenResponse(issuedToken.accessToken(), "Bearer", issuedToken.expiresInSeconds());
  }
}
