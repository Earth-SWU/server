package me.hakyuwon.ecostep.dto;

import me.hakyuwon.ecostep.enums.TreeLevel;
import me.hakyuwon.ecostep.enums.TreeType;

import java.math.BigDecimal;

public class TreeDto {
    private Long id;
    private String treeName;
    private TreeType treeType;
    private TreeLevel treeLevel;
    private BigDecimal treeGrowth;
    private Integer water;
    private Integer fertilizer;
}
