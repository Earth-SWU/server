package me.hakyuwon.ecostep.config.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.repository.UserRepository;
import org.hibernate.service.UnknownServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    @Autowired
    public JwtAuthenticationFilter(TokenProvider tokenProvider, UserDetailsService userDetailsService, UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // HTTP 요청 헤더에서 Authorization 값 가져오기
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer " 이후의 토큰 값만 추출
        String token = authHeader.substring(7);
        String email = tokenProvider.extractUserEmail(token);

        /*try {
            email = tokenProvider.extractUserEmail(token); // JWT에서 email 추출
        } catch (ExpiredJwtException e) {
            // 만료된 토큰 처리 로직
            String refreshToken = request.getHeader("Refresh-Token");  // 리프레시 토큰을 헤더에서 가져옴
            if (refreshToken != null && tokenProvider.validateRefreshToken(refreshToken)) {

                String newAccessToken = tokenProvider.generateAccessTokenFromRefresh(refreshToken);
                response.setHeader("Authorization", "Bearer " + newAccessToken);

                token = newAccessToken;
                email = tokenProvider.extractUserEmail(token);

            }

            else {
                // 리프레시 토큰이 없거나 유효하지 않은 경우, 로그인이 필요함을 알려줌
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token expired and refresh token is invalid or missing.");
                return;
            }
        }*/

        // SecurityContext에 인증 정보가 없고, email이 존재하는 경우
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
           UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (tokenProvider.validateToken(token, userDetails)) { // 토큰 검증
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication); // 인증 정보 저장
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/api/users/signup") || path.startsWith("/api/users/login");
    }
}
