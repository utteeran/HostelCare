package com.example.hosteltracker;

import com.example.hosteltracker.model.UserEntity;
import com.example.hosteltracker.repository.UserRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            UserRepository repository) {

        return loginId -> {
            UserEntity account = repository.findByLoginId(loginId)
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    "Account not found"));

            return User.withUsername(account.getLoginId())
                    .password(account.getPassword())
                    .roles(account.getRole())
                    .disabled(!account.isEnabled())
                    .build();
        };
    }

    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return (request, response, authentication) -> {

            String role = authentication.getAuthorities()
                    .iterator()
                    .next()
                    .getAuthority();

            switch (role) {
                case "ROLE_ADMIN" ->
                        response.sendRedirect("/admin.html");

                case "ROLE_STUDENT" ->
                        response.sendRedirect("/index.html");

                case "ROLE_RC" ->
                        response.sendRedirect("/verify.html");

                case "ROLE_STAFF" ->
                        response.sendRedirect("/staff.html");

                default ->
                        response.sendRedirect("/login.html?error");
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/login.html",
                                "/error",
                                "/csrf"
                        ).permitAll()

                        .requestMatchers("/admin.html")
                        .hasRole("ADMIN")

                        .requestMatchers("/index.html")
                        .hasRole("STUDENT")

                        .requestMatchers("/verify.html")
                        .hasRole("RC")

                        .requestMatchers("/staff.html")
                        .hasRole("STAFF")

                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login.html")
                        .loginProcessingUrl("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler(loginSuccessHandler())
                        .failureUrl("/login.html?error")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login.html?logout")
                        .permitAll()
                );

        return http.build();
    }
}