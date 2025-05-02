package com.certmaster.aws.presentation.controller;

import com.certmaster.aws.application.dto.CertificationDto;
import com.certmaster.aws.domain.entity.Certification;
import com.certmaster.aws.domain.service.CertificationService;
import com.certmaster.aws.domain.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 자격증 관련 요청을 처리하는 컨트롤러
 */
@Controller
@RequestMapping("/certifications")
public class CertificationController {
    
    private final CertificationService certificationService;
    private final QuestionService questionService;
    
    @Autowired
    public CertificationController(CertificationService certificationService, QuestionService questionService) {
        this.certificationService = certificationService;
        this.questionService = questionService;
    }
    
    /**
     * 특정 자격증의 상세 정보를 보여줍니다.
     */
    @GetMapping("/{id}")
    public String getCertificationDetail(@PathVariable("id") Long id, Model model) {
        return certificationService.findCertificationById(id)
                .map(certification -> {
                    long questionCount = questionService.countQuestionsByCertificationId(id);
                    CertificationDto certificationDto = CertificationDto.fromEntity(certification, questionCount);
                    
                    model.addAttribute("certification", certificationDto);
                    return "certification/detail";
                })
                .orElse("redirect:/");
    }
    
    /**
     * 특정 자격증의 문제 풀이 페이지로 이동합니다.
     */
    @GetMapping("/{id}/questions")
    public String getQuestions(@PathVariable("id") Long id, Model model) {
        return certificationService.findCertificationById(id)
                .map(certification -> {
                    // 리다이렉트가 아닌 정확한 URL로 포워딩
                    return "redirect:/questions?certificationId=" + id;
                })
                .orElse("redirect:/");
    }
} 