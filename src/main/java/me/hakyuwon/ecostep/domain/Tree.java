package me.hakyuwon.ecostep.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name="tree")
public class Tree extends BaseEntity {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "tree_id", updatable = false)
    private Long id;

    @Column(name = "tree_name", nullable = true)
    private String treeName;

    @Column(name = "tree_growth", nullable = false)
    private int treeGrowth; // 성장 퍼센트

    @Column(name = "tree_level", nullable = false)
    private int treeLevel;

    @Column(name = "water", nullable = false)
    private int water;

    @Column(name = "fertilizer", nullable = false)
    private int fertilizer;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private static final int WATER_GROWTH = 5;  // 물 1개당 성장 %
    private static final int FERTILIZER_GROWTH = 10;  // 비료 1개당 성장 %

    public void applyItems(int waterGain, int fertilizerGain) {
        this.water += waterGain;
        this.fertilizer += fertilizerGain;
    }

}
