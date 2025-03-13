package me.hakyuwon.ecostep.controller;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.dto.BadgeDto;
import me.hakyuwon.ecostep.dto.MyPageDto;
import me.hakyuwon.ecostep.dto.ProfileDto;
import me.hakyuwon.ecostep.service.BadgeService;
import me.hakyuwon.ecostep.service.MyPageService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class MyPageController {
    private final MyPageService myPageService;
    private final BadgeService badgeService;

    // mypage (프로필, 탄소감축량, 미션 통계)
    @GetMapping("/api/me/{userId}")
    public ResponseEntity<MyPageDto> getMyPage(@PathVariable Long userId) {
        MyPageDto myPageDto = myPageService.getMyPage(userId);
        return ResponseEntity.ok(myPageDto);
    }

    // 프로필 정보 조회 (뱃지, 미션, 나무 레벨)
    @GetMapping("/api/profile/{userId}")
    public ResponseEntity<ProfileDto> getProfile(@PathVariable Long userId) {
        ProfileDto profileDto = myPageService.getProfile(userId);
        return ResponseEntity.ok(profileDto);
    }

    // 뱃지 목록 조회
    @GetMapping("/api/me/badge/{userId}")
    public ResponseEntity<List<BadgeDto>> getBadge(@PathVariable Long userId) {
        List<BadgeDto> badgeDto = badgeService.getAllBadges(userId);
        return ResponseEntity.ok(badgeDto);
    }
}
