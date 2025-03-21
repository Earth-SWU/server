package me.hakyuwon.ecostep.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
public class EmailDto {
    @Getter
    @Setter
    public static class EmailRequestDto {
        private String email;
    }

    @Getter
    @Setter
    public static class VerifyCodeRequestDto {
        private String email;
        private String code;
    }
}
