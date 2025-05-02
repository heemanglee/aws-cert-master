package com.certmaster.aws.application.service;

import com.certmaster.aws.domain.entity.Certification;
import com.certmaster.aws.domain.repository.CertificationRepository;
import com.certmaster.aws.domain.service.CertificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 자격증 서비스 구현 클래스
 */
@Service
public class CertificationServiceImpl implements CertificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(CertificationServiceImpl.class);
    
    private final CertificationRepository certificationRepository;

    @Autowired
    public CertificationServiceImpl(CertificationRepository certificationRepository) {
        this.certificationRepository = certificationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Certification> findAllCertifications() {
        return certificationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Certification> findCertificationById(Long id) {
        return certificationRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Certification> findCertificationByCode(String code) {
        return certificationRepository.findByCode(code);
    }

    @Override
    @Transactional
    public Certification saveCertification(Certification certification) {
        return certificationRepository.save(certification);
    }

    @Override
    @Transactional
    public List<Certification> fetchAndSaveAwsCertifications() {
        logger.info("AWS 자격증 정보 가져오기 시작");
        
        // 실제로는 여기서 AWS API나 웹 크롤링을 통해 자격증 정보를 가져와야 하지만,
        // 현재는 하드코딩된 데이터로 제공합니다.
        List<Certification> awsCertifications = getHardCodedAwsCertifications();
        
        List<Certification> savedCertifications = new ArrayList<>();
        for (Certification cert : awsCertifications) {
            Optional<Certification> existingCert = certificationRepository.findByCode(cert.getCode());
            if (existingCert.isPresent()) {
                logger.info("이미 존재하는 자격증: {}", cert.getCode());
                continue;
            }
            
            Certification savedCert = certificationRepository.save(cert);
            savedCertifications.add(savedCert);
            logger.info("저장된 자격증: {}", savedCert.getCode());
        }
        
        logger.info("AWS 자격증 정보 가져오기 완료: {} 개의 새 자격증 저장됨", savedCertifications.size());
        return savedCertifications;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Certification> findCertificationsByLevel(String level) {
        return certificationRepository.findByLevel(level);
    }
    
    /**
     * 하드코딩된 AWS 자격증 정보를 반환합니다.
     * 실제 애플리케이션에서는 AWS API나 웹 크롤링으로 대체되어야 합니다.
     */
    private List<Certification> getHardCodedAwsCertifications() {
        List<Certification> certifications = new ArrayList<>();
        
        // AWS 기초 자격증
        certifications.add(new Certification(
            "CLF-C01", 
            "AWS Certified Cloud Practitioner", 
            "AWS 클라우드의 이점, AWS 서비스 개요, 보안, 아키텍처, 요금 및 지원에 대한 이해를 검증하는 기초 수준 자격증",
            100.0,
            "영어, 한국어, 일본어, 중국어, 프랑스어, 독일어, 이탈리아어, 포르투갈어, 스페인어",
            "Foundational"
        ));
        
        // 어소시에이트 레벨 자격증
        certifications.add(new Certification(
            "SAA-C03", 
            "AWS Certified Solutions Architect - Associate", 
            "AWS에서 분산 시스템을 설계하고 배포하는 방법에 대한 이해를 검증하는 자격증",
            150.0,
            "영어, 한국어, 일본어, 중국어, 프랑스어, 독일어, 이탈리아어, 포르투갈어, 스페인어",
            "Associate"
        ));
        
        certifications.add(new Certification(
            "DVA-C01", 
            "AWS Certified Developer - Associate", 
            "AWS 서비스를 사용하여 애플리케이션을 개발하고 유지 관리하는 능력을 검증하는 자격증",
            150.0,
            "영어, 한국어, 일본어, 중국어",
            "Associate"
        ));
        
        certifications.add(new Certification(
            "SOA-C02", 
            "AWS Certified SysOps Administrator - Associate", 
            "AWS 환경의 배포, 관리 및 운영 능력을 검증하는 자격증",
            150.0,
            "영어, 한국어, 일본어, 중국어",
            "Associate"
        ));
        
        // 프로페셔널 레벨 자격증
        certifications.add(new Certification(
            "SAP-C01", 
            "AWS Certified Solutions Architect - Professional", 
            "AWS에서 복잡한 분산 시스템을 설계하고 배포하는 고급 능력을 검증하는 자격증",
            300.0,
            "영어, 한국어, 일본어, 중국어",
            "Professional"
        ));
        
        certifications.add(new Certification(
            "DOP-C01", 
            "AWS Certified DevOps Engineer - Professional", 
            "AWS에서 CI/CD 파이프라인을 구현하고 운영 자동화를 설계하는 능력을 검증하는 자격증",
            300.0,
            "영어, 한국어, 일본어, 중국어",
            "Professional"
        ));
        
        // 전문 분야 자격증
        certifications.add(new Certification(
            "MLS-C01", 
            "AWS Certified Machine Learning - Specialty", 
            "AWS에서 기계 학습 솔루션을 설계, 구현 및 배포하는 능력을 검증하는 자격증",
            300.0,
            "영어",
            "Specialty"
        ));
        
        certifications.add(new Certification(
            "ANS-C01", 
            "AWS Certified Advanced Networking - Specialty", 
            "AWS에서 복잡한 네트워킹 작업을 수행하는 능력을 검증하는 자격증",
            300.0,
            "영어",
            "Specialty"
        ));
        
        certifications.add(new Certification(
            "DAS-C01", 
            "AWS Certified Data Analytics - Specialty", 
            "AWS 데이터 분석 서비스를 사용하여 데이터 처리 솔루션을 설계하고 구현하는 능력을 검증하는 자격증",
            300.0,
            "영어",
            "Specialty"
        ));
        
        certifications.add(new Certification(
            "SCS-C01", 
            "AWS Certified Security - Specialty", 
            "AWS 보안 서비스를 활용하여 안전한 애플리케이션을 설계하고 구현하는 능력을 검증하는 자격증",
            300.0,
            "영어",
            "Specialty"
        ));
        
        return certifications;
    }
} 