package org.example.demo_datn.Config;


import lombok.RequiredArgsConstructor;
import org.example.demo_datn.Exception.AppException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;

//    private final JwtAuthenticationEntryPoint entryPoint;

//    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
//    private final CustomAccessDenied accessDeniedHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws AppException {
        return http

                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/login","/user/driver/created",
//                                "/truck/created","/api/timer//runHelloWorld").permitAll()authenticated()
                        .anyRequest().permitAll()
                )
//                .exceptionHandling(ex -> ex
//                        .authenticationEntryPoint(entryPoint)
//                )
//                .exceptionHandling(ex -> ex
//                        .authenticationEntryPoint(authenticationEntryPoint)
//                        .accessDeniedHandler(accessDeniedHandler)
//                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}