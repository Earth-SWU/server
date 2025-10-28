package me.hakyuwon.ecostep.controller;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.dto.TreeResponseDto;
import me.hakyuwon.ecostep.exception.CustomException;
import me.hakyuwon.ecostep.exception.ErrorCode;
import me.hakyuwon.ecostep.repository.UserRepository;
import me.hakyuwon.ecostep.service.TreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TreeController {
    @Autowired
    private final TreeService treeService;
    private final UserRepository userRepository;

    /*
    @PutMapping("/api/tree/name/{userId}")
    public ResponseEntity<TreeResponseDto> name(@PathVariable Long userId, @RequestBody TreeDto.TreeRequestDto requestDto, @RequestHeader("Authorization") String token) {
        String treeName = requestDto.getTreeName();
        TreeResponseDto updatedTree = treeService.setTreeName(userId, treeName);
        return ResponseEntity.ok(updatedTree);}
    }*/

    // 물 주기
    @PostMapping("/api/tree/water")
    public ResponseEntity<TreeResponseDto> useWater(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        TreeResponseDto updatedTree = treeService.useWater(user.getId());
        return ResponseEntity.ok(updatedTree);
    }

    // 비료 주기
    @PostMapping("/api/tree/fertilizer")
    public ResponseEntity<TreeResponseDto> useFertilizer(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        TreeResponseDto updatedTree = treeService.useFertilizer(user.getId());
        return ResponseEntity.ok(updatedTree);
    }
}