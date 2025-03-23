package me.hakyuwon.ecostep.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {
    private int connectionTimeout = 20000;
    private int responseTimeout = 20000;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(connectionTimeout))  // 연결 타임아웃 설정
                .setReadTimeout(Duration.ofSeconds(responseTimeout))      // 응답 타임아웃 설정
                .build();
    }

}
