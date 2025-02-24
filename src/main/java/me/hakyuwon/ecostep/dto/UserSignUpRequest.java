package me.hakyuwon.ecostep.dto;

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
    private String email;
    private String password;
    private String phoneNumber;
    private UserStatus status;

    public String getEmail() {
        return email;
    }

    public User toEntity(){
        return User.builder()
                .email(this.email)
                .password(this.password)
                .phoneNumber(this.phoneNumber)
                .status(this.status)
                .build();
    }
}
