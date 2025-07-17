package me.hakyuwon.ecostep.controller;

import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.config.jwt.TokenProvider;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.dto.*;
import me.hakyuwon.ecostep.exception.CustomException;
import me.hakyuwon.ecostep.exception.ErrorCode;
import me.hakyuwon.ecostep.repository.UserRepository;
import me.hakyuwon.ecostep.service.MailService;
import me.hakyuwon.ecostep.service.TreeService;
import me.hakyuwon.ecostep.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class UserController {

    @Autowired
    private final UserService userService;
    private final TreeService treeService;
    private final TokenProvider tokenProvider;
    private final MailService mailService;
    private final UserRepository userRepository;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    // 회원가입
    @PostMapping("/api/users/signup")
    public ResponseEntity<Object> signup(@RequestBody @Valid UserSignUpRequest request){
        return ResponseEntity.ok().body(userService.signUp(request));
    }

    // 로그인
    @PostMapping("/api/users/login")
    public ResponseEntity<Object> login(@RequestBody UserLoginRequest request){
        return ResponseEntity.ok(userService.logIn(request));
    }

    // 회원 탈퇴
    @DeleteMapping("/api/users/delete")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        userService.deleteUser(email);
        return ResponseEntity.ok("회원 탈퇴 성공");
    }

    // 메인 화면
    @GetMapping("/api/home/{userId}")
    public ResponseEntity<TreeResponseDto> getHome(@PathVariable Long userId, @AuthenticationPrincipal UserDetails userDetails){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (!user.getEmail().equals(userDetails.getUsername())) {
                throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
        TreeResponseDto treeInfo = treeService.getTreeInfo(userId);
        return ResponseEntity.ok(treeInfo);
    }

    // 인증 메일 전송
    @PostMapping("/api/email-check")
    public ResponseEntity<String> emailCheck(@RequestBody EmailDto.EmailRequestDto dto) {
        try {
            mailService.sendSimpleMessage(dto);
            return ResponseEntity.ok("인증번호가 발송되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메일 발송 실패");
        }
    }

    // 인증 번호 확인
    @PostMapping("/api/verify-code")
    public ResponseEntity<String> verifyCode(@RequestBody EmailDto.VerifyCodeRequestDto dto) {
        if (mailService.verifyCode(dto)) {
            return ResponseEntity.ok("인증 성공");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 실패");
        }
    }

    // 리프레시 토큰 -> 액세스 토큰 갱신
    @PostMapping("/api/refresh")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestBody Map<String, String> refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.get("refreshToken");

        if (refreshToken == null || !tokenProvider.validateRefreshToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // 리프레시 토큰으로 새로운 액세스 토큰 발급
        String newAccessToken = tokenProvider.generateAccessTokenFromRefresh(refreshToken);
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", newAccessToken);

        return ResponseEntity.ok(response);
    }

    // 회원가입 이후, 첫 뱃지 지급 api
    @PostMapping("/api/beginner/{userId}")
    public ResponseEntity<String> assignBeginnerBadge(@PathVariable Long userId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (!user.getEmail().equals(userDetails.getUsername())) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        userService.firstBadge(userId);
        return ResponseEntity.ok("에코스텝 비기너");
    }

    /* 비밀번호 재설정
    @PostMapping("/api/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String code, @RequestParam String newPassword) {
        if (mailService.resetPassword(email, code, newPassword)) {
            return ResponseEntity.ok("비밀번호가 변경되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증번호가 올바르지 않습니다.");
        }
    }*/

}
