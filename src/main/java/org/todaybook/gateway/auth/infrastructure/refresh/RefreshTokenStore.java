package org.todaybook.gateway.auth.infrastructure.refresh;

import java.time.Duration;
import reactor.core.publisher.Mono;

public interface RefreshTokenStore {

  Mono<Boolean> save(String refreshToken, String userId, Duration ttl);

  Mono<Boolean> delete(String refreshToken);

  Mono<String> rotate(String oldRefreshToken, String newRefreshToken, Duration ttl);
}
