package me.hakyuwon.ecostep.service;

import me.hakyuwon.ecostep.config.jwt.TokenProvider;
import me.hakyuwon.ecostep.domain.Badge;
import me.hakyuwon.ecostep.domain.Tree;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.domain.UserBadge;
import me.hakyuwon.ecostep.dto.UserDto;
import me.hakyuwon.ecostep.dto.UserLoginRequest;
import me.hakyuwon.ecostep.dto.UserSignUpRequest;
import me.hakyuwon.ecostep.exception.CustomException;
import me.hakyuwon.ecostep.exception.ErrorCode;
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
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);}

        // 비밀번호 일치 확인
        if(!userDto.getPassword().equals(userDto.getConfirmPassword())){
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
        }

        // 핸드폰 번호 중복 확인
        if(userRepository.existsByPhoneNumber(userDto.getPhoneNumber())){
            throw new CustomException(ErrorCode.DUPLICATE_PHONE);
        }

        User newUser = userDto.toEntity();

        // 비밀번호 암호화 후 저장
        newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));

        // Tree 객체 생성
        Tree tree = new Tree();
        tree.setUser(newUser);
        tree.setTreeLevel(1);
        tree.setTreeGrowth(0);
        tree.setWater(0);

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
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));

        if (userDto.getPassword() == null || !bCryptPasswordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
        }

        String accessToken = tokenProvider.createToken(user.getEmail());
        String refreshToken = tokenProvider.createRefreshToken(user.getEmail());

        return UserDto.UserLoginResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 회원 탈퇴
    public void deleteUser(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));

        userRepository.delete(user);
    }

    // 회원가입 후 뱃지 획득
    public void firstBadge(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Badge badge = badgeRepository.findByName("에코스텝 비기너")
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 뱃지입니다."));

        if (userBadgeRepository.existsByUserAndBadge(user, badge)) {
            throw new IllegalStateException("이미 해당 뱃지를 보유하고 있습니다.");
        }

        UserBadge userBadge = new UserBadge();
        userBadge.setUser(user);
        userBadge.setBadge(badge);
        userBadge.setAwardedAt(LocalDate.now());

        userBadgeRepository.save(userBadge);
    }

}
