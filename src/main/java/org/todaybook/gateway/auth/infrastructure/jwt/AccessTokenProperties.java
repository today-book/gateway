package org.todaybook.gateway.auth.infrastructure.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "token.access")
public class AccessTokenProperties {
  private String secret;
  private Long expirationSeconds;
}
