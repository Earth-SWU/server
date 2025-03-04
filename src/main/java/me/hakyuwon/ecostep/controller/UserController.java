package me.hakyuwon.ecostep.controller;

import io.jsonwebtoken.Claims;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.config.jwt.TokenProvider;
import me.hakyuwon.ecostep.dto.UserDto;
import me.hakyuwon.ecostep.dto.UserLoginRequest;
import me.hakyuwon.ecostep.dto.UserSignUpRequest;
import me.hakyuwon.ecostep.service.MailService;
import me.hakyuwon.ecostep.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final MailService mailService;

    // 그냥 처음
    @GetMapping("/")
    public String home() {
        return "redirect:/index.html";
    }

    // 회원가입
    @PostMapping("/api/users/signup")
    public ResponseEntity<UserDto.UserSignupResponseDto> signup(@RequestBody UserSignUpRequest request){
        UserDto.UserSignupResponseDto signupResponse = userService.signUp(request);
        // 회원가입 후 로그인 페이지로
        return ResponseEntity.ok().body(signupResponse);
    }

    // 로그인
    @PostMapping("/api/users/login")
    public ResponseEntity<UserDto.UserLoginResponseDto> login(@RequestBody UserLoginRequest request){
        UserDto.UserLoginResponseDto loginResponse = userService.logIn(request);
        return ResponseEntity.ok().body(loginResponse);
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
    @GetMapping("/api/home")
    public String getHome(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();

        return "User Email: " + email;
    }

    // 인증 메일 전송
    @ResponseBody
    @PostMapping("/api/emailCheck")
    public String emailCheck(@RequestBody UserDto.MailDto mailDto) throws MessagingException, UnsupportedEncodingException {
        String authCode = mailService.sendSimpleMessage(mailDto.getEmail());
        return authCode;
    }

}
