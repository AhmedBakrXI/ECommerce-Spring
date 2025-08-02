package org.project.ecommerce.config;

import org.project.ecommerce.user.security.jwt.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private static final String[] AUTH_WHITELIST = {
            "/api/v1/user/register",
            "/api/v1/user/auth",
            "/api/v1/user/refresh",
            "/api/v1/verify/**"
    };

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest().authenticated() // All other requests require authentication
        );
        http.sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.httpBasic(withDefaults());
        http.csrf(CsrfConfigurer::disable);
        return http.build();
    }
}
