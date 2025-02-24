package me.hakyuwon.ecostep.service;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.dto.UserDto;
import me.hakyuwon.ecostep.dto.UserSignUpRequest;
import me.hakyuwon.ecostep.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // user 엔티티 객체 생성, 저장
    public UserDto.UserSignupResponseDto signUp(UserSignUpRequest userDto) {
        // 이메일 중복 검증
        if (userRepository.existsByEmail(userDto.getEmail())){
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");}

        User newUser = userDto.toEntity();

        // 비밀번호 암호화 후 저장
        newUser = User.builder()
                .email(newUser.getEmail())
                .password(bCryptPasswordEncoder.encode(newUser.getPassword()))
                .phoneNumber(newUser.getPhoneNumber())
                .status(newUser.getStatus())
                .build();

        userRepository.save(newUser);

        return UserDto.UserSignupResponseDto.builder()
                .email(newUser.getEmail())
                .build();
    }

    @Transactional
    public UserDto.UserLoginResponseDto logIn(UserDto.UserLoginRequestDto userDto){

    }

}
