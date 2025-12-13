package org.todaybook.gateway.auth.infrastructure.refresh;

public interface RefreshTokenEncoder {
  String encode(String refreshToken);
}
