package com.certmaster.aws.application.service;

import com.certmaster.aws.domain.entity.Certification;
import com.certmaster.aws.domain.entity.Question;
import com.certmaster.aws.domain.repository.CertificationRepository;
import com.certmaster.aws.domain.repository.QuestionRepository;
import com.certmaster.aws.domain.service.QuestionService;
import com.certmaster.aws.infrastructure.util.QuestionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 문제 서비스 구현 클래스
 */
@Service
public class QuestionServiceImpl implements QuestionService {
    
    private static final Logger logger = LoggerFactory.getLogger(QuestionServiceImpl.class);
    
    private final QuestionRepository questionRepository;
    private final CertificationRepository certificationRepository;
    private final QuestionParser questionParser;
    
    @Autowired
    public QuestionServiceImpl(
            QuestionRepository questionRepository,
            CertificationRepository certificationRepository,
            QuestionParser questionParser) {
        this.questionRepository = questionRepository;
        this.certificationRepository = certificationRepository;
        this.questionParser = questionParser;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Question> findAllQuestions() {
        return questionRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Question> findQuestionById(Long id) {
        return questionRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Question> findQuestionsByCertificationId(Long certificationId) {
        return questionRepository.findByCertificationId(certificationId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countQuestionsByCertificationId(Long certificationId) {
        return questionRepository.countByCertificationId(certificationId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Question> findRandomQuestionsByCertificationId(Long certificationId, int limit) {
        return questionRepository.findRandomQuestionsByCertificationId(certificationId, limit);
    }
    
    @Override
    @Transactional
    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }
    
    @Override
    @Transactional
    public List<Question> parseAndSaveQuestionsFromFile(Long certificationId, String filePath) throws IOException {
        logger.info("파일에서 문제를 파싱합니다: {}", filePath);
        
        Optional<Certification> certificationOpt = certificationRepository.findById(certificationId);
        if (!certificationOpt.isPresent()) {
            logger.error("자격증이 존재하지 않습니다: ID = {}", certificationId);
            throw new IllegalArgumentException("자격증이 존재하지 않습니다: ID = " + certificationId);
        }
        
        Certification certification = certificationOpt.get();
        List<Question> parsedQuestions = questionParser.parseQuestionsFromMarkdown(filePath);
        List<Question> savedQuestions = new ArrayList<>();
        
        for (Question question : parsedQuestions) {
            // 번역 적용
            Question translatedQuestion = questionParser.translateQuestionToKorean(question);
            
            // 자격증 연결
            translatedQuestion.setCertification(certification);
            
            // 저장
            Question savedQuestion = questionRepository.save(translatedQuestion);
            savedQuestions.add(savedQuestion);
        }
        
        logger.info("{}개의 문제가 파싱되어 저장되었습니다.", savedQuestions.size());
        return savedQuestions;
    }
} 