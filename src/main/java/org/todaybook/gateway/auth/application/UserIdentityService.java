package org.todaybook.gateway.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.todaybook.gateway.auth.application.dto.AuthenticatedUser;
import org.todaybook.gateway.auth.application.exception.InternalServerErrorException;
import org.todaybook.gateway.auth.application.exception.UnauthorizedException;
import org.todaybook.gateway.auth.infrastructure.webclient.UserOauthCreateRequest;
import org.todaybook.gateway.auth.infrastructure.webclient.UserServiceClient;
import org.todaybook.gateway.security.oauth.AuthCodePayload;
import reactor.core.publisher.Mono;

/**
 * Auth 플로우에서 "유저 식별/생성"을 담당하는 Application Service입니다.
 *
 * <p>역할:
 *
 * <ul>
 *   <li>OAuth 인증 후 받은 payload(provider/providerUserId)를 이용해 유저를 조회합니다.
 *   <li>유저가 없다면(User service 404 → Mono.empty()) 회원가입을 시도합니다.
 *   <li>최종적으로 토큰 발급에 필요한 형태({@link AuthenticatedUser})로 변환하여 반환합니다.
 * </ul>
 *
 * <p>중요:
 *
 * <ul>
 *   <li>실제 회원가입 정책(기본 Role 부여 등)은 User 서비스의 책임입니다.
 *   <li>이 클래스는 "조회 → 없으면 가입" 오케스트레이션만 수행합니다.
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class UserIdentityService {

  private final UserServiceClient userServiceClient;

  /**
   * OAuth payload를 기반으로 "기존 유저 조회" 또는 "신규 유저 생성"을 수행합니다.
   *
   * <p>흐름:
   *
   * <ol>
   *   <li>provider/providerUserId로 유저 조회
   *   <li>없으면 회원가입 요청(idempotent)
   *   <li>UserSummary → AuthenticatedUser로 변환
   * </ol>
   *
   * <p>이 메서드는 "인증 성공 후" 호출되므로, provider/providerUserId가 비어 있는 것은 서버 내부 버그로 간주합니다.
   */
  public Mono<AuthenticatedUser> resolveOrCreateFromOauth(AuthCodePayload payload) {
    if (payload == null) {
      return Mono.error(new InternalServerErrorException("AuthCodePayload is null"));
    }
    if (isBlank(payload.provider()) || isBlank(payload.providerUserId())) {
      return Mono.error(
          new InternalServerErrorException(
              "Invalid OAuth payload: provider/providerUserId is blank"));
    }

    String provider = payload.provider();
    String providerUserId = payload.providerUserId();

    return userServiceClient
        .findByOauth(provider, providerUserId)
        .map(AuthenticatedUser::from)
        .switchIfEmpty(
            userServiceClient
                .createOauthUser(
                    new UserOauthCreateRequest(provider, providerUserId, payload.nickname()))
                .map(AuthenticatedUser::from));
  }

  /**
   * userId로 유저를 조회하여 {@link AuthenticatedUser}로 변환합니다.
   *
   * <p>주로 refresh 플로우에서 rotate 결과의 userId로 최신 roles/status/nickname을 반영하기 위해 사용됩니다.
   *
   * <p>User 서비스에서 404가 내려오면(= Mono.empty()) 인증 불가로 보는 것이 일반적이므로, empty를 그대로 흘리지 않고 Unauthorized로
   * 변환합니다.
   */
  public Mono<AuthenticatedUser> loadAuthenticatedUser(String userId) {
    if (isBlank(userId)) {
      return Mono.error(new InternalServerErrorException("userId is blank"));
    }

    return userServiceClient
        .findByUserId(userId)
        .switchIfEmpty(Mono.error(new UnauthorizedException("USER_NOT_FOUND")))
        .map(AuthenticatedUser::from);
  }

  private boolean isBlank(String value) {
    return !StringUtils.hasText(value);
  }
}
