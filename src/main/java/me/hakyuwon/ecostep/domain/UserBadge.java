package me.hakyuwon.ecostep.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name="user_badge")
public class UserBadge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user; // 유저

    @ManyToOne
    @JoinColumn(name = "badge_id")
    private Badge badge; // 부여된 뱃지

    private LocalDate awardedAt; // 뱃지 부여 일자

    public void setInternalUser(User user){
        this.user = user;
    }
}
