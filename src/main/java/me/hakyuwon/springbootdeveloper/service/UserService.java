package me.hakyuwon.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.springbootdeveloper.domain.User;
import me.hakyuwon.springbootdeveloper.dto.UserDto;
import me.hakyuwon.springbootdeveloper.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // user 엔티티 객체 생성, 저장
    public UserDto.UserSignupResponseDto signUp(UserDto.UserSignupRequestDto userDto) {
        // 이메일 중복 검증
        if (userRepository.existsByEmail(userDto.getEmail())){
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");}

        User newUser = User.builder()
                .email(userDto.getEmail())
                .password(bCryptPasswordEncoder.encode(userDto.getPassword()))
                .build();
        userRepository.save(newUser);

        return UserDto.UserSignupResponseDto.builder()
                .email(newUser.getEmail())
                .build();
    }

    /*@Transactional
    public UserDto.UserLoginResponseDto signIn(UserDto.UserLoginRequestDto userDto){

    }*/

}
