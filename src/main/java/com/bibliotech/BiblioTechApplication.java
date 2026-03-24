package com.bibliotech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableJpaRepositories(basePackages = "com.bibliotech.repository")
@EnableReactiveMongoRepositories(basePackages = "com.bibliotech.reactive.repository")
public class BiblioTechApplication {

    public static void main(String[] args) {
        SpringApplication.run(BiblioTechApplication.class, args);
    }
}
