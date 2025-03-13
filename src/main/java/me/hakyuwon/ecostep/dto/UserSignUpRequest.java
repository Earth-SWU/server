package me.hakyuwon.ecostep.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.hakyuwon.ecostep.domain.User;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignUpRequest {
    @NotBlank(message="이메일을 입력해 주세요")
    @Email(message = "올바른 주소를 입력하세요")
    private String email;

    @NotBlank(message="비밀번호를 입력해 주세요")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @NotBlank
    private String confirmPassword;

    @NotBlank(message="번호를 입력해 주세요")
    private String phoneNumber;

    public User toEntity(){
        return User.builder()
                .email(this.email)
                .password(this.password)
                .phoneNumber(this.phoneNumber)
                .build();
    }
}
