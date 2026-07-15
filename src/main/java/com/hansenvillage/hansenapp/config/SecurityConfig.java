package com.hansenvillage.hansenapp.config;

import com.hansenvillage.hansenapp.security.JsonSecurityHandlers;
import com.hansenvillage.hansenapp.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JsonSecurityHandlers jsonSecurityHandlers;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, JsonSecurityHandlers jsonSecurityHandlers) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jsonSecurityHandlers = jsonSecurityHandlers;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/register/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login", "/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/refresh", "/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/admin/register", "/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/admin/login").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jsonSecurityHandlers)
                        .accessDeniedHandler(jsonSecurityHandlers))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
