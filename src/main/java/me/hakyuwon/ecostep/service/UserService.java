package me.hakyuwon.ecostep.service;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.config.jwt.TokenProvider;
import me.hakyuwon.ecostep.domain.Tree;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.dto.UserDto;
import me.hakyuwon.ecostep.dto.UserLoginRequest;
import me.hakyuwon.ecostep.dto.UserSignUpRequest;
import me.hakyuwon.ecostep.repository.TreeRepository;
import me.hakyuwon.ecostep.repository.UserRepository;
import org.antlr.v4.runtime.Token;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final TreeRepository treeRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenProvider tokenProvider;

    public UserService(UserRepository userRepository,TreeRepository treeRepository, BCryptPasswordEncoder bCryptPasswordEncoder, TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.treeRepository = treeRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenProvider = tokenProvider;
    }

    // user 엔티티 객체 생성, 저장 (회원가입)
    public UserDto.UserSignupResponseDto signUp(UserSignUpRequest userDto) {
        // 이메일 중복 검증
        if (userRepository.existsByEmail(userDto.getEmail())){
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");}

        // 비밀번호 일치 확인
        if(!userDto.getPassword().equals(userDto.getConfirmPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        User newUser = userDto.toEntity();

        // 비밀번호 암호화 후 저장
        newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));

        // Tree 객체 생성
        Tree tree = new Tree();
        tree.setUser(newUser);  // Tree와 User 연결
        tree.setLevel(1);    // 트리의 초기 설정
        tree.setGrowth(0);
        tree.setWater(0);
        tree.setFertilizer(0);

        newUser.setTree(tree);
        userRepository.save(newUser);

        return UserDto.UserSignupResponseDto.builder()
                .userId(newUser.getId())
                .email(newUser.getEmail())
                .build();
    }

    // 로그인
    public UserDto.UserLoginResponseDto logIn(UserLoginRequest userDto){
        User user = userRepository.findByEmail(userDto.getEmail())
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 사용자입니다."));
        if (userDto.getPassword() == null || !bCryptPasswordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        String accessToken = tokenProvider.createToken(user.getEmail());
        String refreshToken = tokenProvider.createRefreshToken(user.getEmail());

        return UserDto.UserLoginResponseDto.builder()
                .email(user.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 회원 탈퇴
    @Transactional
    public void deleteUser(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 사용자입니다."));

        userRepository.delete(user);
    }
}
