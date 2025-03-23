package me.hakyuwon.ecostep.controller;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.config.jwt.TokenProvider;
import me.hakyuwon.ecostep.domain.Tree;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.dto.RankingResponse;
import me.hakyuwon.ecostep.repository.TreeRepository;
import me.hakyuwon.ecostep.repository.UserRepository;
import me.hakyuwon.ecostep.service.RankingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RankingController {
    private final RankingService rankingService;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final TreeRepository treeRepository;

    @GetMapping("/api/rankings")
    public List<RankingResponse> getRankings(@RequestHeader("Authorization") String token) {
        try{
            System.out.println(">>>>> 랭킹 API 호출됨");

            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Claims claims = tokenProvider.getClaims(token); // 토큰에서 payload 추출
            String email = claims.getSubject();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. 1"));

            List<RankingResponse> rankings = rankingService.getTop10Rankings();
            return ResponseEntity.ok(rankings).getBody();
        }
        catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
