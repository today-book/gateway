package org.todaybook.gateway.security.oauth;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.todaybook.gateway.auth.Infrastructure.redis.AuthCodeStore;
import org.todaybook.gateway.security.kakao.KakaoOAuth2User;
import reactor.core.publisher.Mono;

@Component
@EnableConfigurationProperties(AuthProperties.class)
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements ServerAuthenticationSuccessHandler {

  private final AuthCodeStore authCodeStore;
  private final AuthProperties authProperties;

  @Override
  public Mono<Void> onAuthenticationSuccess(
      WebFilterExchange exchange, Authentication authentication) {
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

    KakaoOAuth2User user = KakaoOAuth2User.from(oAuth2User);

    String authCode = UUID.randomUUID().toString();

    return authCodeStore
        .save(authCode, user.kakaoId())
        .then(
            Mono.defer(
                () -> {
                  var response = exchange.getExchange().getResponse();
                  response.setStatusCode(HttpStatus.FOUND);
                  response
                      .getHeaders()
                      .setLocation(
                          UriComponentsBuilder.fromUriString(
                                  authProperties.getLoginSuccessRedirectUri())
                              .queryParam("authCode", authCode)
                              .build()
                              .toUri());
                  return response.setComplete();
                }));
  }
}
