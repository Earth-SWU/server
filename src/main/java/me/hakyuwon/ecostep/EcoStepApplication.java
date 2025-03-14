package me.hakyuwon.ecostep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class EcoStepApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcoStepApplication.class, args);
    }
}
