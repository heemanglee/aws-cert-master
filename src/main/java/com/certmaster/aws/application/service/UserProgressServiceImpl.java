package com.certmaster.aws.application.service;

import com.certmaster.aws.domain.entity.Certification;
import com.certmaster.aws.domain.entity.Question;
import com.certmaster.aws.domain.entity.UserProgress;
import com.certmaster.aws.domain.repository.CertificationRepository;
import com.certmaster.aws.domain.repository.QuestionRepository;
import com.certmaster.aws.domain.repository.UserProgressRepository;
import com.certmaster.aws.domain.service.QuestionService;
import com.certmaster.aws.domain.service.UserProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 사용자 진행 상황 서비스 구현 클래스
 */
@Service
public class UserProgressServiceImpl implements UserProgressService {
    
    private final UserProgressRepository userProgressRepository;
    private final QuestionRepository questionRepository;
    private final CertificationRepository certificationRepository;
    private final QuestionService questionService;
    
    @Autowired
    public UserProgressServiceImpl(
            UserProgressRepository userProgressRepository,
            QuestionRepository questionRepository,
            CertificationRepository certificationRepository,
            QuestionService questionService) {
        this.userProgressRepository = userProgressRepository;
        this.questionRepository = questionRepository;
        this.certificationRepository = certificationRepository;
        this.questionService = questionService;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserProgress> findByUserId(String userId) {
        return userProgressRepository.findByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserProgress> findByUserIdAndQuestionCertificationId(String userId, Long certificationId) {
        return userProgressRepository.findByUserIdAndQuestionCertificationId(userId, certificationId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<UserProgress> findByUserIdAndQuestionId(String userId, Long questionId) {
        return userProgressRepository.findByUserIdAndQuestionId(userId, questionId);
    }
    
    @Override
    @Transactional
    public UserProgress recordProgress(String userId, Long questionId, boolean isCorrect) {
        Optional<Question> questionOpt = questionRepository.findById(questionId);
        if (!questionOpt.isPresent()) {
            throw new IllegalArgumentException("문제가 존재하지 않습니다: ID = " + questionId);
        }
        
        Question question = questionOpt.get();
        
        // 기존 진행 상황 조회
        Optional<UserProgress> existingProgressOpt = userProgressRepository.findByUserIdAndQuestionId(userId, questionId);
        UserProgress userProgress;
        
        if (existingProgressOpt.isPresent()) {
            // 기존 진행 상황 업데이트
            userProgress = existingProgressOpt.get();
            userProgress.recordAttempt(isCorrect);
        } else {
            // 새 진행 상황 생성
            userProgress = new UserProgress(userId, question);
            userProgress.recordAttempt(isCorrect);
        }
        
        // 정답을 맞추지 못한 경우 자동으로 오답 노트에 추가
        if (!isCorrect && !userProgress.isFlaggedForReview()) {
            userProgress.setFlaggedForReview(true);
        }
        
        return userProgressRepository.save(userProgress);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getCorrectAnswerCount(String userId, Long certificationId) {
        return userProgressRepository.countCorrectByUserIdAndCertificationId(userId, certificationId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getTotalAnsweredCount(String userId, Long certificationId) {
        return userProgressRepository.countByUserIdAndCertificationId(userId, certificationId);
    }
    
    @Override
    @Transactional
    public UserProgress updateReviewFlag(String userId, Long questionId, boolean flagged) {
        Optional<UserProgress> progressOpt = userProgressRepository.findByUserIdAndQuestionId(userId, questionId);
        
        if (!progressOpt.isPresent()) {
            // 해당 문제에 대한 진행 상황이 없는 경우, 새로 생성
            Optional<Question> questionOpt = questionRepository.findById(questionId);
            if (!questionOpt.isPresent()) {
                throw new IllegalArgumentException("문제가 존재하지 않습니다: ID = " + questionId);
            }
            
            UserProgress newProgress = new UserProgress(userId, questionOpt.get());
            newProgress.setFlaggedForReview(flagged);
            return userProgressRepository.save(newProgress);
        }
        
        UserProgress progress = progressOpt.get();
        progress.setFlaggedForReview(flagged);
        return userProgressRepository.save(progress);
    }
    
    @Override
    @Transactional
    public UserProgress addReviewNote(String userId, Long questionId, String note) {
        Optional<UserProgress> progressOpt = userProgressRepository.findByUserIdAndQuestionId(userId, questionId);
        
        if (!progressOpt.isPresent()) {
            // 해당 문제에 대한 진행 상황이 없는 경우, 새로 생성
            Optional<Question> questionOpt = questionRepository.findById(questionId);
            if (!questionOpt.isPresent()) {
                throw new IllegalArgumentException("문제가 존재하지 않습니다: ID = " + questionId);
            }
            
            UserProgress newProgress = new UserProgress(userId, questionOpt.get());
            newProgress.setFlaggedForReview(true); // 메모가 있으면 자동으로 오답 노트에 추가
            newProgress.setReviewNote(note);
            return userProgressRepository.save(newProgress);
        }
        
        UserProgress progress = progressOpt.get();
        progress.setReviewNote(note);
        if (!progress.isFlaggedForReview()) {
            progress.setFlaggedForReview(true); // 메모가 있으면 자동으로 오답 노트에 추가
        }
        return userProgressRepository.save(progress);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserProgress> findFlaggedForReview(String userId) {
        return userProgressRepository.findByUserIdAndFlaggedForReviewTrue(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserProgress> findFlaggedForReviewByCertificationId(String userId, Long certificationId) {
        return userProgressRepository.findFlaggedForReviewByUserIdAndCertificationId(userId, certificationId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserProgress> findLowCorrectRateQuestions(String userId, Long certificationId, float maxCorrectRate, int minAttempts) {
        return userProgressRepository.findLowCorrectRateByUserIdAndCertificationId(userId, certificationId, maxCorrectRate, minAttempts);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<Long, UserProgressSummary> getUserProgressSummary(String userId) {
        List<Certification> certifications = certificationRepository.findAll();
        
        Map<Long, UserProgressSummary> result = new HashMap<>();
        
        for (Certification certification : certifications) {
            Long certId = certification.getId();
            String certName = certification.getName();
            
            long totalQuestions = questionService.countQuestionsByCertificationId(certId);
            long attemptedQuestions = getTotalAnsweredCount(userId, certId);
            long correctQuestions = getCorrectAnswerCount(userId, certId);
            long reviewFlaggedQuestions = userProgressRepository.findFlaggedForReviewByUserIdAndCertificationId(userId, certId).size();
            
            UserProgressSummary summary = new UserProgressSummary(
                certId, certName, totalQuestions, attemptedQuestions, correctQuestions, reviewFlaggedQuestions
            );
            
            result.put(certId, summary);
        }
        
        return result;
    }
} 