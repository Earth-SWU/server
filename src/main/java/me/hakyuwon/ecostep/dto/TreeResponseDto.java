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

    public TreeResponseDto(Tree tree) {
        this.id = tree.getId();
        this.treeName = tree.getTreeName();
        this.level = tree.getTreeLevel();
        this.growth = tree.getTreeGrowth();
        this.water = tree.getWater();
    }
}
