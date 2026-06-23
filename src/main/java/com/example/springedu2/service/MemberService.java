package com.example.springedu2.service;

import com.example.springedu2.dto.MemberCreateForm;
import com.example.springedu2.dto.MemberUpdateForm;
import com.example.springedu2.entity.Member;
import com.example.springedu2.entity.Role;
import com.example.springedu2.repository.MemberRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    //로그인을 위해 db 에서 회원정보를 조회해서  UserDetails
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //회원정보 DB에서 회원 이름으로 조회
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));

        // 조회한 결과를 Member -> UserDetails 으로 변환
        UserDetails user =  User.builder()
                .username(member.getUsername()) //사용자 id
                .password(member.getPassword()) //사용자 pwd
                .disabled(!member.isEnabled()) //계정 사용 가능 X -> 삭제
                .roles(member.getRole().toString()) //사용자 권한 "ADMIN" -> "ROLE_ADMIN" 권한
                .build();
        return user;
    }
    //------------------------------------------------------------------------------------------
    //회원 조회
    //전체 조회
    public List<Member> findAll() {
        return memberRepository.findAll();
    }
    //아이디로 조회
    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
    }
    //이름으로 조회
    public Member findByUsername(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
    }
    //일반 유저 회원가입
    public Member register(MemberCreateForm memberForm) {
        memberForm.setRole(Role.USER.name());
        return create(memberForm);
    }

    //회원가입 및 관리자가 회원 추가
    @Transactional
    public Member create(MemberCreateForm memberForm) {
        //기존 회원인지 조회
        validNewMember(memberForm.getUsername(), memberForm.getEmail());

        Member member = new Member();
        member.setUsername(memberForm.getUsername());
        member.setPassword(passwordEncoder.encode(memberForm.getPassword()));
        member.setName(memberForm.getName());
        member.setEmail(memberForm.getEmail());
        member.setRole( parseRole(memberForm.getRole()) );
        member.setEnabled(true);
        return memberRepository.save(member);
    }

    // 기존 회원인지 체크
    private void validNewMember(String username, String email) {
        if(memberRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }
        if(memberRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }
    }

    // 권한 문자열 변환 "ADMIN" -> Role.ADMIN
    private Role parseRole(String role) {
        if(role == null || role.isBlank()) {
            return Role.USER;
        }
        return Role.valueOf(role.toUpperCase());
    }
    public MemberUpdateForm toUpdateForm(Member member) {
        MemberUpdateForm form = new MemberUpdateForm();
        form.setName(member.getName());
        form.setEmail(member.getEmail());
        form.setPassword(member.getPassword());
        form.setRole(member.getRole().toString());
        form.setEnabled(member.isEnabled());
        return form;
    }

    //회원 정보 수정
    @Transactional
    public Member update(Long id, @Valid MemberUpdateForm memberForm,
                         boolean adminMode) {
        Member member = findById(id);

        if(memberRepository.existsByEmailAndIdNot(memberForm.getEmail(), id)) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }
        member.setName(memberForm.getName());
        member.setEmail(memberForm.getEmail());

        if(memberForm.getPassword() != null && !memberForm.getPassword().isBlank()) {
            member.setPassword(passwordEncoder.encode(memberForm.getPassword()));
        }
        if(adminMode) {
            member.setRole(parseRole(memberForm.getRole()));
            member.setEnabled(memberForm.isEnabled());
        }
        return member;
    }

}
