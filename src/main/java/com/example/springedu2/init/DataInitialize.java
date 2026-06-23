package com.example.springedu2.init;

import com.example.springedu2.entity.Member;
import com.example.springedu2.entity.Role;
import com.example.springedu2.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


// implements ApplicationRunner : CommandLineRunner - 파라미터 차이
// 애플리케이션 실행되면(SpringApplication.run(Springedu2Application.class args)) 바로 실행할 명령을 기술

@Component
@RequiredArgsConstructor
public class DataInitialize implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        // 기존 계정이 없다면, admin, user 계정을 생성 -> mysql 저장
        createIfNotExists("admin", "admin1234", "관리자", "admin@example.com", Role.ADMIN);
        createIfNotExists("user", "user1234", "일반사용자", "user@example.com", Role.USER);
        createIfNotExists("guest", "guest1234", "손님", "guest@example.com", Role.GUEST);
    }

    protected void createIfNotExists(String username, String password ,
                                     String name, String email, Role role) {
        //계정이 존재한다면 return
        if(memberRepository.existsByUsername(username)) {
            return;
        }
        // 계정이 없으면 저장
        Member member = new Member();


        member.setUsername(username);
        String pwd = passwordEncoder.encode(password);
        member.setPassword(pwd);
        member.setName(name);
        member.setEmail(email);
        member.setRole(role);
        member.setEnabled(true);

        System.out.println("저장된 member: " + member);

        memberRepository.save(member);

    }
}
