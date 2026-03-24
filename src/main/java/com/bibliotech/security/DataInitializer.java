package com.bibliotech.security;

import com.bibliotech.entity.Author;
import com.bibliotech.entity.Role;
import com.bibliotech.entity.User;
import com.bibliotech.repository.AuthorRepository;
import com.bibliotech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AuthorRepository authorRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin1234"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("=================================================");
            System.out.println("Admin user created with username: 'admin' and password: 'admin1234'");
            System.out.println("=================================================");
        }

        if (authorRepository.count() == 0) {
            Author defaultAuthor = Author.builder()
                    .name("Jane Austen")
                    .biography("English novelist known primarily for her six major novels.")
                    .build();
            authorRepository.save(defaultAuthor);
            System.out.println("=================================================");
            System.out.println("Default author created: ID = " + defaultAuthor.getId() + " - " + defaultAuthor.getName());
            System.out.println("=================================================");
        }
    }
}
