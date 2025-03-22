package me.hakyuwon.ecostep.dto;

import lombok.*;

public class TreeDto {
    private Long id;
    private String treeName;
    private int level;
    private int growth;
    private int water;

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
