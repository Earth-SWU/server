package me.hakyuwon.ecostep.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.enums.UserStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignUpRequest {
    @NotBlank(message="이메일을 입력해 주세요")
    @Email(message = "올바른 주소를 입력하세요")
    private String email;

    @NotBlank(message="비밀번호를 입력해 주세요")
    private String password;

    @NotBlank(message="번호를 입력해 주세요")
    private String phoneNumber;

    public String getEmail() {
        return email;
    }

    public User toEntity(){
        return User.builder()
                .email(this.email)
                .password(this.password)
                .phoneNumber(this.phoneNumber)
                .build();
    }
}
