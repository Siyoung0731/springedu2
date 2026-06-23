package com.example.springedu2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    //로그인 페이지로 이동
    @GetMapping("/login")
    public String loginPage() {
        return "login";             //login.html
    }
    
    //로그인 처리할 주소 필요 X
    //@PostMapping("/login") 은 security filter가 처리하므로 코딩X
    //DB 처리 로직을 별도의 클래스에 구현해서 security 가 자동으로 호출처리
    //UserDetailsSerivce 에서 loadUserByUsername() 함수를 실행하고 조회해서 결과를 반환
    //그 결과를 UserDetails 객체의 User로  저장해서 SpringSecurity 에 보낸다. : login Ok
    
    @GetMapping("/access-denied")
    public String accessDeniedPage() {
        return "access-denied";     //access-denied.html
    }
}
