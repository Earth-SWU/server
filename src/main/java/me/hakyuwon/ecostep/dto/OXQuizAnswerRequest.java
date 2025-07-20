package me.hakyuwon.ecostep.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class OXQuizAnswerRequest {
    // 정답 제출 요청 DTO
    private String userAnswer; // O, X
}
