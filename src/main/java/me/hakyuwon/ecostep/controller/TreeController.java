package me.hakyuwon.ecostep.controller;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.config.jwt.TokenProvider;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.dto.TreeDto;
import me.hakyuwon.ecostep.dto.TreeResponseDto;
import me.hakyuwon.ecostep.repository.UserRepository;
import me.hakyuwon.ecostep.service.TreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TreeController {
    @Autowired
    private final TreeService treeService;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    // 나무 이름 짓기
    @PutMapping("/api/tree/name/{userId}")
    public ResponseEntity<TreeResponseDto> name(@PathVariable Long userId, @RequestBody TreeDto.TreeRequestDto requestDto, @RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Claims claims = tokenProvider.getClaims(token); // 토큰에서 payload 추출
            String email = claims.getSubject();

            User user1 = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. 1"));
            User user2 = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. 2"));

            // 요청된 userId와 인증된 userId가 일치하는지 검증
            if (!user1.getId().equals(user2.getId())) {
                throw new SecurityException("잘못된 접근입니다.");
            }

        String treeName = requestDto.getTreeName();
        TreeResponseDto updatedTree = treeService.setTreeName(userId, treeName);
        return ResponseEntity.ok(updatedTree);}
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // 물 주기
    @PostMapping("/api/tree/water/{userId}")
    public ResponseEntity<TreeResponseDto> useWater(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Claims claims = tokenProvider.getClaims(token); // 토큰에서 payload 추출
            String email = claims.getSubject();

            User user1 = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. 1"));
            User user2 = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. 2"));

            // 요청된 userId와 인증된 userId가 일치하는지 검증
            if (!user1.getId().equals(user2.getId())) {
                throw new SecurityException("잘못된 접근입니다.");
            }
            TreeResponseDto updatedTree = treeService.useWater(userId);
            return ResponseEntity.ok(updatedTree);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    /* 비료 주기
    @PostMapping("/api/tree/fertilizer/{userId}")
    public ResponseEntity<TreeResponseDto> useFertilizer(@PathVariable Long userId) {
        TreeResponseDto updatedTree = treeService.useFertilizer(userId);
        return ResponseEntity.ok(updatedTree);
    }*/
}