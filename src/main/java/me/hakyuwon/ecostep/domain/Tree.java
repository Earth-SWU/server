package me.hakyuwon.ecostep.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Builder
    public Tree(String treeName, int treeGrowth, int treeLevel, int water, int fertilizer) {
        this.treeName = treeName;
        this.treeGrowth = treeGrowth;
        this.treeLevel = treeLevel;
        this.water = water;
        this.fertilizer = fertilizer;
    }

    public void applyItems(int waterGain, int fertilizerGain) {
        this.water += waterGain;
        this.fertilizer += fertilizerGain;
    }

    public void useWater() {
        this.water--;
        this.treeGrowth += 5;
        checkLevelUp();
    }

    public void useFertilizer(){
        this.fertilizer--;
        this.treeGrowth += 10;
        checkLevelUp();
    }

    public void checkLevelUp(){
        while(this.treeGrowth>=100){
            if(this.treeLevel<4){
                this.treeLevel++;
                this.treeGrowth -= 100;
            }else{
                this.treeGrowth = 100; // 만렙 도달 시 성장도 100% 고정
                break;
            }
        }
    }

    public void setUserInternal(User user) {
        this.user = user;
    }
}
