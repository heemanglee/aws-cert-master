package com.certmaster.aws.domain.service;

import com.certmaster.aws.domain.entity.UserProgress;

import java.util.List;
import java.util.Map;
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
    
    /**
     * 사용자의 오답 노트에 문제를 추가하거나 제거합니다.
     * 
     * @param userId 사용자 ID
     * @param questionId 문제 ID
     * @param flagged 추가 여부
     * @return 업데이트된 진행 상황
     */
    UserProgress updateReviewFlag(String userId, Long questionId, boolean flagged);
    
    /**
     * 사용자의 오답 노트에 메모를 추가합니다.
     * 
     * @param userId 사용자 ID
     * @param questionId 문제 ID
     * @param note 메모 내용
     * @return 업데이트된 진행 상황
     */
    UserProgress addReviewNote(String userId, Long questionId, String note);
    
    /**
     * 사용자의 오답 노트에 추가된 문제 목록을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @return 오답 노트 목록
     */
    List<UserProgress> findFlaggedForReview(String userId);
    
    /**
     * 특정 자격증에 대한 사용자의 오답 노트 목록을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param certificationId 자격증 ID
     * @return 해당 자격증의 오답 노트 목록
     */
    List<UserProgress> findFlaggedForReviewByCertificationId(String userId, Long certificationId);
    
    /**
     * 특정 자격증에 대한 사용자의 정답률이 낮은 문제 목록을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param certificationId 자격증 ID
     * @param maxCorrectRate 최대 정답률 (0.0 ~ 1.0)
     * @param minAttempts 최소 시도 횟수
     * @return 정답률이 낮은 문제 목록
     */
    List<UserProgress> findLowCorrectRateQuestions(String userId, Long certificationId, float maxCorrectRate, int minAttempts);
    
    /**
     * 사용자의 자격증별 학습 진행 상황 요약을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @return 자격증ID를 키로 하는 진행 상황 맵
     */
    Map<Long, UserProgressSummary> getUserProgressSummary(String userId);
    
    /**
     * 사용자 진행 상황 요약 정보를 담는 내부 클래스
     */
    class UserProgressSummary {
        private final Long certificationId;
        private final String certificationName;
        private final long totalQuestions;
        private final long attemptedQuestions;
        private final long correctQuestions;
        private final long reviewFlaggedQuestions;
        
        public UserProgressSummary(Long certificationId, String certificationName, 
                                  long totalQuestions, long attemptedQuestions, 
                                  long correctQuestions, long reviewFlaggedQuestions) {
            this.certificationId = certificationId;
            this.certificationName = certificationName;
            this.totalQuestions = totalQuestions;
            this.attemptedQuestions = attemptedQuestions;
            this.correctQuestions = correctQuestions;
            this.reviewFlaggedQuestions = reviewFlaggedQuestions;
        }
        
        public Long getCertificationId() {
            return certificationId;
        }
        
        public String getCertificationName() {
            return certificationName;
        }
        
        public long getTotalQuestions() {
            return totalQuestions;
        }
        
        public long getAttemptedQuestions() {
            return attemptedQuestions;
        }
        
        public long getCorrectQuestions() {
            return correctQuestions;
        }
        
        public long getReviewFlaggedQuestions() {
            return reviewFlaggedQuestions;
        }
        
        public double getProgressPercentage() {
            return totalQuestions > 0 ? (double) attemptedQuestions / totalQuestions * 100 : 0;
        }
        
        public double getCorrectPercentage() {
            return attemptedQuestions > 0 ? (double) correctQuestions / attemptedQuestions * 100 : 0;
        }
    }
} 