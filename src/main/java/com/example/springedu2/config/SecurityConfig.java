package com.example.springedu2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf) -> csrf.disable())         // 실수는 설정, 공부는 설정 안함
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/css/**",
                                "/img/**",
                                "/js/**",
                                "/fonts/**",
                                "/login",
                                "/members/register"
                        ).permitAll() // 누구나 사용 가능
                        .requestMatchers("/admin/**", "/vupdate", "/vdelete").hasRole("ADMIN")
                        .requestMatchers("/visitorMain.html", "/visitorForm.html",
                                "/vlist", "/vinsert", "/vsearch", "/one", "/members/me").authenticated()
                        .anyRequest().authenticated() // 설정하지 않은 다른 요청도 로그인 필요
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/loginProc")
                        .defaultSuccessUrl("/index.html")
                )
                .logout(logout -> logout
                        .)
                .exceptionHandling(exception -> exception.accessDeniedHandler())
    }
    // 비밀번호 암호화
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
