package me.hakyuwon.ecostep.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long userId; // 사용자 ID (숫자형 기본키)
    private String email; // 사용자 이메일 (기본 아이디)
    private String password; // 사용자 패스워드
    private Boolean reward; // 리워드 상태
    private String phoneNumber;
    private TreeDto tree;


    @Builder
    @Getter
    public static class UserSignupResponseDto {
        private Long userId;
        private String email;
    }

    @Builder
    @Getter
    public static class UserLoginResponseDto {
        private Long userId;
        private String email;
        private String accessToken;
        private String refreshToken;
    }
}
