package com.certmaster.aws.infrastructure.config;

import com.certmaster.aws.domain.entity.Certification;
import com.certmaster.aws.domain.entity.Question;
import com.certmaster.aws.domain.service.CertificationService;
import com.certmaster.aws.domain.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * 애플리케이션 시작 시 초기 데이터를 로드하는 설정 클래스
 */
@Configuration
public class DataInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private static final String DUMP_DIR = "/Users/heemang/Desktop/projects/aws/cloud-practitioner";
    
    private final CertificationService certificationService;
    private final QuestionService questionService;
    
    @Autowired
    public DataInitializer(CertificationService certificationService, QuestionService questionService) {
        this.certificationService = certificationService;
        this.questionService = questionService;
    }
    
    /**
     * 애플리케이션 시작 시 실행되는 CommandLineRunner 빈
     * AWS 자격증 정보 및 덤프 문제를 초기 데이터로 로드합니다.
     */
    @Bean
    public CommandLineRunner initData() {
        return args -> {
            logger.info("초기 데이터 로드 시작");
            
            // AWS 자격증 정보 가져오기
            List<Certification> certifications = certificationService.fetchAndSaveAwsCertifications();
            logger.info("AWS 자격증 {} 개가 초기 데이터로 로드되었습니다.", certifications.size());
            
            // Cloud Practitioner 자격증 찾기
            Optional<Certification> cloudPractitioner = certificationService.findCertificationByCode("CLF-C01");
            if (cloudPractitioner.isPresent()) {
                loadCloudPractitionerQuestions(cloudPractitioner.get().getId());
            } else {
                logger.warn("Cloud Practitioner 자격증을 찾을 수 없어 문제를 로드할 수 없습니다.");
            }
            
            logger.info("초기 데이터 로드 완료");
        };
    }
    
    /**
     * Cloud Practitioner 덤프 문제를 로드합니다.
     * 
     * @param certificationId 자격증 ID
     */
    private void loadCloudPractitionerQuestions(Long certificationId) {
        File dumpDir = new File(DUMP_DIR);
        if (!dumpDir.exists() || !dumpDir.isDirectory()) {
            logger.warn("덤프 디렉토리가 존재하지 않습니다: {}", DUMP_DIR);
            return;
        }
        
        File[] dumpFiles = dumpDir.listFiles((dir, name) -> name.startsWith("practice-exam-") && name.endsWith(".md"));
        if (dumpFiles == null || dumpFiles.length == 0) {
            logger.warn("덤프 파일을 찾을 수 없습니다: {}", DUMP_DIR);
            return;
        }
        
        int totalQuestions = 0;
        
        for (File dumpFile : dumpFiles) {
            try {
                logger.info("덤프 파일 로드 중: {}", dumpFile.getName());
                List<Question> questions = questionService.parseAndSaveQuestionsFromFile(certificationId, dumpFile.getAbsolutePath());
                totalQuestions += questions.size();
                logger.info("덤프 파일 {} 에서 {} 개의 문제를 로드했습니다.", dumpFile.getName(), questions.size());
            } catch (Exception e) {
                logger.error("덤프 파일 {} 로드 중 오류 발생: {}", dumpFile.getName(), e.getMessage(), e);
            }
        }
        
        logger.info("총 {} 개의 Cloud Practitioner 문제가 로드되었습니다.", totalQuestions);
    }
} 