package me.hakyuwon.ecostep.service;

import me.hakyuwon.ecostep.config.jwt.TokenProvider;
import me.hakyuwon.ecostep.domain.Badge;
import me.hakyuwon.ecostep.domain.Tree;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.domain.UserBadge;
import me.hakyuwon.ecostep.dto.UserDto;
import me.hakyuwon.ecostep.dto.UserLoginRequest;
import me.hakyuwon.ecostep.dto.UserSignUpRequest;
import me.hakyuwon.ecostep.repository.BadgeRepository;
import me.hakyuwon.ecostep.repository.TreeRepository;
import me.hakyuwon.ecostep.repository.UserBadgeRepository;
import me.hakyuwon.ecostep.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;


@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final TreeRepository treeRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenProvider tokenProvider;
    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;

    public UserService(UserRepository userRepository, TreeRepository treeRepository, BCryptPasswordEncoder bCryptPasswordEncoder, TokenProvider tokenProvider, UserBadgeRepository userBadgeRepository, BadgeRepository badgeRepository) {
        this.userRepository = userRepository;
        this.treeRepository = treeRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenProvider = tokenProvider;
        this.userBadgeRepository = userBadgeRepository;
        this.badgeRepository = badgeRepository;
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
        treeRepository.save(tree);
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
    public void deleteUser(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 사용자입니다."));

        userRepository.delete(user);
    }

    // 회원가입 후 뱃지 획득
    public void firstBadge(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Badge badge = badgeRepository.findByName("에코스텝 비기너")
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 뱃지입니다."));

        UserBadge userBadge = new UserBadge();
        userBadge.setUser(user);
        userBadge.setBadge(badge);
        userBadge.setAwardedAt(LocalDate.now());

        userBadgeRepository.save(userBadge);
    }
}
