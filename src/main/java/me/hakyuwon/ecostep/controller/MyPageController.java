package me.hakyuwon.ecostep.controller;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.dto.MyPageDto;
import me.hakyuwon.ecostep.dto.ProfileDto;
import me.hakyuwon.ecostep.service.MyPageService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Controller
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping("/api/me/{userId}")
    public ResponseEntity<MyPageDto> getMyPage(@PathVariable Long userId) {
        MyPageDto myPageDto = myPageService.getMyPage(userId);
        return ResponseEntity.ok(myPageDto);
    }

    @GetMapping("/api/profile/{userId}")
    public ResponseEntity<ProfileDto> getProfile(@PathVariable Long userId) {
        ProfileDto profileDto = myPageService.getProfile(userId);
        return ResponseEntity.ok(profileDto);
    }
}
