package com.certmaster.aws.presentation.controller;

import com.certmaster.aws.application.dto.CertificationDto;
import com.certmaster.aws.domain.entity.Certification;
import com.certmaster.aws.domain.service.CertificationService;
import com.certmaster.aws.domain.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 메인 페이지와 자격증 관련 요청을 처리하는 컨트롤러
 */
@Controller
public class HomeController {
    
    private final CertificationService certificationService;
    private final QuestionService questionService;
    
    @Autowired
    public HomeController(CertificationService certificationService, QuestionService questionService) {
        this.certificationService = certificationService;
        this.questionService = questionService;
    }
    
    /**
     * 메인 페이지(자격증 목록)를 표시합니다.
     */
    @GetMapping("/")
    public String home(Model model) {
        List<Certification> certifications = certificationService.findAllCertifications();
        
        // 자격증별로 문제 수 계산
        List<CertificationDto> certificationDtos = new ArrayList<>();
        for (Certification certification : certifications) {
            long questionCount = questionService.countQuestionsByCertificationId(certification.getId());
            certificationDtos.add(CertificationDto.fromEntity(certification, questionCount));
        }
        
        // 레벨별로 자격증 그룹화
        Map<String, List<CertificationDto>> certificationsByLevel = certificationDtos.stream()
                .collect(Collectors.groupingBy(CertificationDto::getLevel));
        
        model.addAttribute("certificationsByLevel", certificationsByLevel);
        return "home";
    }
} 