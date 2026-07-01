package me.hakyuwon.ecostep.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column (name="nickname", nullable = false)
    private String nickname;

    @Column (name = "major", nullable = false)
    private String major;

    @Column(name = "reward", nullable = false)
    private Boolean reward = false;

    @Column(name = "refresh_token")
    private String refreshToken;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Tree tree;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserBadge> userBadges = new ArrayList<>();

    @Builder
    public User(String email, String password, String phoneNumber, String nickname, String major) {
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.nickname = nickname;
        this.major = major;
    }

    public void encodePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }
    public void clearRefreshToken() {
        this.refreshToken = null;
    }
    public void connectTree(Tree tree) {
        this.tree = tree;
        tree.setUserInternal(this);
    }
    public void addBadges(UserBadge userBadge) {
        this.userBadges.add(userBadge);
        userBadge.setInternalUser(this);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return email;
    }
    @Override
    public String getPassword() {
        return password;
    }

    // 인증 사용자의 계정 유효 기간 정보를 반환
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 인증 사용자의 계정 잠금 상태를 반환
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 인증 사용자의 비밀번호 유효 기간 상태를 반환
    // false: 기간 만료
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
