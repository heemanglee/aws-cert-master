package com.certmaster.aws.domain.repository;

import com.certmaster.aws.domain.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 자격증 엔티티에 대한 데이터 액세스를 위한 리포지토리 인터페이스
 */
@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {
    
    /**
     * 자격증 코드로 자격증을 조회합니다.
     * 
     * @param code 자격증 코드
     * @return 조회된 자격증 (없는 경우 빈 Optional)
     */
    Optional<Certification> findByCode(String code);
    
    /**
     * 자격증 이름으로 자격증을 조회합니다.
     * 
     * @param name 자격증 이름
     * @return 조회된 자격증 (없는 경우 빈 Optional)
     */
    Optional<Certification> findByName(String name);
    
    /**
     * 특정 레벨의 모든 자격증을 조회합니다.
     * 
     * @param level 자격증 레벨
     * @return 조회된 자격증 목록
     */
    java.util.List<Certification> findByLevel(String level);
} 