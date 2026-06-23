package com.example.springedu2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// SpringSecurity 의 설정
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //서버가 내려주는 csrf 토큰 값을 사용하지 않는다 -> 순수 html(visitorForm) 이라 csrf 토큰을 보관 처리 불가능하다.
                .csrf(csrf -> csrf.disable())         // 실수는 설정, 공부는 설정 안함
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/index.html","/css/**",
                                "/img/**","/js/**", "/fonts/**",
                                "/login", "/members/register"
                        ).permitAll() // 누구나 사용 가능
                        .requestMatchers("/admin/**", "/vupdate", "/vdelete").hasRole("ADMIN")
                        .requestMatchers(
                                "/visitorMain", "/one", "/visitorForm.html",
                                "/vlist", "/vinsert", "/vsearch", "/members/me"
                        ).authenticated() // 로그인 필요
                        .anyRequest().authenticated() // 설정하지 않은 다른 요청도 로그인 필요
                )
                //formLogin() : 사용자가 <form> 으로 입력한 username, password 를 기반으로 인증 처리
                // DataInitializer 클래스를 미리 db에 저장한다. -> Member table
                .formLogin(form -> form
                        .loginPage("/login")
                        // GET /login -> PageController 에 /login 주소 이동 -> login.html 로 보냄
                        // 내가 만든 로그인 화면 사용
                        // 만약 <input name="username"/> -> <input name="loginId"/>
                        // 만약 <input name="password"/> -> <input name="loginPwd"/>
                        // Security 설정에서
                        // .formLogin( form -> form
                        //      .usernameParameter("loginId")
                        //      .passwordParameter("loginPwd")
                        //) -> 이렇게 바꾸면 된다.
                        .loginProcessingUrl("/login")
                        // Post /login
                        // Spring Security 가 username, password 읽어서 인증처리 : 자동
                        // UserDetailsService 안의 loadUserByUsername() 을 실행해서 db 검색 로그인처리까지 진행
                        .defaultSuccessUrl("/visitorMain", true)
                        // 로그인 성공 시 "/" 나 "/visitorMain.html"
                        // 비밀번호가 틀리거나, 사용자가 없을 경우 -> "/login?error 또는 .failureUrl("/login?error") 이동해서 thymeleaf 에서 처리
                        // login.html p 태그 param.error 에 "아이디 또는 비밀번호가 올바르지 않습니다" 라고 출력
                        .permitAll() // 누구나 사용 가능
                        // 로그인 화면, 로그인처리 URL, 로그인실패 URL 는 인증 없이 접근가능해야한다.
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/access-denied")
                ); // 접근 거부 페이지 처리
        return http.build();
    }
    // 비밀번호 암호화
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
