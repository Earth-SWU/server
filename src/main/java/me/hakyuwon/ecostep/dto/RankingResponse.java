package me.hakyuwon.ecostep.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RankingResponse {
    private int rank;
    private Long userId;
    private int treeLevel;
    private double growth;
}
