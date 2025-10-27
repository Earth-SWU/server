package me.hakyuwon.ecostep.controller;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.config.jwt.TokenProvider;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.dto.*;
import me.hakyuwon.ecostep.exception.CustomException;
import me.hakyuwon.ecostep.exception.ErrorCode;
import me.hakyuwon.ecostep.repository.UserRepository;
import me.hakyuwon.ecostep.service.MissionService;
import me.hakyuwon.ecostep.service.PredictModelService;
import me.hakyuwon.ecostep.service.ReceiptOCR;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/missions")
public class MissionController {
    private final MissionService missionService;
    private final ReceiptOCR ocrService;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PredictModelService predictModelService;

    // 미션 목록 조회
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, List<MissionDto>>> getMission(@PathVariable Long userId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<MissionDto> missionDtos = missionService.getAllMissions(userId);
        Map<String, List<MissionDto>> response = new HashMap<>();
        response.put("missions", missionDtos);
        return ResponseEntity.ok(response);
    }

    // 미션 완료
    @PostMapping("/complete")
    public ResponseEntity<MissionDto.MissionBadgeResponseDto> completeMission(@RequestBody UserMissionDto userMissionDto, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        MissionDto.MissionBadgeResponseDto response = missionService.completeMission(user.getId(), userMissionDto.getMissionId());
        return ResponseEntity.ok(response);
    }

    // 퀴즈 틀렸을 때
    @PostMapping("/quiz/fail")
    public ResponseEntity<String> failQuizMission(@RequestParam Long missionId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String message = missionService.failMission(user.getId(), missionId);
        return ResponseEntity.ok(message);
    }

    // 출석 체크 미션
    @GetMapping("/attend")
    public ResponseEntity<String> getAttendanceStatus(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String result = missionService.checkAttendance(user.getId());
        return ResponseEntity.ok(result);
    }

    // 영수증 인증 미션
    @PostMapping("/receipt")
    public ResponseEntity<?> analyzeReceipt(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            String result = ocrService.analyzeReceipt(file);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            throw new CustomException(ErrorCode.FILE_PROCESSING_FAILED);
        }
    }

    // 텀블러 사용 미션
    @PostMapping("/tumbler")
    @ResponseBody
    public String checkMission(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        try {
            boolean success = predictModelService.isMissionSuccessful(file);
            return success ? "미션 성공!" : "미션 실패";
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_PROCESSING_FAILED);
        }
    }

    // 3000보 이상 걷기
    @PostMapping("/walk")
    public ResponseEntity<String> walkMission(@RequestBody StepDataDto stepDto, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String result = missionService.checkSteps(stepDto);
        return ResponseEntity.ok(result);
    }

    // 각 뱃지 지급 api
    @PostMapping("/badge/check")
    public ResponseEntity<String> checkBadge(@RequestBody UserMissionDto userMissionDto, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            return missionService.checkBadge(user.getId(), userMissionDto.getMissionId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // 계단 오르기 미션
    @PostMapping("/stairs/complete")
    public ResponseEntity<String> checkQr(@RequestBody QrMissionDto dto, @AuthenticationPrincipal UserDetails userDetails ){
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Long missionId = dto.getMissionId();
        String request = dto.getQrString();
        String response = missionService.checkStairs(user.getId(), missionId, request);
        return ResponseEntity.ok(response);
    }

}
