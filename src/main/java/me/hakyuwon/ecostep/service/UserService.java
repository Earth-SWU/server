package me.hakyuwon.ecostep.service;

import lombok.RequiredArgsConstructor;
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
import me.hakyuwon.ecostep.repository.UserBadgeRepository;
import me.hakyuwon.ecostep.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;


@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenProvider tokenProvider;
    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;

    // 회원가입
    public UserDto.UserSignupResponseDto signUp(UserSignUpRequest request) {
        // 이메일 중복 검증
        if (userRepository.existsByEmail(request.getEmail())){
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);}

        // 비밀번호 검증
        request.validatePassword();

        // 핸드폰 번호 중복 확인
        if(userRepository.existsByPhoneNumber(request.getPhoneNumber())){
            throw new CustomException(ErrorCode.DUPLICATE_PHONE);
        }

        User newUser = request.toEntity();

        // 비밀번호 암호화 후 저장
        newUser.encodePassword(bCryptPasswordEncoder.encode(newUser.getPassword()));

        // 나무 객체 생성
        Tree tree = Tree.builder()
                .treeName(request.getNickname())
                .treeLevel(0)
                .treeGrowth(0)
                .water(0)
                .build();

        newUser.connectTree(tree);
        userRepository.save(newUser);

        return UserDto.UserSignupResponseDto.builder()
                .userId(newUser.getId())
                .email(newUser.getEmail())
                .build();
    }

    // 로그인
    public UserDto.UserLoginResponseDto logIn(UserLoginRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));

        if (request.getPassword() == null || !bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
        }

        String accessToken = tokenProvider.createToken(user.getEmail());
        String refreshToken = tokenProvider.createRefreshToken(user.getEmail());
        user.updateRefreshToken(refreshToken); // User 엔티티에 토큰 저장

        return UserDto.UserLoginResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 로그아웃
    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.clearRefreshToken();
    }

    // 로그아웃 후 토큰 검증
    @Transactional(readOnly = true)
    public void validateAndRevokeRefresh(String email, String clientRefreshToken) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getRefreshToken() == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN); // 이미 로그아웃됨
        }
        if (!user.getRefreshToken().equals(clientRefreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN); // 토큰 불일치
        }
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
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

        Badge badge = badgeRepository.findByName("에코스텝 비기너")
                .orElseThrow(()-> new CustomException(ErrorCode.BADGE_NOT_FOUND));

        if (userBadgeRepository.existsByUserAndBadge(user, badge)) {
            throw new CustomException(ErrorCode.USER_BADGE_ALREADY_EXISTS);
        }

        UserBadge userBadge = UserBadge.builder()
                .badge(badge)
                .awardedAt(LocalDate.now())
                .build();
        user.addBadges(userBadge);
    }
}
