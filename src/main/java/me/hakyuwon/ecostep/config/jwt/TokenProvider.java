package me.hakyuwon.ecostep.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class TokenProvider {

    private final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 60 * 6; // 6시간
    private final long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24 * 20; // 30일

    private Key key;

    public TokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰 생성
    public String createToken(String email){
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+ACCESS_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS256,key)
                .compact();
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS256,key)
                .compact();
    }

    // 토큰에서 user의 email 추출
    public String extractUserEmail(String token) {
        return getClaims(token).getSubject();  // subject에 저장된 userId 반환
    }

    // 토큰 검증 메서드
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            Claims claims = getClaims(token); // Claims 파싱 시 예외 발생하면 유효하지 않은 토큰
            String email = claims.getSubject(); // jwt에서 email 추출
            return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // JWT 만료 여부 확인
    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    // JWT에서 Claims 추출
    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateAccessTokenFromRefresh(String refreshToken) {
        // 리프레시 토큰에서 사용자 정보 추출
        String email = extractUserEmail(refreshToken);

        // 새로운 액세스 토큰 생성
        return createToken(email);
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {
            // 리프레시 토큰 검증
            Jwts.parser().setSigningKey(key).parseClaimsJws(refreshToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
