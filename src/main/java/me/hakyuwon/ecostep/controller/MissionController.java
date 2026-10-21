package me.hakyuwon.ecostep.controller;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.dto.*;
import me.hakyuwon.ecostep.exception.CustomException;
import me.hakyuwon.ecostep.exception.ErrorCode;
import me.hakyuwon.ecostep.repository.UserRepository;
import me.hakyuwon.ecostep.service.MissionService;
import me.hakyuwon.ecostep.service.PredictModelService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/missions")
public class MissionController {
    private final MissionService missionService;
    private final UserRepository userRepository;
    private final PredictModelService predictModelService;

    // 미션 목록 조회
    @GetMapping()
    public ResponseEntity<Map<String, List<MissionDto>>> getMission(@AuthenticationPrincipal User user) {
        List<MissionDto> missionDtos = missionService.getAllMissions(user.getId());
        Map<String, List<MissionDto>> response = new HashMap<>();
        response.put("missions", missionDtos);
        return ResponseEntity.ok(response);
    }

    // 미션 완료
    @PostMapping("/complete")
    public ResponseEntity<String> completeMission(@RequestBody UserMissionDto userMissionDto, @AuthenticationPrincipal User user) {
        String response = missionService.completeMission(user.getId(), userMissionDto.getMissionId());
        return ResponseEntity.ok(response);
    }

    // 퀴즈 틀렸을 때
    @PostMapping("/quiz/fail/{missionId}")
    public ResponseEntity<String> failQuizMission(@PathVariable Long missionId, @AuthenticationPrincipal User user) {
        String message = missionService.failMission(user.getId(), missionId);
        return ResponseEntity.ok(message);
    }

    // 출석 체크 미션
    @GetMapping("/attend")
    public ResponseEntity<String> getAttendanceStatus(@AuthenticationPrincipal User user) {
        String result = missionService.checkAttendance(user.getId());
        return ResponseEntity.ok(result);
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

    // 5000보 이상 걷기
    @PostMapping("/walk")
    public ResponseEntity<String> walkMission(@RequestBody StepDataDto stepDto, @AuthenticationPrincipal User user) {
        String result = missionService.checkSteps(stepDto, user);
        return ResponseEntity.ok(result);
    }

    // 배지 지급
    @PostMapping("/badge/check")
    public ResponseEntity<String> checkBadge(@RequestParam Long missionId, @AuthenticationPrincipal User user) {
        String result = missionService.checkBadge(user.getId(), missionId);
        return ResponseEntity.ok(result);
    }

    // 환경 일기 쓰기 미션
    @PostMapping("/eco-diary/complete")
    public ResponseEntity<String> writeDiary(@RequestBody DiaryDto dto, @AuthenticationPrincipal User user){
        Long missionId = dto.getMissionId();
        return ResponseEntity.ok(missionService.keepEcoDiary(user.getId(),missionId, dto));
    }

    // 계단 오르기 미션
    @PostMapping("/stairs/complete")
    public ResponseEntity<String> checkQr(@RequestBody QrMissionDto dto, @AuthenticationPrincipal User user){
        Long missionId = dto.getMissionId();
        String request = dto.getQrString();
        String response = missionService.checkStairs(missionId, request);
        return ResponseEntity.ok(response);
    }
}
