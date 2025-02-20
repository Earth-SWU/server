package me.hakyuwon.springbootdeveloper.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false)
    private Long userId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "reward", nullable = false)
    private Boolean reward = false;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Tree tree;

}
