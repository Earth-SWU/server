package me.hakyuwon.ecostep.controller;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.dto.UserDto;
import me.hakyuwon.ecostep.dto.UserSignUpRequest;
import me.hakyuwon.ecostep.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserService userService;

    // 회원가입
    @PostMapping("/api/signup")
    public ResponseEntity<UserDto.UserSignupResponseDto> signup(@RequestBody UserSignUpRequest request){
        UserDto.UserSignupResponseDto signupResponse = userService.signUp(request);
        // 회원가입 후 로그인 페이지로
        return ResponseEntity.ok().body(signupResponse);
    }

    //로그인
    @PostMapping("/api/users/signin")
    public ResponseEntity<UserDto.UserLoginResponseDto> login(@RequestBody UserDto.UserLoginRequestDto request){
        UserDto.UserLoginResponseDto loginResponse = userService.logIn(request);
        return ResponseEntity.ok().body(loginResponse);
    }

}
