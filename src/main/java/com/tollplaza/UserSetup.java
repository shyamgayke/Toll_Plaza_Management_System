
package com.tollplaza;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserSetup {

    @Bean
    public CommandLineRunner createUsers(
            JdbcTemplate jdbc,
            PasswordEncoder passwordEncoder) {

        return args -> {
            createUser(jdbc, passwordEncoder,
                    System.getenv("ADMIN_USERNAME"),
                    System.getenv("ADMIN_PASSWORD"),
                    "ROLE_ADMIN");

            createUser(jdbc, passwordEncoder,
                    System.getenv("STAFF_USERNAME"),
                    System.getenv("STAFF_PASSWORD"),
                    "ROLE_STAFF");
        };
    }

    private void createUser(
            JdbcTemplate jdbc,
            PasswordEncoder encoder,
            String username,
            String password,
            String role) {

        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            System.out.println(
                    "User setup skipped: required environment variables are missing.");
            return;
        }

        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM app_users WHERE username = ?",
                Integer.class,
                username);

        if (count == null || count == 0) {
            jdbc.update(
                    "INSERT INTO app_users (username, password, enabled) VALUES (?, ?, TRUE)",
                    username,
                    encoder.encode(password));

            System.out.println("Created user: " + username);
        }

        Integer roleCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM app_authorities WHERE username = ? AND authority = ?",
                Integer.class,
                username,
                role);

        if (roleCount == null || roleCount == 0) {
            jdbc.update(
                    "INSERT INTO app_authorities (username, authority) VALUES (?, ?)",
                    username,
                    role);
        }
    }
}
