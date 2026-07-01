package me.hakyuwon.ecostep.controller;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.dto.TreeResponseDto;
import me.hakyuwon.ecostep.service.TreeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TreeController {
    private final TreeService treeService;

    // 물 주기
    @PostMapping("/api/tree/water")
    public ResponseEntity<TreeResponseDto> useWater(@AuthenticationPrincipal UserDetails userDetails) {
        TreeResponseDto updatedTree = treeService.useWater(userDetails.getUsername());
        return ResponseEntity.ok(updatedTree);
    }

    // 비료 주기
    @PostMapping("/api/tree/fertilizer")
    public ResponseEntity<TreeResponseDto> useFertilizer(@AuthenticationPrincipal UserDetails userDetails) {
        TreeResponseDto updatedTree = treeService.useFertilizer(userDetails.getUsername());
        return ResponseEntity.ok(updatedTree);
    }
}