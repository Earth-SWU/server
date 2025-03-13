package me.hakyuwon.ecostep.controller;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.dto.TreeDto;
import me.hakyuwon.ecostep.dto.TreeResponseDto;
import me.hakyuwon.ecostep.repository.UserRepository;
import me.hakyuwon.ecostep.service.TreeService;
import me.hakyuwon.ecostep.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TreeController {
    private final TreeService treeService;
    private final UserRepository userRepository;

    // 나무 이름 짓기
    @PutMapping("/api/tree/name")
    public ResponseEntity<TreeResponseDto> name(@AuthenticationPrincipal UserDetails userDetails, @RequestBody TreeDto.TreeRequestDto requestDto) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);  // 인증되지 않은 사용자 처리
        }
        String email = userDetails.getUsername(); // 이메일 주소를 가져오기

        // userRepository에서 사용자 찾기
        Optional<User> optionalUser = userRepository.findByEmail(email);

        // 사용자 존재 여부 확인 (Optional이기 때문에)
        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);  // 이메일로 사용자 찾을 수 없을 경우 처리
        }

        // 사용자 ID 가져오기
        User user = optionalUser.get();
        Long userId = user.getId();
        String treeName = requestDto.getTreeName(); // requestBody에서 treeName 가져오기

        TreeResponseDto updatedTree = treeService.setTreeName(userId, treeName);
        return ResponseEntity.ok(updatedTree);
    }

    // 물 주기
    @PostMapping("/api/tree/water/{userId}")
    public ResponseEntity<TreeResponseDto> useWater(@PathVariable Long userId) {
        TreeResponseDto updatedTree = treeService.useWater(userId);
        return ResponseEntity.ok(updatedTree);
    }

    // 비료 주기
    @PostMapping("/api/tree/fertilizer/{userId}")
    public ResponseEntity<TreeResponseDto> useFertilizer(@PathVariable Long userId) {
        TreeResponseDto updatedTree = treeService.useFertilizer(userId);
        return ResponseEntity.ok(updatedTree);
    }
}