package me.hakyuwon.springbootdeveloper.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserDto {
    private Long userId; // 사용자 ID (숫자형 기본키)
    private String email; // 사용자 이메일 (기본 아이디)
    private String password; // 사용자 패스워드
    private Boolean reward; // 리워드 상태
    private TreeDto tree;

    @Getter
    public static class UserSignupRequestDto {
        private String email;
        private String password;
        private String treename;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSignupResponseDto {
        private String email;
        private String treename;
    }

    @Getter
    public static class UserLoginRequestDto {
        private String email;
        private String password;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserLoginResponseDto {
        private String email;
        // private JwtToken token;
    }
}
