package com.certmaster.aws.application.service;

import com.certmaster.aws.domain.entity.Question;
import com.certmaster.aws.domain.entity.UserProgress;
import com.certmaster.aws.domain.repository.QuestionRepository;
import com.certmaster.aws.domain.repository.UserProgressRepository;
import com.certmaster.aws.domain.service.UserProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 사용자 진행 상황 서비스 구현 클래스
 */
@Service
public class UserProgressServiceImpl implements UserProgressService {
    
    private final UserProgressRepository userProgressRepository;
    private final QuestionRepository questionRepository;
    
    @Autowired
    public UserProgressServiceImpl(
            UserProgressRepository userProgressRepository,
            QuestionRepository questionRepository) {
        this.userProgressRepository = userProgressRepository;
        this.questionRepository = questionRepository;
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
            userProgress.setAttemptCount(userProgress.getAttemptCount() + 1);
            
            if (isCorrect) {
                userProgress.setCorrectCount(userProgress.getCorrectCount() + 1);
                userProgress.setLastCorrectTime(LocalDateTime.now());
            }
            
            userProgress.setLastAttemptTime(LocalDateTime.now());
        } else {
            // 새 진행 상황 생성
            userProgress = new UserProgress();
            userProgress.setUserId(userId);
            userProgress.setQuestion(question);
            userProgress.setAttemptCount(1);
            userProgress.setCorrectCount(isCorrect ? 1 : 0);
            userProgress.setFirstAttemptTime(LocalDateTime.now());
            userProgress.setLastAttemptTime(LocalDateTime.now());
            
            if (isCorrect) {
                userProgress.setLastCorrectTime(LocalDateTime.now());
            }
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
} 