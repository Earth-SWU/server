package me.hakyuwon.ecostep.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

// 정답 제출 응답 DTO
@Getter
@Setter
@RequiredArgsConstructor
public class OxQuizAnswerResponse {
    private boolean isCorrect;
    private String explanation;
}
