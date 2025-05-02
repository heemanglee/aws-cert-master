package com.certmaster.aws.presentation.controller;

import com.certmaster.aws.application.dto.QuestionDto;
import com.certmaster.aws.domain.entity.Certification;
import com.certmaster.aws.domain.entity.Question;
import com.certmaster.aws.domain.entity.UserProgress;
import com.certmaster.aws.domain.service.CertificationService;
import com.certmaster.aws.domain.service.QuestionService;
import com.certmaster.aws.domain.service.UserProgressService;
import com.certmaster.aws.domain.service.UserProgressService.UserProgressSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 마이 페이지 및 오답 노트 관련 요청을 처리하는 컨트롤러
 */
@Controller
@RequestMapping("/mypage")
public class MyPageController {
    
    private final UserProgressService userProgressService;
    private final QuestionService questionService;
    private final CertificationService certificationService;
    
    // 임시 사용자 ID (실제로는 인증 시스템과 연동 필요)
    private static final String TEMP_USER_ID = "temp-user";
    
    @Autowired
    public MyPageController(
            UserProgressService userProgressService, 
            QuestionService questionService,
            CertificationService certificationService) {
        this.userProgressService = userProgressService;
        this.questionService = questionService;
        this.certificationService = certificationService;
    }
    
    /**
     * 마이 페이지 메인 화면을 표시합니다.
     * 사용자의 전체 학습 진행 상황을 보여줍니다.
     */
    @GetMapping
    public String getMyPage(Model model) {
        Map<Long, UserProgressSummary> summaryMap = userProgressService.getUserProgressSummary(TEMP_USER_ID);
        
        model.addAttribute("summaryMap", summaryMap);
        return "mypage/index";
    }
    
    /**
     * 특정 자격증의 학습 상세 현황을 표시합니다.
     */
    @GetMapping("/certifications/{certificationId}")
    public String getCertificationProgress(
            @PathVariable("certificationId") Long certificationId,
            Model model) {
        
        Optional<Certification> certificationOpt = certificationService.findCertificationById(certificationId);
        
        if (!certificationOpt.isPresent()) {
            return "redirect:/mypage";
        }
        
        Certification certification = certificationOpt.get();
        Map<Long, UserProgressSummary> summaryMap = userProgressService.getUserProgressSummary(TEMP_USER_ID);
        UserProgressSummary summary = summaryMap.get(certificationId);
        
        List<UserProgress> progressList = userProgressService.findByUserIdAndQuestionCertificationId(TEMP_USER_ID, certificationId);
        
        model.addAttribute("certification", certification);
        model.addAttribute("summary", summary);
        model.addAttribute("progressList", progressList);
        
        return "mypage/certification_progress";
    }
    
    /**
     * 오답 노트 목록을 표시합니다.
     */
    @GetMapping("/review")
    public String getReviewNotes(Model model) {
        List<UserProgress> reviewList = userProgressService.findFlaggedForReview(TEMP_USER_ID);
        
        // 자격증 ID별로 그룹화
        Map<Long, List<UserProgress>> groupedReviews = reviewList.stream()
                .collect(Collectors.groupingBy(progress -> progress.getQuestion().getCertification().getId()));
        
        model.addAttribute("groupedReviews", groupedReviews);
        return "mypage/review_notes";
    }
    
    /**
     * 특정 자격증의 오답 노트를 표시합니다.
     */
    @GetMapping("/review/{certificationId}")
    public String getCertificationReviewNotes(
            @PathVariable("certificationId") Long certificationId,
            Model model) {
        
        Optional<Certification> certificationOpt = certificationService.findCertificationById(certificationId);
        
        if (!certificationOpt.isPresent()) {
            return "redirect:/mypage/review";
        }
        
        Certification certification = certificationOpt.get();
        List<UserProgress> reviewList = userProgressService.findFlaggedForReviewByCertificationId(TEMP_USER_ID, certificationId);
        
        List<QuestionDto> questionDtos = reviewList.stream()
                .map(UserProgress::getQuestion)
                .map(QuestionDto::fromEntity)
                .collect(Collectors.toList());
        
        model.addAttribute("certification", certification);
        model.addAttribute("reviewList", reviewList);
        model.addAttribute("questionDtos", questionDtos);
        
        return "mypage/certification_review";
    }
    
    /**
     * 오답 노트에 문제를 추가하거나 제거합니다.
     */
    @PostMapping("/review/toggle")
    @ResponseBody
    public ResponseEntity<?> toggleReviewFlag(
            @RequestParam("questionId") Long questionId,
            @RequestParam("flagged") boolean flagged) {
        
        UserProgress progress = userProgressService.updateReviewFlag(TEMP_USER_ID, questionId, flagged);
        
        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "flagged", progress.isFlaggedForReview()
        ));
    }
    
    /**
     * 오답 노트에 메모를 추가합니다.
     */
    @PostMapping("/review/note")
    @ResponseBody
    public ResponseEntity<?> addReviewNote(
            @RequestParam("questionId") Long questionId,
            @RequestParam("note") String note) {
        
        UserProgress progress = userProgressService.addReviewNote(TEMP_USER_ID, questionId, note);
        
        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "note", progress.getReviewNote()
        ));
    }
    
    /**
     * 정답률이 낮은 문제 목록을 표시합니다.
     */
    @GetMapping("/weak-points/{certificationId}")
    public String getWeakPoints(
            @PathVariable("certificationId") Long certificationId,
            @RequestParam(value = "maxRate", defaultValue = "0.5") float maxRate,
            @RequestParam(value = "minAttempts", defaultValue = "2") int minAttempts,
            Model model) {
        
        Optional<Certification> certificationOpt = certificationService.findCertificationById(certificationId);
        
        if (!certificationOpt.isPresent()) {
            return "redirect:/mypage";
        }
        
        Certification certification = certificationOpt.get();
        List<UserProgress> weakPoints = userProgressService.findLowCorrectRateQuestions(TEMP_USER_ID, certificationId, maxRate, minAttempts);
        
        List<QuestionDto> questionDtos = weakPoints.stream()
                .map(UserProgress::getQuestion)
                .map(QuestionDto::fromEntity)
                .collect(Collectors.toList());
        
        model.addAttribute("certification", certification);
        model.addAttribute("weakPoints", weakPoints);
        model.addAttribute("questionDtos", questionDtos);
        model.addAttribute("maxRate", maxRate);
        model.addAttribute("minAttempts", minAttempts);
        
        return "mypage/weak_points";
    }
} 