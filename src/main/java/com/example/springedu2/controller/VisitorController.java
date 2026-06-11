package com.example.springedu2.controller;

import com.example.springedu2.entity.Visitor;
import com.example.springedu2.repository.VisitorRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class MeetingController {
    @Autowired
    VisitorRepository visitorRepository;
    //목록 조회
    @GetMapping("/list")
    public ModelAndView list() {
        List<Visitor> visitors = visitorRepository.findAll();
        ModelAndView mv = new ModelAndView();
        mv.addObject("list", visitors);
        return visitorView(visitors, null);
    }

    //검색
    @GetMapping("/vsearch")
    public ModelAndView search(@RequestParam(defaultValue = "") String key) {
        List<Visitor> visitors = key.isBlank()
                ? visitorRepository.findAll()
                : visitorRepository.findByMemoContainingIgnoreCaseOrderByIdDesc(key);
        return visitorsView(visitors, "메인 페이지로 돌아가기");
    }
    //단건 조회
    @GetMapping(value = "/one", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseEntity<Visitor> one(@RequestParam Integer id) {
        return visitorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    //등록
    @PostMapping("/vinsert")
    @Transactional
    public String insert(@Valid Visitor visitor, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("msg", "이름과 내용을 모두 입력하세요. ");
        }
        visitorRepository.save(visitor);
        return "redirect:/vlist";
    }
    //수정
    @PostMapping("/vupdate")
    @Transactional
    public String update(@Valid Visitor visitor,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("msg", "수정할 이름과 내용을 모두 입력하세요.");
            return "redirect:/vlist";
        }
        Visitor entity = visitorRepository.findById(visitor.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방명록입니다."));
        entity.setName(visitor.getName());
        entity.setMemo(visitor.getMemo());

        return "redirect:/vlist";
    }
    //삭제
    @PostMapping("/vdelete")
    @Transactional
    public String delete(@RequestParam Integer id,
                         RedirectAttributes redirectAttributes) {
        if(!visitorRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("msg", "삭제할 방명록을 찾을 수 없습니다.");
            return "redirect:/vlist";
        }
    }
}
