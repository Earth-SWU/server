package me.hakyuwon.ecostep.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
public class EmailDto {
    @Getter
    @Setter
    public class EmailRequestDto {
        private String email;
    }

    @Getter
    @Setter
    public class VerifyCodeRequestDto {
        private String email;
        private String code;
    }
}
