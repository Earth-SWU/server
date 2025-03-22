package me.hakyuwon.ecostep.controller;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.dto.TreeDto;
import me.hakyuwon.ecostep.dto.TreeResponseDto;
import me.hakyuwon.ecostep.repository.UserRepository;
import me.hakyuwon.ecostep.service.TreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TreeController {
    @Autowired
    private final TreeService treeService;

    // 나무 이름 짓기
    @PutMapping("/api/tree/name/{userId}")
    public ResponseEntity<TreeResponseDto> name(@PathVariable Long userId, @RequestBody TreeDto.TreeRequestDto requestDto) {
        String treeName = requestDto.getTreeName();
        TreeResponseDto updatedTree = treeService.setTreeName(userId, treeName);
        return ResponseEntity.ok(updatedTree);
    }

    // 물 주기
    @PostMapping("/api/tree/water/{userId}")
    public ResponseEntity<TreeResponseDto> useWater(@PathVariable Long userId) {
        TreeResponseDto updatedTree = treeService.useWater(userId);
        return ResponseEntity.ok(updatedTree);
    }

    /* 비료 주기
    @PostMapping("/api/tree/fertilizer/{userId}")
    public ResponseEntity<TreeResponseDto> useFertilizer(@PathVariable Long userId) {
        TreeResponseDto updatedTree = treeService.useFertilizer(userId);
        return ResponseEntity.ok(updatedTree);
    }*/
}