package me.hakyuwon.springbootdeveloper.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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

    @Column(name="rdc_amount",nullable = false, precision = 5, scale = 2)
    private BigDecimal reductionAmount; // 감축된 탄소량 (kg)
}
