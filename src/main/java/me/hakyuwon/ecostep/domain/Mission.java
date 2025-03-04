package me.hakyuwon.ecostep.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.hakyuwon.ecostep.enums.MissionType;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="mission_id", updatable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MissionType missionType;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal carbonReduction;

    @Column(nullable = false)
    private int mission_water;

    @Column(nullable = false)
    private int mission_fert;
}
