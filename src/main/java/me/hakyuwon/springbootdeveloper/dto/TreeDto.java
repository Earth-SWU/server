package me.hakyuwon.springbootdeveloper.dto;

import me.hakyuwon.springbootdeveloper.enums.TreeLevel;
import me.hakyuwon.springbootdeveloper.enums.TreeType;

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
