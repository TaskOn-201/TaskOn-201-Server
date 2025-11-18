package com.twohundredone.taskonserver;

import com.twohundredone.taskonserver.auth.jwt.JwtAuthenticationFilter;
import com.twohundredone.taskonserver.auth.jwt.JwtProvider;
import com.twohundredone.taskonserver.auth.service.CustomUserDetailsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/signup",
                                "/api/auth/login",
                                "/api/auth/reissue",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // ✅ JWT 필터 등록
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtProvider, customUserDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager 주입용
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // TODO: 실제 배포 시에는 프론트 도메인만 허용하도록 변경해야 함
        config.setAllowedOrigins(List.of("http://localhost:8080","http://localhost:3000", "http://localhost:5173"));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
