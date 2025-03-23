package me.hakyuwon.ecostep.controller;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.config.jwt.TokenProvider;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.dto.MissionDto;
import me.hakyuwon.ecostep.dto.StepDataDto;
import me.hakyuwon.ecostep.dto.UserMissionDto;
import me.hakyuwon.ecostep.repository.MissionRepository;
import me.hakyuwon.ecostep.repository.UserMissionRepository;
import me.hakyuwon.ecostep.repository.UserRepository;
import me.hakyuwon.ecostep.service.MissionService;
import me.hakyuwon.ecostep.service.ReceiptOCR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.token.TokenService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/missions")
public class MissionController {
    @Autowired
    private final MissionService missionService;
    private final ReceiptOCR ocrService;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final MissionRepository missionRepository;

    // 미션 목록 조회
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, List<MissionDto>>> getMission(@PathVariable Long userId) {
        List<MissionDto> missionDtos = missionService.getAllMissions(userId);
        Map<String, List<MissionDto>> response = new HashMap<>();
        response.put("missions", missionDtos);
        return ResponseEntity.ok(response);
    }
    // 미션 완료
    @PostMapping("/complete")
    public ResponseEntity<String> completeMission(@RequestBody UserMissionDto userMissionDto) {
        String result = missionService.completeMission(userMissionDto.getUserId(), userMissionDto.getMissionId());
        return ResponseEntity.ok(result);
    }

    // 출석 체크 미션
    @GetMapping("/attend/{userId}")
    public ResponseEntity<String> getAttendanceStatus(@PathVariable Long userId) {
        String result = missionService.checkAttendance(userId);
        return ResponseEntity.ok(result);
    }

    // 영수증 인증 미션
    @PostMapping("/receipt")
    public ResponseEntity<?> analyzeReceipt(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Claims claims = tokenProvider.getClaims(token); // 토큰에서 payload 추출
            String email = claims.getSubject();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 사용자입니다."));

            String result = ocrService.analyzeReceipt(file);
            missionService.completeMission(user.getId(),2L);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 텀블러 사용 미션


    // 3000보 이상 걷기
    @PostMapping("/walk")
    public ResponseEntity<String> walkMission(@RequestBody StepDataDto stepDto) {
        String result = missionService.checkSteps(stepDto);
        return ResponseEntity.ok(result);
    }

    // 각 뱃지 지급 api

    // 친환경 물품 구매

    // 친환경 기사 읽기
}
