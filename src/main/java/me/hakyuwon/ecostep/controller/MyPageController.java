package me.hakyuwon.ecostep.controller;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.config.jwt.TokenProvider;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.dto.BadgeDto;
import me.hakyuwon.ecostep.dto.MyPageDto;
import me.hakyuwon.ecostep.dto.PredictDto;
import me.hakyuwon.ecostep.dto.ProfileDto;
import me.hakyuwon.ecostep.repository.UserRepository;
import me.hakyuwon.ecostep.service.BadgeService;
import me.hakyuwon.ecostep.service.MyPageService;
import me.hakyuwon.ecostep.service.PredictModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class MyPageController {
    @Autowired
    private final MyPageService myPageService;
    private final BadgeService badgeService;
    private final PredictModelService predictModelService;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    // mypage (프로필, 탄소감축량, 미션 통계)
    @GetMapping("/api/me/{userId}")
    public ResponseEntity<MyPageDto> getMyPage(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
        try{
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Claims claims = tokenProvider.getClaims(token); // 토큰에서 payload 추출
            String email = claims.getSubject();

            User user1 = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. 1"));
            User user2 = userRepository.findByEmail(email)
                    .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 사용자입니다. 2"));

            // 요청된 userId와 인증된 userId가 일치하는지 검증
            if (!user1.getId().equals(user2.getId())) {
                throw new SecurityException("잘못된 접근입니다.");
            }
            MyPageDto myPageDto = myPageService.getMyPage(userId);
            return ResponseEntity.ok(myPageDto);}
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
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

    // 예측 모델
    @PostMapping("/api/me/predict")
    public ResponseEntity<PredictDto.PredictResponse> getPrediction(@RequestBody PredictDto.PredictRequest request) {
        PredictDto.PredictResponse predictionResult = predictModelService.callPredictAPI(request.getInputData());
        return ResponseEntity.ok(predictionResult);
    }
}
