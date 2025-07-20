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

    private final String question = "텀블러 사용은 일회용 컵보다 탄소배출을 줄이는 데 도움이 된다.";
    private final String correctAnswer = "O";
    private final String explanation = "텀블러는 반복 사용이 가능하여 일회용 컵보다 훨씬 적은 탄소를 배출합니다.";

    // 미션 목록 조회
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, List<MissionDto>>> getMission(@PathVariable Long userId, @AuthenticationPrincipal UserDetails userDetails) {
        // TODO: userId를 파라미터로 받을 필요 없음 (전체 로직 적용)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (!user.getEmail().equals(userDetails.getUsername())) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

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

    // 출석 체크 미션
    @GetMapping("/attend/{userId}")
    public ResponseEntity<String> getAttendanceStatus(@PathVariable Long userId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (!user.getEmail().equals(userDetails.getUsername())) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
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

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 텀블러 사용 미션
    @PostMapping("/tumbler")
    @ResponseBody
    public String checkMission(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal UserDetails userDetails) throws IOException {
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
        String result = missionService.checkSteps(stepDto);
        return ResponseEntity.ok(result);
    }

    // 각 뱃지 지급 api
    @PostMapping("/badge/check")
    public ResponseEntity<String> checkBadge(@RequestBody UserMissionDto userMissionDto, @RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Claims claims = tokenProvider.getClaims(token); // 토큰에서 payload 추출
            String email = claims.getSubject();

            User user1 = userRepository.findByEmail(email)
                    .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 사용자입니다."));
            User user2 = userRepository.findById(userMissionDto.getUserId())
                    .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 사용자입니다. 2"));

            // 요청된 userId와 인증된 userId가 일치하는지 검증
            if (!user1.getId().equals(user2.getId())) {
                throw new SecurityException("잘못된 접근입니다.");
            }
            return missionService.checkBadge(userMissionDto.getUserId(), userMissionDto.getMissionId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // ox 퀴즈 보여주기
    @GetMapping("/quiz")
    public ResponseEntity<OXQuizDto> getQuiz(@AuthenticationPrincipal UserDetails userDetails) {
        OXQuizDto quiz = new OXQuizDto();
        quiz.setQuestion(question);
        quiz.setOptions(List.of("O"));
        return ResponseEntity.ok(quiz);
    }

    // ox 퀴즈 정답 제출
    @PostMapping("/quiz/answer")
    public ResponseEntity<OxQuizAnswerResponse> submitQuiz(@RequestBody OXQuizAnswerRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        boolean isCorrect = correctAnswer.equalsIgnoreCase(request.getUserAnswer());

        OxQuizAnswerResponse response = new OxQuizAnswerResponse();
        response.setCorrect(isCorrect);
        response.setExplanation(explanation);
        return ResponseEntity.ok(response);
    }

}
