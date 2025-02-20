package me.hakyuwon.springbootdeveloper.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "mission_name", nullable = false)
    private String missionName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal carbonReduction;

    @Column(nullable = false)
    private int mission_water;

    @Column(nullable = false)
    private int mission_fert;
}
