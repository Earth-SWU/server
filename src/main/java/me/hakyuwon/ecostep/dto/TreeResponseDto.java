package me.hakyuwon.ecostep.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.hakyuwon.ecostep.domain.Tree;

@Getter
@NoArgsConstructor
public class TreeResponseDto {
    private Long id;
    private String treeName;
    private int level;
    private int growth;
    private int water;
    private int fertilizer;

    public TreeResponseDto(Tree tree) {
        this.id = tree.getId();
        this.treeName = tree.getTreeName();
        this.level = tree.getLevel();
        this.growth = tree.getGrowth();
        this.water = tree.getWater();
        this.fertilizer = tree.getFertilizer();
    }
}
