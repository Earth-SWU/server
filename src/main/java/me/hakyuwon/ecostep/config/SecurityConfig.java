package me.hakyuwon.ecostep.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      return http.authorizeHttpRequests((auth)->auth
              .requestMatchers("/", "/api/users/login").permitAll()
              .requestMatchers("/api/users/signup").permitAll()
              .anyRequest().authenticated()
      ).formLogin((auth)->auth
      .loginPage("/login")
              .defaultSuccessUrl("/")
              .permitAll()).build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}

