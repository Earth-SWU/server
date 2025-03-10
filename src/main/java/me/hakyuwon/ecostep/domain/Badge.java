package me.hakyuwon.ecostep.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import me.hakyuwon.ecostep.enums.BadgeType;
import java.util.List;

@Getter
@Setter
@Entity
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BadgeType type;

    private String name;
    private String description;
    private int requiredCount;

    @ManyToMany
    private List<Mission> missions;
}
