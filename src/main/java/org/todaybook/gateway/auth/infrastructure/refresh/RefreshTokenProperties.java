package org.todaybook.gateway.auth.infrastructure.refresh;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "token.refresh")
public class RefreshTokenProperties {
  private String secret;
  private Long expirationSeconds;
}
