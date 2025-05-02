package com.certmaster.aws.domain.service;

import com.certmaster.aws.domain.entity.Certification;

import java.util.List;
import java.util.Optional;

/**
 * 자격증 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface CertificationService {
    
    /**
     * 모든 자격증을 조회합니다.
     * 
     * @return 자격증 목록
     */
    List<Certification> findAllCertifications();
    
    /**
     * ID로 자격증을 조회합니다.
     * 
     * @param id 자격증 ID
     * @return 조회된 자격증 (없는 경우 빈 Optional)
     */
    Optional<Certification> findCertificationById(Long id);
    
    /**
     * 코드로 자격증을 조회합니다.
     * 
     * @param code 자격증 코드
     * @return 조회된 자격증 (없는 경우 빈 Optional)
     */
    Optional<Certification> findCertificationByCode(String code);
    
    /**
     * 새 자격증을 저장합니다.
     * 
     * @param certification 저장할 자격증
     * @return 저장된 자격증
     */
    Certification saveCertification(Certification certification);
    
    /**
     * 외부 API를 통해 AWS 자격증 정보를 가져와 데이터베이스에 저장합니다.
     * 
     * @return 저장된 자격증 목록
     */
    List<Certification> fetchAndSaveAwsCertifications();
    
    /**
     * 특정 레벨의 자격증을 조회합니다.
     * 
     * @param level 자격증 레벨
     * @return 조회된 자격증 목록
     */
    List<Certification> findCertificationsByLevel(String level);
} 