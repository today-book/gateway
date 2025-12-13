package org.todaybook.gateway.auth.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.todaybook.gateway.auth.Infrastructure.jwt.JwtProvider;
import org.todaybook.gateway.auth.Infrastructure.jwt.JwtTokenCreateCommand;
import org.todaybook.gateway.auth.Infrastructure.redis.AuthCodeStore;
import org.todaybook.gateway.auth.Infrastructure.redis.RefreshTokenStore;
import org.todaybook.gateway.auth.domain.JwtToken;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final RefreshTokenStore refreshTokenStore;
  private final AuthCodeStore authCodeStore;
  private final JwtProvider jwtProvider;

  // TODO 유저 조회 -> Jwt Claim 셋팅
  public Mono<JwtToken> loginWithAuthCode(String authCode) {
    return authCodeStore
        .getUserId(authCode)
        .switchIfEmpty(Mono.error(new UnauthorizedException("INVALID_AUTH_CODE")))
        .flatMap(
            userId ->
                authCodeStore
                    .delete(authCode)
                    .then(
                        jwtProvider.createToken(
                            new JwtTokenCreateCommand(userId, null, List.of("USER_ROLE")))));
  }

  public Mono<JwtToken> refresh(String refreshToken) {
    return refreshTokenStore
        .findUserId(refreshToken)
        .switchIfEmpty(Mono.error(new UnauthorizedException()))
        .flatMap(
            userId ->
                refreshTokenStore
                    .delete(refreshToken)
                    .then(
                        jwtProvider.createToken(new JwtTokenCreateCommand(userId, "", List.of()))));
  }

  public Mono<Void> delete(String refreshToken) {
    return refreshTokenStore.delete(refreshToken).then();
  }
}
