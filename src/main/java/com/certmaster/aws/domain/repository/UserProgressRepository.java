package com.certmaster.aws.domain.repository;

import com.certmaster.aws.domain.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 진행 상황 엔티티에 대한 데이터 액세스를 위한 리포지토리 인터페이스
 */
@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    
    /**
     * 특정 사용자의 특정 문제에 대한 진행 상황을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param questionId 문제 ID
     * @return 조회된 진행 상황 (없는 경우 빈 Optional)
     */
    Optional<UserProgress> findByUserIdAndQuestionId(String userId, Long questionId);
    
    /**
     * 특정 사용자의 모든 진행 상황을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @return 조회된 진행 상황 목록
     */
    List<UserProgress> findByUserId(String userId);
    
    /**
     * 특정 사용자의 특정 자격증에 대한 모든 진행 상황을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param certificationId 자격증 ID
     * @return 조회된 진행 상황 목록
     */
    @Query("SELECT up FROM UserProgress up WHERE up.userId = :userId AND up.question.certification.id = :certificationId")
    List<UserProgress> findByUserIdAndCertificationId(@Param("userId") String userId, @Param("certificationId") Long certificationId);
    
    /**
     * 특정 사용자의 특정 자격증에 대한 오답 노트 항목을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param certificationId 자격증 ID
     * @return 조회된 오답 노트 항목 목록
     */
    @Query("SELECT up FROM UserProgress up WHERE up.userId = :userId AND up.question.certification.id = :certificationId AND up.flaggedForReview = true")
    List<UserProgress> findFlaggedReviewByUserIdAndCertificationId(@Param("userId") String userId, @Param("certificationId") Long certificationId);
    
    /**
     * 특정 사용자의 특정 자격증에 대한 맞은 문제 수를 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param certificationId 자격증 ID
     * @return 맞은 문제 수
     */
    @Query("SELECT COUNT(up) FROM UserProgress up WHERE up.userId = :userId AND up.question.certification.id = :certificationId AND up.correct = true")
    long countCorrectByUserIdAndCertificationId(@Param("userId") String userId, @Param("certificationId") Long certificationId);
    
    /**
     * 특정 사용자의 특정 자격증에 대한 시도한 문제 수를 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param certificationId 자격증 ID
     * @return 시도한 문제 수
     */
    @Query("SELECT COUNT(up) FROM UserProgress up WHERE up.userId = :userId AND up.question.certification.id = :certificationId")
    long countAttemptsByUserIdAndCertificationId(@Param("userId") String userId, @Param("certificationId") Long certificationId);
} 