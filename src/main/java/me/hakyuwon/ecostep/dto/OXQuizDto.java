package me.hakyuwon.ecostep.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class OXQuizDto {
    private String question;
    private List<String> options; // O, X
}
