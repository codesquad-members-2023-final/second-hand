package com.secondhand.oauth.service;

import com.secondhand.domain.member.Member;
import com.secondhand.service.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class JwtService {

    public static final String SUBJECT_NAME = "login_member";
    public static final String MEMBER_ID = "memberId";
    private String secret;
    private String refreshSecretKey;

    public JwtService(@Value("${JWT_SECRET_KEY}") String secret,
                      @Value("${JWT_SECRET_REFRESH_KEY}") String refreshSecretKey) {
        this.secret = secret;
        this.refreshSecretKey = refreshSecretKey;
    }

    public Token createToken(Member member) {

        String accessToken = Jwts.builder()
                .setSubject(SUBJECT_NAME)
                .claim(MEMBER_ID, member.getId()) //페이로드,헤더는 자동설정
                .setExpiration(new Date((new Date()).getTime() + 24 * 60 * 60 * 1000)) // 토큰의 만료일을 설정 : 현재 10일
                .signWith(SignatureAlgorithm.HS256, secret) // HS256 알고리즘과 시크릿 키를 사용하여 서명
                .compact();

        String refreshToken = Jwts.builder()
                .claim(MEMBER_ID, member.getId()) //페이로드,헤더는 자동설정
                .setIssuedAt(new Date()) // 토큰 발행 시간 정보
                .setExpiration(new Date((new Date()).getTime() + 24 * 60 * 60 * 1000 * 10)) // 토큰의 만료일을 설정 : 현재 10일
                .signWith(SignatureAlgorithm.HS256, refreshSecretKey)  // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();

        return Token.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }


    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token);

            if (claimsJws.getBody().getExpiration().before(new Date())) {
                return false;
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public Long getSubject(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        log.debug("claims = {}", claims);
        log.debug("MEMBER_ID = {}", claims.get(MEMBER_ID, Long.class));
        return claims.get(MEMBER_ID, Long.class);
    }
}
