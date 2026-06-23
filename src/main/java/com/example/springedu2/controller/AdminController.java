package com.example.springedu2.controller;

import com.example.springedu2.dto.MemberCreateForm;
import com.example.springedu2.entity.Member;
import com.example.springedu2.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController {
    private final MemberService memberService;

    //회원 목록
    @GetMapping("/admin/members")
    public String memberList(Model model) {

        List<Member> memberList = memberService.findAll();
        model.addAttribute("memberList", memberList);
        return "memberList";
    }

    //회원 추가(관리자가 회원 추가)
    @PostMapping("/admin/members")
    @Transactional
    public String adminCreate(  // @ModelAttribute("memberForm") == Member member = new Member();
            @Valid @ModelAttribute("memberForm") MemberCreateForm memberCreateForm,
            BindingResult bindingResult
            ) {
        if(bindingResult.hasErrors()) {
            return "memberAdminForm";      // 다시 입력 받아라
        }
        
        // 새 회원을 관리자가 추가
        try {
            memberService.create(memberCreateForm);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("createFail", e.getMessage());
            return "memberAdminForm";       // 회원 추가 실패 -> 다시 추가화면으로 이동
        }

        return "redirect:/admin/members";   // 목록 조회
    }
    //회원 추가를 위해 입력받는 화면
    @GetMapping("/admin/members/new")
    public String adminCreateForm(Model model) {
        model.addAttribute("memberForm", new MemberCreateForm());
        return "memberAdminForm";
    }
}
