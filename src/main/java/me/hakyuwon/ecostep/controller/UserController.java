package me.hakyuwon.ecostep.controller;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.config.jwt.TokenProvider;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.dto.*;
import me.hakyuwon.ecostep.repository.UserRepository;
import me.hakyuwon.ecostep.service.MailService;
import me.hakyuwon.ecostep.service.TreeService;
import me.hakyuwon.ecostep.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Object> signup(@RequestBody UserSignUpRequest request){
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("이미 등록된 이메일입니다.");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
        }
        UserDto.UserSignupResponseDto signupResponse = userService.signUp(request);
        return ResponseEntity.ok().body(signupResponse);
    }

    // 로그인
    @PostMapping("/api/users/login")
    public ResponseEntity<Object> login(@RequestBody UserLoginRequest request){
        try {
            UserDto.UserLoginResponseDto loginResponse = userService.logIn(request);
            return ResponseEntity.ok(loginResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // 회원 탈퇴
    @DeleteMapping("/api/users/delete")
    public ResponseEntity<String> deleteUser(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7); // Bearer을 제거하고 token만 남겨두는 과정
        }

        Claims claims = tokenProvider.getClaims(token); // 토큰에서 payload 추출
        String email = claims.getSubject(); // payload의 sub인 email을 추출

        userService.deleteUser(email); // 그 이메일로 delete 실행
        return ResponseEntity.ok("회원 탈퇴 성공");
    }

    // 메인 화면
    @GetMapping("/api/home/{userId}")
    public ResponseEntity<TreeResponseDto> getHome(@PathVariable Long userId, @RequestHeader("Authorization") String token){
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
        // 트리 정보 가져오기
        TreeResponseDto treeInfo = treeService.getTreeInfo(userId);
        return ResponseEntity.ok(treeInfo);}
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "Invalid or expired refresh token"));
        }

        // 리프레시 토큰으로 새로운 액세스 토큰 발급
        String newAccessToken = tokenProvider.generateAccessTokenFromRefresh(refreshToken);
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", newAccessToken);

        return ResponseEntity.ok(response);
    }

    // 회원가입 이후, 첫 뱃지 지급 api
    @PostMapping("/api/beginner/{userId}")
    public ResponseEntity<String> assignBeginnerBadge(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
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

        userService.firstBadge(userId);
        return ResponseEntity.ok("에코스텝 비기너");}
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
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
