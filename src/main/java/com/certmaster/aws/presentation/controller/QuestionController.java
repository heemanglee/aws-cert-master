package com.certmaster.aws.presentation.controller;

import com.certmaster.aws.application.dto.QuestionDto;
import com.certmaster.aws.domain.entity.Certification;
import com.certmaster.aws.domain.entity.Question;
import com.certmaster.aws.domain.entity.UserProgress;
import com.certmaster.aws.domain.service.CertificationService;
import com.certmaster.aws.domain.service.QuestionService;
import com.certmaster.aws.domain.service.UserProgressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 문제 풀이 관련 요청을 처리하는 컨트롤러
 */
@Controller
@RequestMapping("/questions")
public class QuestionController {
    
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);
    
    private final QuestionService questionService;
    private final CertificationService certificationService;
    private final UserProgressService userProgressService;
    
    // 임시 사용자 ID (실제로는 인증 시스템과 연동 필요)
    private static final String TEMP_USER_ID = "temp-user";
    
    @Autowired
    public QuestionController(
            QuestionService questionService, 
            CertificationService certificationService,
            UserProgressService userProgressService) {
        this.questionService = questionService;
        this.certificationService = certificationService;
        this.userProgressService = userProgressService;
    }
    
    /**
     * 특정 자격증의 문제 목록을 표시합니다.
     */
    @GetMapping
    public String getQuestions(@RequestParam("certificationId") Long certificationId, Model model) {
        Optional<Certification> certificationOpt = certificationService.findCertificationById(certificationId);
        
        if (!certificationOpt.isPresent()) {
            return "redirect:/";
        }
        
        Certification certification = certificationOpt.get();
        List<Question> questions = questionService.findQuestionsByCertificationId(certificationId);
        
        // 사용자의 진행 상황 가져오기
        List<UserProgress> userProgresses = userProgressService.findByUserIdAndQuestionCertificationId(TEMP_USER_ID, certificationId);
        Map<Long, UserProgress> progressMap = userProgresses.stream()
                .collect(Collectors.toMap(
                    progress -> progress.getQuestion().getId(), 
                    progress -> progress
                ));
        
        List<QuestionDto> questionDtos = questions.stream()
                .map(question -> {
                    QuestionDto dto = QuestionDto.fromEntity(question);
                    // 사용자가 이미 문제를 풀었는지 확인하여 설정
                    UserProgress progress = progressMap.get(question.getId());
                    dto.setAlreadySolved(progress != null && progress.getAttemptCount() > 0);
                    return dto;
                })
                .collect(Collectors.toList());
        
        // 진행 상황 통계 추가
        long totalAnswered = userProgressService.getTotalAnsweredCount(TEMP_USER_ID, certificationId);
        long correctAnswers = userProgressService.getCorrectAnswerCount(TEMP_USER_ID, certificationId);
        
        model.addAttribute("certification", certification);
        model.addAttribute("questions", questionDtos);
        model.addAttribute("totalQuestions", questions.size());
        model.addAttribute("totalAnswered", totalAnswered);
        model.addAttribute("correctAnswers", correctAnswers);
        
        return "question/list";
    }
    
    /**
     * 문제 풀이 페이지를 표시합니다.
     */
    @GetMapping("/{id}")
    public String getQuestion(@PathVariable("id") Long id, Model model) {
        return questionService.findQuestionById(id)
                .map(question -> {
                    QuestionDto questionDto = QuestionDto.fromEntity(question);
                    model.addAttribute("question", questionDto);
                    return "question/solve";
                })
                .orElse("redirect:/");
    }
    
    /**
     * 문제 풀이 결과를 채점합니다.
     */
    @PostMapping("/{id}/submit")
    public String submitAnswer(
            @PathVariable("id") Long id,
            @RequestParam(value = "selectedOptions", required = false) List<Long> selectedOptions,
            Model model) {
        
        // selectedOptions를 final로 만들기 위해 새 변수 생성
        final List<Long> finalSelectedOptions = selectedOptions != null ? selectedOptions : new ArrayList<>();
        
        logger.info("문제 ID: {}, 제출된 답안: {}", id, finalSelectedOptions);
        
        return questionService.findQuestionById(id)
                .map(question -> {
                    QuestionDto questionDto = QuestionDto.fromEntity(question);
                    
                    // 정답 확인
                    List<Long> correctOptionIds = questionDto.getCorrectOptionIds();
                    boolean isCorrect = new HashSet<>(finalSelectedOptions).equals(new HashSet<>(correctOptionIds));
                    
                    logger.info("정답 옵션 ID: {}", correctOptionIds);
                    logger.info("정답 여부: {}", isCorrect);
                    
                    // 각 옵션의 정보 로깅
                    questionDto.getOptions().forEach(option -> 
                        logger.info("옵션 ID: {}, 내용: {}, 정답 여부: {}", 
                            option.getId(), 
                            option.getContent().substring(0, Math.min(30, option.getContent().length())), 
                            option.isCorrect())
                    );
                    
                    // 사용자 진행 상황 기록
                    userProgressService.recordProgress(TEMP_USER_ID, id, isCorrect);
                    
                    model.addAttribute("question", questionDto);
                    model.addAttribute("selectedOptions", finalSelectedOptions);
                    model.addAttribute("isCorrect", isCorrect);
                    model.addAttribute("showResult", true);
                    
                    return "question/result";
                })
                .orElse("redirect:/");
    }
    
    /**
     * 랜덤 문제 풀이 모드를 시작합니다.
     */
    @GetMapping("/random")
    public String getRandomQuestions(
            @RequestParam("certificationId") Long certificationId,
            @RequestParam(value = "count", defaultValue = "10") Integer count,
            Model model) {
        
        Optional<Certification> certificationOpt = certificationService.findCertificationById(certificationId);
        
        if (!certificationOpt.isPresent()) {
            return "redirect:/";
        }
        
        Certification certification = certificationOpt.get();
        List<Question> randomQuestions = questionService.findRandomQuestionsByCertificationId(certificationId, count);
        
        if (randomQuestions.isEmpty()) {
            model.addAttribute("message", "이 자격증에 대한 문제가 없습니다.");
            return "redirect:/certifications/" + certificationId;
        }
        
        List<QuestionDto> questionDtos = randomQuestions.stream()
                .map(QuestionDto::fromEntity)
                .collect(Collectors.toList());
        
        model.addAttribute("certification", certification);
        model.addAttribute("questions", questionDtos);
        model.addAttribute("isRandomMode", true);
        
        return "question/random";
    }
} 