package com.restaurant.ordering.Config;

import com.restaurant.ordering.Security.JwtTokenProvider;
import com.restaurant.ordering.ServiceImpl.UserServiceImpl;
import com.restaurant.ordering.Security.JwtAuthenticationFilter;
import com.restaurant.ordering.Security.JwtAuthorizationFilter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectProvider<UserServiceImpl> userServiceProvider;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(ObjectProvider<UserServiceImpl> userServiceProvider,
                          JwtTokenProvider jwtTokenProvider,
                          PasswordEncoder passwordEncoder) {
        this.userServiceProvider = userServiceProvider;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager)
            throws Exception {
        // Create filters with proper dependencies
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
                authenticationManager,
                jwtTokenProvider
        );
        JwtAuthorizationFilter jwtAuthorizationFilter = new JwtAuthorizationFilter(
                jwtTokenProvider,
                userServiceProvider.getObject()
        );

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                // Configure session management first
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/**",
                                "/h2-console/**",
                                "/customer/**",
                                "/tables/**",
                                "/order/**"
                        ).permitAll()
                        .requestMatchers("/api/kitchen/**").hasRole("KITCHEN")
                        .requestMatchers("/api/waiter/**").hasRole("WAITER")
                        .requestMatchers("/manager/menu/**").hasRole("MANAGER")
                        .anyRequest().authenticated()
                )
                // Configure headers for H2 console - allow frames for H2 console
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                )
                // Add authentication filter
                .addFilter(jwtAuthenticationFilter)
                // Add authorization filter
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userServiceProvider.getObject());
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
}
