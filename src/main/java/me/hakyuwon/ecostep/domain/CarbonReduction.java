package me.hakyuwon.ecostep.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class CarbonReduction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne // carbon이 UserMission을 참조
    @JoinColumn(name = "user_mission_id", nullable = false)
    private UserMission userMission;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name="rdc_amount",nullable = false)
    private double reductionAmount = 0; // 감축된 탄소량 (kg)
}
