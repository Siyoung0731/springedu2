package com.example.springedu2.controller;

import com.example.springedu2.entity.Visitor;
import com.example.springedu2.repository.VisitorRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class VisitorController {

    // 1. Autowired 이용 생성자 주입
    //@Autowired
    //private VisitorRepository visitorRepository;

/*    // 2. 생성자 주입 : 최신 문법
    public VisitorController(VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
    }
    private VisitorRepository visitorRepository;*/

    // 3. 생성자 주입 다른 방법, final 가능
    // @RequireArgsConstructor 필수 -> 단점 : Lombok 필수!
    private final VisitorRepository visitorRepository;

    //목록 조회
    @GetMapping("/vlist")
    public ModelAndView list() {
        List<Visitor> visitors = visitorRepository.findAll();           // 전체 목록 조회
        return visitorView(visitors, null);
    }
    // visitorView 함수
    private ModelAndView visitorView(List<Visitor> visitors, String buttonText) {
        ModelAndView mv = new ModelAndView("visitorView");
        //mv.setViewName("visitorView");            //visitorView.html(Model 사용) - thymeleaf
        if(visitors.isEmpty()) {
            mv.addObject("msg", "조회된 결과가 없습니다.");
        } else {
            mv.addObject("visitors", visitors);
        }
        if(buttonText != null) {
            mv.addObject("buttonText", buttonText);
        }
        return mv;
    }

    //검색
    @GetMapping("/vsearch")
    public ModelAndView search(@RequestParam(defaultValue = "") String key) {
        List<Visitor> visitors = key.isBlank()
                ? visitorRepository.findAll()
                : visitorRepository.findByMemoContainingIgnoreCaseOrderByIdDesc(key);
        ModelAndView mv = new ModelAndView();
        mv.setViewName("visitorView");
        mv.addObject("visitors", visitors);
        mv.addObject("msg", "메인페이지로 돌아가기");
        return mv;
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
    // @Valid : form 에서 넘어온 데이터를 @Entity 에 있는
    // 설정(@Id, @NotBlank, @Column(nullable=false) )
    // 과 비교해서 입력 데이터를 검증
    // BindingResult : 객체에서 오류가 발생 시 코드를 담아주는 역할
    @PostMapping("/vinsert")
    @Transactional
    public String insert(@Valid Visitor visitor, BindingResult bindingResult, Model model) {

        System.out.println("visitor: " + visitor);
        System.out.println("BindingResult: " + bindingResult);
        // 오류 검증
        if (bindingResult.hasErrors()) {
            model.addAttribute("msg", "이름과 내용을 모두 입력하세요. ");
        }
        visitorRepository.save(visitor);            // entity 객체를 사용해야함
        return "redirect:/vlist";
    }

    //수정
    // RedirectAttributes : 리디렉션을 수행할 때 한 컨트롤러 메서드에서 다른 컨트롤러
    // 메서드로 Attributes 를 전달하는데 이용한다. -> 필요할 때 : redirect 를 하여 변경된 값을 넘겨줄 때
    // addAttribute : 브라우저의 주소창에 보이게 URL에 추가하여 정보 전달
    // addFlashAttributes : 세션에 저장되고 오직 다음 요청에서만 접근 가능
    @PostMapping("/vupdate/{id}")
    @Transactional
    public String update(@Valid Visitor visitor,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
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
    @GetMapping("/vdelete")
    @Transactional
    public String delete(@RequestParam Integer id,
                         RedirectAttributes redirectAttributes) {
        if (!visitorRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("msg", "삭제할 방명록을 찾을 수 없습니다.");
        }
        return "redirect:/vlist";
    }
}
