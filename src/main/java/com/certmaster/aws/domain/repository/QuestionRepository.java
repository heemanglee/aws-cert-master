package com.certmaster.aws.domain.repository;

import com.certmaster.aws.domain.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 문제 엔티티에 대한 데이터 액세스를 위한 리포지토리 인터페이스
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    /**
     * 특정 자격증에 속한 모든 문제를 조회합니다.
     * 
     * @param certificationId 자격증 ID
     * @return 조회된 문제 목록
     */
    List<Question> findByCertificationId(Long certificationId);
    
    /**
     * 특정 자격증에 속한 문제 수를 조회합니다.
     * 
     * @param certificationId 자격증 ID
     * @return 문제 수
     */
    long countByCertificationId(Long certificationId);
    
    /**
     * 특정 자격증에 속한 문제를 랜덤하게 가져옵니다.
     * 
     * @param certificationId 자격증 ID
     * @param limit 가져올 문제 수
     * @return 무작위로 선택된 문제 목록
     */
    @Query(value = "SELECT * FROM questions WHERE certification_id = :certificationId ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestionsByCertificationId(@Param("certificationId") Long certificationId, @Param("limit") int limit);
} 