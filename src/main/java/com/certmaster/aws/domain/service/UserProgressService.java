package com.certmaster.aws.domain.service;

import com.certmaster.aws.domain.entity.UserProgress;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 진행 상황 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface UserProgressService {
    
    /**
     * 사용자 ID로 진행 상황을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @return 사용자의 진행 상황 목록
     */
    List<UserProgress> findByUserId(String userId);
    
    /**
     * 사용자 ID와 자격증 ID로 진행 상황을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param certificationId 자격증 ID
     * @return 해당 자격증에 대한 사용자의 진행 상황 목록
     */
    List<UserProgress> findByUserIdAndQuestionCertificationId(String userId, Long certificationId);
    
    /**
     * 사용자 ID와 문제 ID로 진행 상황을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param questionId 문제 ID
     * @return 특정 문제에 대한 사용자의 진행 상황
     */
    Optional<UserProgress> findByUserIdAndQuestionId(String userId, Long questionId);
    
    /**
     * 사용자의 문제 풀이 결과를 기록합니다.
     * 
     * @param userId 사용자 ID
     * @param questionId 문제 ID
     * @param isCorrect 정답 여부
     * @return 저장된 진행 상황
     */
    UserProgress recordProgress(String userId, Long questionId, boolean isCorrect);
    
    /**
     * 특정 자격증에 대한 사용자의 정답 수를 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param certificationId 자격증 ID
     * @return 정답 수
     */
    long getCorrectAnswerCount(String userId, Long certificationId);
    
    /**
     * 특정 자격증에 대한 사용자의 총 시도 문제 수를 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param certificationId 자격증 ID
     * @return 총 시도 문제 수
     */
    long getTotalAnsweredCount(String userId, Long certificationId);
} 