package com.secondhand.domain.login;

import com.secondhand.exception.v2.ErrorMessage;
import com.secondhand.infrastructure.JwtProperties;
import com.secondhand.infrastructure.jwt.JwtTokenProvider;
import com.secondhand.exception.v2.UnAuthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JWT 발급 / 검증 테스트")
class JwtTokenProviderTest {

    private final String secretKey = "2901ujr9021urf0u902hf021y90fh9c210hg093hg091h3g90h30gh901hg09h01";
    private final JwtTokenProvider jwtProvider = new JwtTokenProvider(new JwtProperties(secretKey));

    @DisplayName("회원의 PK가 payload로 주어지면 토큰이 생성되는데 성공한다.")
    @Test
    void whenCreateAccessToken_thenSuccess() {
        // given

        // when
        Token accessToken = jwtProvider.createToken(1L);

        // then
        assertThat(accessToken).isNotNull();
    }

    @DisplayName("유효하지 않은 토큰이 주어지면 토큰을 검증할 때 예외가 던져진다.")
    @Test
    void givenInvalidToken_thenThrowsException() {
        // given
        String invalidToken = "asdf.asdf.asdf";

        // when & then
        assertThatThrownBy(() -> jwtProvider.validateToken(invalidToken))
                .isInstanceOf(UnAuthorizedException.class)
                .extracting("errorMessage").isEqualTo(ErrorMessage.INVALID_TOKEN);
    }

    @DisplayName("만료된 토큰이 주어지면 토큰을 검증할 때 예외가 던져진다.")
    @Test
    void givenExpiredToken_thenThrowsException() {
        // given
        String expiredToken = TokenCreator.createExpiredToken(1L);

        System.out.println("expiredToken = " + expiredToken);
        // when & then
        assertThatThrownBy(() -> jwtProvider.validateToken(expiredToken))
                .isInstanceOf(UnAuthorizedException.class)
                .extracting("errorMessage").isEqualTo(ErrorMessage.EXPIRED_TOKEN);
    }

    @DisplayName("payload가 멤버 PK인 토큰이 주어지면 토큰에서 클레임을 추출하는데 성공한다.")
    @Test
    void givenToken_whenExtractClaims_thenSuccess() {
        // given
        String token = TokenCreator.createToken(1L).getAccessToken();

        // when
        Map<String, Object> claims = jwtProvider.extractClaims(token);


        // then
        assertThat(claims.get("memberId").toString()).isEqualTo("1");
    }

    @DisplayName("로그아웃한 엑세스토큰이 주어지면 에러를 발생시킨다.")
    @Test
    void given_when_then() {
        // given

        // when

        // then
    }
}
