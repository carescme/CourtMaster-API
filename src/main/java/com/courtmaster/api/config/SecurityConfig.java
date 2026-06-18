package com.courtmaster.api.config;

import com.courtmaster.api.filter.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login", "/api/auth/registro").permitAll()
                .requestMatchers(HttpMethod.GET, "/").permitAll()
                .requestMatchers("/error").permitAll()

                .requestMatchers(HttpMethod.GET, "/api/pistas/**").hasAnyRole("USER", "OWNER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/pistas/**").hasAnyRole("ADMIN", "OWNER")
                .requestMatchers(HttpMethod.PUT, "/api/pistas/**").hasAnyRole("ADMIN", "OWNER")
                .requestMatchers(HttpMethod.DELETE, "/api/pistas/**").hasAnyRole("ADMIN", "OWNER")

                .requestMatchers(HttpMethod.GET, "/api/clubes/**").hasAnyRole("USER", "OWNER", "ADMIN")
                .requestMatchers("/api/clubes/**").hasAnyRole("ADMIN", "OWNER")

                .requestMatchers("/api/reservas/**").hasAnyRole("USER", "OWNER", "ADMIN")

                .requestMatchers("/api/transacciones/**").hasAnyRole("USER", "OWNER", "ADMIN")

                .requestMatchers("/api/usuarios/perfil").authenticated()
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}