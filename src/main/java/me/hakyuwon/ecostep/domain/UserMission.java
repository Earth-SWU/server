package me.hakyuwon.ecostep.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class UserMission extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @OneToOne(mappedBy = "userMission", cascade = CascadeType.ALL)
    private CarbonReduction carbonReduction;
}
