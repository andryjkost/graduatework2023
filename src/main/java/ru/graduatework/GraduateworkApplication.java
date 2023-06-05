package ru.graduatework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "ru.graduatework")
@EnableScheduling
public class GraduateworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraduateworkApplication.class, args);
    }

}
