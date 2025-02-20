package me.hakyuwon.springbootdeveloper.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.enabled;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity implements UserDetails {
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

    @Builder
    public User(String email, String password, Tree tree) {
        this.email = email;
        this.password = password;
        this.tree = tree;

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if ("admin@example.com".equals(this.email)) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
    // 특정 이메일이 관리자 역할을 가지도록 설정

    @Override
    public String getUsername() {
        return email;
    }
    @Override
    public String getPassword() {
        return password;
    }
    // 인증 사용자의 계정 유효 기간 정보를 반환
    // false: 기간 만료
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 인증 사용자의 계정 잠금 상태를 반환
    // false: 잠금 상태
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
    // 인증 사용자의 활성화 상태를 반환
    // false: 비활성화 상태
    // enabled.equals("1") 의 값은 개인이 설정한 값으로 커스터마이징해주세요.
    @Override
    public boolean isEnabled() {
        if (enabled.equals("1")) {
            return false;
        } else {
            return true;
        }
    }
}
