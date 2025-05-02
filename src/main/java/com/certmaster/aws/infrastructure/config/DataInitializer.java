package com.certmaster.aws.infrastructure.config;

import com.certmaster.aws.domain.entity.Certification;
import com.certmaster.aws.domain.service.CertificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 애플리케이션 시작 시 초기 데이터를 로드하는 설정 클래스
 */
@Configuration
public class DataInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    private final CertificationService certificationService;
    
    @Autowired
    public DataInitializer(CertificationService certificationService) {
        this.certificationService = certificationService;
    }
    
    /**
     * 애플리케이션 시작 시 실행되는 CommandLineRunner 빈
     * AWS 자격증 정보를 초기 데이터로 로드합니다.
     */
    @Bean
    public CommandLineRunner initData() {
        return args -> {
            logger.info("초기 데이터 로드 시작");
            
            // AWS 자격증 정보 가져오기
            List<Certification> certifications = certificationService.fetchAndSaveAwsCertifications();
            logger.info("AWS 자격증 {} 개가 초기 데이터로 로드되었습니다.", certifications.size());
            
            logger.info("초기 데이터 로드 완료");
        };
    }
} 