package com.example.springedu2.controller;

import com.example.springedu2.dto.MemberCreateForm;
import com.example.springedu2.dto.MemberUpdateForm;
import com.example.springedu2.entity.Member;
import com.example.springedu2.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    // 회원정보 수정 -> 입력 받는 화면으로 이동
    @GetMapping("/admin/members/{id}/edit")
    public String adminEditForm(
            @PathVariable Long id, Model model) {
        // 수정을 위한 DB 데이터를 Entity 조회 - (1)
        Member member = memberService.findById(id);

        // db에서 조회한 member를 -> memberAdminEditForm 에서 사용할 객체인 MemberUpdateForm 구조로 변경 - (2)
        MemberUpdateForm memberForm = memberService.toUpdateForm(member);

        model.addAttribute("memberForm", memberForm);
        model.addAttribute("member", member);   //조회한 정보
        return "memberAdminEditForm";       // memberAdminEditForm.html
    }

    // 넘어온 수정정보를 가지고 member 정보를 수정
    @PostMapping("/admin/members/{id}/edit")
    public String adminEditForm(@PathVariable Long id, @Valid @ModelAttribute("memberForm") MemberUpdateForm form,
                                BindingResult bindingResult, Model model) {
        //넘어온 정보를 수정한다
        Member member = memberService.findById(id);
        if(bindingResult.hasErrors()) {
            model.addAttribute("memberForm", member);
            return "memberAdminEditForm";
        }

        try {
            memberService.update(id, form, true);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("updateFail", e.getMessage());
            model.addAttribute("member", member);
            return "memberAdminEditForm";
        }

        return "redirect:/admin/members";
    }

    // 회원정보 삭제
    @PostMapping("/admin/members/{id}/delete")
    public String adminDeleteForm(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            memberService.delete(id, authentication.getName());
        } catch(IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("msg", e.getMessage());
        }
        return "redirect:/admin/members";
    }
}
