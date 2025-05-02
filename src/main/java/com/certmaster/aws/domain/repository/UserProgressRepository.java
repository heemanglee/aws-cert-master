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
     * 사용자 ID로 진행 상황을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @return 사용자의 진행 상황 목록
     */
    List<UserProgress> findByUserId(String userId);
    
    /**
     * 사용자 ID와 문제 ID로 진행 상황을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param questionId 문제 ID
     * @return 특정 문제에 대한 사용자의 진행 상황
     */
    Optional<UserProgress> findByUserIdAndQuestionId(String userId, Long questionId);
    
    /**
     * 사용자 ID와 자격증 ID로 진행 상황을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param certificationId 자격증 ID
     * @return 해당 자격증에 대한 사용자의 진행 상황 목록
     */
    @Query("SELECT up FROM UserProgress up WHERE up.userId = :userId AND up.question.certification.id = :certificationId")
    List<UserProgress> findByUserIdAndQuestionCertificationId(@Param("userId") String userId, @Param("certificationId") Long certificationId);
    
    /**
     * 특정 자격증에 대한 사용자의 정답 수를 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param certificationId 자격증 ID
     * @return 정답 수
     */
    @Query("SELECT COUNT(up) FROM UserProgress up WHERE up.userId = :userId AND up.question.certification.id = :certificationId AND up.correctCount > 0")
    long countCorrectByUserIdAndCertificationId(@Param("userId") String userId, @Param("certificationId") Long certificationId);
    
    /**
     * 특정 자격증에 대한 사용자의 총 시도 문제 수를 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param certificationId 자격증 ID
     * @return 총 시도 문제 수
     */
    @Query("SELECT COUNT(up) FROM UserProgress up WHERE up.userId = :userId AND up.question.certification.id = :certificationId")
    long countByUserIdAndCertificationId(@Param("userId") String userId, @Param("certificationId") Long certificationId);
} 