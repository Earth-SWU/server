package me.hakyuwon.ecostep.domain;

import jakarta.persistence.*;
import lombok.*;
import me.hakyuwon.ecostep.enums.UserStatus;
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
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phonenumber", nullable = false, unique = true)
    private String phoneNumber;


    @Column(name = "reward", nullable = false)
    private Boolean reward = false;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Tree tree;

    @Builder
    public User(String email, String password, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if ("admin@example.com".equals(this.email)) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
    // 특정 이메일이 관리자 역할을 가짐

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
    @Override
    public boolean isEnabled() {
        if (enabled.equals("1")) {
            return false;
        } else {
            return true;
        }
    }
}
