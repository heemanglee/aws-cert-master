package com.certmaster.aws.domain.service;

import com.certmaster.aws.domain.entity.Question;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * 문제 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface QuestionService {
    
    /**
     * 모든 문제를 조회합니다.
     * 
     * @return 문제 목록
     */
    List<Question> findAllQuestions();
    
    /**
     * ID로 문제를 조회합니다.
     * 
     * @param id 문제 ID
     * @return 조회된 문제 (없는 경우 빈 Optional)
     */
    Optional<Question> findQuestionById(Long id);
    
    /**
     * 특정 자격증에 속한 모든 문제를 조회합니다.
     * 
     * @param certificationId 자격증 ID
     * @return 조회된 문제 목록
     */
    List<Question> findQuestionsByCertificationId(Long certificationId);
    
    /**
     * 특정 자격증에 속한 문제 수를 조회합니다.
     * 
     * @param certificationId 자격증 ID
     * @return 문제 수
     */
    long countQuestionsByCertificationId(Long certificationId);
    
    /**
     * 특정 자격증에 속한 문제를 랜덤하게 가져옵니다.
     * 
     * @param certificationId 자격증 ID
     * @param limit 가져올 문제 수
     * @return 무작위로 선택된 문제 목록
     */
    List<Question> findRandomQuestionsByCertificationId(Long certificationId, int limit);
    
    /**
     * 문제를 저장합니다.
     * 
     * @param question 저장할 문제
     * @return 저장된 문제
     */
    Question saveQuestion(Question question);
    
    /**
     * 문제 덤프 파일을 파싱하여 데이터베이스에 저장합니다.
     * 
     * @param certificationId 자격증 ID
     * @param filePath 덤프 파일 경로
     * @return 저장된 문제 목록
     */
    List<Question> parseAndSaveQuestionsFromFile(Long certificationId, String filePath) throws IOException;
} 