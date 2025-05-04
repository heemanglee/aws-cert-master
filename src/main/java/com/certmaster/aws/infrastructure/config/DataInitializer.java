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
import java.util.Arrays;
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
        
        // 파일명 기준으로 정렬 (숫자 순서대로)
        Arrays.sort(dumpFiles, (f1, f2) -> {
            // 파일명에서 숫자 부분 추출 (예: practice-exam-1.md -> 1)
            String name1 = f1.getName();
            String name2 = f2.getName();
            
            try {
                int num1 = extractFileNumber(name1);
                int num2 = extractFileNumber(name2);
                return Integer.compare(num1, num2);
            } catch (NumberFormatException e) {
                // 숫자 추출에 실패하면 파일명 자체로 비교
                return name1.compareTo(name2);
            }
        });
        
        logger.info("총 {} 개의 덤프 파일을 찾았습니다.", dumpFiles.length);
        for (File file : dumpFiles) {
            logger.info("정렬된 덤프 파일: {}", file.getName());
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
    
    /**
     * 파일 이름에서 숫자 부분을 추출합니다.
     * 예: practice-exam-1.md -> 1
     */
    private int extractFileNumber(String filename) {
        // 파일명에서 "practice-exam-" 다음부터 ".md" 전까지의 부분 추출
        int startIndex = filename.indexOf("-exam-") + 6;
        int endIndex = filename.lastIndexOf(".md");
        
        if (startIndex >= 0 && endIndex > startIndex) {
            String numberPart = filename.substring(startIndex, endIndex);
            return Integer.parseInt(numberPart);
        }
        
        throw new NumberFormatException("파일명에서 숫자를 추출할 수 없습니다: " + filename);
    }
} 