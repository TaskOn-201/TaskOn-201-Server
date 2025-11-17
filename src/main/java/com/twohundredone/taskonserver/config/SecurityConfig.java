package com.twohundredone.taskonserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // H2 console 사용하려면 csrf 비활성 필요
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // H2 페이지가 iframe이라 불가 → 허용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()  // H2 console 허용
                        .anyRequest().permitAll()  // 지금 테스트니까 전체 허용 TODO: (나중에 수정)
                );

        return http.build();
    }
}
