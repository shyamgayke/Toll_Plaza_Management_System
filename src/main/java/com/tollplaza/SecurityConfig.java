package com.tollplaza;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.access.AccessDeniedHandler;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        JdbcUserDetailsManager manager =
                new JdbcUserDetailsManager(dataSource);

        manager.setUsersByUsernameQuery(
                "SELECT username, password, enabled " +
                "FROM app_users WHERE username = ?"
        );

        manager.setAuthoritiesByUsernameQuery(
                "SELECT username, authority " +
                "FROM app_authorities WHERE username = ?"
        );

        return manager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                // Public pages and static resources
                .requestMatchers(
                    "/login",
                    "/error",
                    "/css/**",
                    "/js/**",
                    "/images/**"
                ).permitAll()

                // Only Admin can open plaza/booth creation and edit forms
                .requestMatchers(
                    "/plazas/new",
                    "/plazas/edit/**",
                    "/booths/new",
                    "/booths/edit/**"
                ).hasRole("ADMIN")

                // Only Admin can create, update, or delete plazas and booths
                .requestMatchers(
                    HttpMethod.POST,
                    "/plazas",
                    "/plazas/update/**",
                    "/plazas/delete/**",
                    "/booths",
                    "/booths/update/**",
                    "/booths/delete/**"
                ).hasRole("ADMIN")

                // All remaining application pages require authentication
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error")
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            .exceptionHandling(exception -> exception
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.sendRedirect("/access-denied");
                })
            );
        // CSRF protection remains enabled by default.
        return http.build();
    }
}