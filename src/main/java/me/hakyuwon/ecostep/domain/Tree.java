package me.hakyuwon.ecostep.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Tree extends BaseEntity {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "tree_id", updatable = false)
    private Long treeId;

    @Column(name = "tree_name", nullable = false)
    private String treeName;

    @Column(name = "tree_growth", nullable = false)
    private BigDecimal growth; // 성장 퍼센트

    @Column(name = "tree_level", nullable = false)
    private Integer level = 1; // 나무 레벨

    @Column(name = "water", nullable = false)
    private Integer water = 0;

    @Column(name = "fertilizer", nullable = false)
    private Integer fertilizer = 0;

}
