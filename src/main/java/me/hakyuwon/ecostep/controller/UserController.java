package me.hakyuwon.ecostep.controller;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.dto.UserDto;
import me.hakyuwon.ecostep.dto.UserSignUpRequest;
import me.hakyuwon.ecostep.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserService userService;

    // 회원가입
    @PostMapping("/api/users/signup")
    public ResponseEntity<UserDto.UserSignupResponseDto> signup(@RequestBody UserSignUpRequest request){
        UserDto.UserSignupResponseDto signupResponse = userService.signUp(request);
        // 회원가입 후 로그인 페이지로
        return ResponseEntity.ok().body(signupResponse);
    }

    // 로그인
    @PostMapping("/api/users/login")
    public ResponseEntity<UserDto.UserLoginResponseDto> login(@RequestBody UserDto.UserLoginRequestDto request){
        UserDto.UserLoginResponseDto loginResponse = userService.logIn(request);
        return ResponseEntity.ok().body(loginResponse);
    }

    // 로그아웃
    @PostMapping("/api/users/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        userService.logout(token);
        return ResponseEntity.ok().build(); // HTTP 200 응답 반환
    }


    // 회원 탈퇴
    @DeleteMapping("/api/users/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // 메인 화면
    @GetMapping("/api/home")
    public String getHome(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();

        return "User Email: " + email;
    }
}
