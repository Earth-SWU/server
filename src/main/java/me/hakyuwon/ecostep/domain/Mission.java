package me.hakyuwon.ecostep.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.hakyuwon.ecostep.enums.MissionType;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="mission")
public class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="mission_id", updatable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MissionType missionType;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "carbonReduction", nullable = false)
    private double carbonReduction;

    @Column(name ="mission_water",nullable = false)
    private int mission_water;

}
