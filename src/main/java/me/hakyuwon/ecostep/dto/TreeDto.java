package me.hakyuwon.ecostep.dto;

import lombok.*;
import me.hakyuwon.ecostep.domain.Tree;
import me.hakyuwon.ecostep.enums.TreeType;

import java.math.BigDecimal;

public class TreeDto {
    private Long id;
    private String treeName;
    private int level;
    private int growth;
    private int water;
    private int fertilizer;

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TreeRequestDto {
        private String treeName;
    }

    public TreeDto(String treeName) {
        this.treeName = treeName;
    }
}
