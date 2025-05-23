package com.certmaster.aws.infrastructure.util;

import com.certmaster.aws.domain.entity.Option;
import com.certmaster.aws.domain.entity.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 덤프 파일에서 문제를 파싱하는 유틸리티 클래스
 */
@Component
public class QuestionParser {
    private static final Logger logger = LoggerFactory.getLogger(QuestionParser.class);
    
    // 문제 시작 패턴 (예: "1. AWS allows users...")
    private static final Pattern QUESTION_PATTERN = Pattern.compile("^(\\d+)\\. (.+)$");
    
    // 보기 패턴 (예: "- A. AWS CLI.")
    private static final Pattern OPTION_PATTERN = Pattern.compile("^\\s*- ([A-Z])\\. (.+)$");
    
    // 정답 패턴 (예: "Correct answer: D" 또는 "Correct answer: B, E")
    private static final Pattern ANSWER_PATTERN = Pattern.compile("Correct answer:\\s*([A-Z](,\\s*[A-Z])*)");
    
    // 새로운 형식의 정답 패턴 (예: "Correct Answer: B")
    private static final Pattern ANSWER_PATTERN_ALT = Pattern.compile("Correct Answer:\\s*([A-Z](,\\s*[A-Z])*)");
    
    /**
     * 마크다운 파일에서 문제와 보기를 파싱합니다.
     * 
     * @param filePath 마크다운 파일 경로
     * @return 파싱된 문제 목록
     * @throws IOException 파일 읽기 오류 발생 시
     */
    public List<Question> parseQuestionsFromMarkdown(String filePath) throws IOException {
        List<Question> questions = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            Question currentQuestion = null;
            List<Option> currentOptions = new ArrayList<>();
            List<String> correctAnswers = new ArrayList<>();
            StringBuilder questionContent = new StringBuilder();
            StringBuilder explanation = new StringBuilder();
            boolean isReadingQuestion = false;
            boolean isReadingOptions = false;
            boolean isReadingAnswer = false;
            
            while ((line = reader.readLine()) != null) {
                Matcher questionMatcher = QUESTION_PATTERN.matcher(line);
                
                // 새로운 문제를 발견한 경우
                if (questionMatcher.find()) {
                    // 이전 문제가 있다면 저장
                    if (currentQuestion != null) {
                        finalizeQuestion(currentQuestion, currentOptions, correctAnswers, explanation.toString());
                        questions.add(currentQuestion);
                    }
                    
                    // 새 문제 초기화 - 기본 생성자 대신 파라미터가 있는 생성자 사용
                    String content = questionMatcher.group(2);
                    currentQuestion = new Question(content, "");
                    currentOptions = new ArrayList<>();
                    correctAnswers = new ArrayList<>();
                    questionContent = new StringBuilder(content);
                    explanation = new StringBuilder();
                    isReadingQuestion = true;
                    isReadingOptions = false;
                    isReadingAnswer = false;
                    continue;
                }
                
                // 보기 처리
                Matcher optionMatcher = OPTION_PATTERN.matcher(line);
                if (optionMatcher.find()) {
                    if (isReadingQuestion) {
                        currentQuestion.setContent(questionContent.toString().trim());
                        isReadingQuestion = false;
                    }
                    isReadingOptions = true;
                    
                    String optionLetter = optionMatcher.group(1);
                    String optionContent = optionMatcher.group(2);
                    
                    // 옵션 내용에 알파벳 식별자를 포함하여 저장
                    String fullOptionContent = optionLetter + ". " + optionContent;
                    Option option = new Option(fullOptionContent, false); // 일단 정답 여부는 false로 설정
                    
                    logger.info("옵션 추가: {} - {}", optionLetter, optionContent.substring(0, Math.min(30, optionContent.length())));
                    currentOptions.add(option);
                    continue;
                }
                
                // 정답 처리
                if (line.contains("<details") || line.contains("</details>")) {
                    continue; // 마크다운 상세 태그 무시
                }
                
                if (line.contains("<summary") || line.contains("</summary>")) {
                    continue; // 마크다운 요약 태그 무시
                }
                
                Matcher answerMatcher = ANSWER_PATTERN.matcher(line);
                if (answerMatcher.find()) {
                    isReadingAnswer = true;
                    isReadingOptions = false;
                    
                    String answers = answerMatcher.group(1);
                    for (String answer : answers.split(",\\s*")) {
                        correctAnswers.add(answer.trim());
                    }
                    continue;
                }
                
                // 새로운 형식의 정답 패턴 체크
                Matcher altAnswerMatcher = ANSWER_PATTERN_ALT.matcher(line);
                if (altAnswerMatcher.find()) {
                    isReadingAnswer = true;
                    isReadingOptions = false;
                    
                    String answers = altAnswerMatcher.group(1);
                    logger.info("새 형식으로 정답 찾음: {}", answers);
                    for (String answer : answers.split(",\\s*")) {
                        correctAnswers.add(answer.trim());
                    }
                    continue;
                }
                
                // 문제 내용 추가
                if (isReadingQuestion && !line.trim().isEmpty() && !line.contains("---")) {
                    questionContent.append(" ").append(line.trim());
                }
                
                // 설명 추가
                if (isReadingAnswer && !line.contains("Correct answer:") && !line.trim().isEmpty()) {
                    explanation.append(line.trim()).append(" ");
                }
            }
            
            // 마지막 문제 처리
            if (currentQuestion != null) {
                finalizeQuestion(currentQuestion, currentOptions, correctAnswers, explanation.toString());
                questions.add(currentQuestion);
            }
        }
        
        return questions;
    }
    
    /**
     * 문제, 보기, 정답 정보를 모두 취합하여 최종 문제 객체를 구성합니다.
     * 
     * @param question 문제 객체
     * @param options 보기 목록
     * @param correctAnswers 정답 목록
     * @param explanation 설명
     */
    private void finalizeQuestion(Question question, List<Option> options, List<String> correctAnswers, String explanation) {
        logger.info("파싱된 문제: {}", question.getContent().substring(0, Math.min(50, question.getContent().length())));
        logger.info("파싱된 정답: {}", correctAnswers);
        
        // 각 옵션에 대한 알파벳 식별자와 내용을 매핑
        Map<String, Option> optionMap = new HashMap<>();
        for (Option option : options) {
            String content = option.getContent();
            if (content.length() >= 1) {
                // 옵션 내용에서 알파벳 추출 시도
                Matcher matcher = Pattern.compile("^([A-Z])\\. ").matcher(content);
                if (matcher.find()) {
                    String letter = matcher.group(1);
                    optionMap.put(letter, option);
                    logger.info("옵션 매핑: {} -> {}", letter, content.substring(0, Math.min(30, content.length())));
                }
            }
        }
        
        // 정답 설정
        for (String correctAnswer : correctAnswers) {
            Option option = optionMap.get(correctAnswer);
            if (option != null) {
                option.setCorrect(true);
                logger.info("정답으로 설정: {}", correctAnswer);
            } else {
                logger.warn("정답 매핑 실패: {}", correctAnswer);
            }
        }
        
        // 옵션을 문제에 추가
        for (Option option : options) {
            question.addOption(option);
            logger.info("옵션 추가: 정답={}, 내용={}", option.isCorrect(), 
                option.getContent().substring(0, Math.min(30, option.getContent().length())));
        }
        
        // 설명 설정
        question.setExplanation(explanation.trim());
    }
    
    /**
     * 영어 문제와 보기를 한글로 번역합니다.
     * (실제 환경에서는 외부 번역 API를 사용하거나 미리 번역된 내용을 사용할 수 있습니다)
     * 
     * @param question 번역할 문제
     * @return 번역된 문제
     */
    public Question translateQuestionToKorean(Question question) {
        // 번역을 사용하지 않고 원본 내용을 그대로 유지
        
        // 보기 유지 (원본 내용을 그대로 사용)
        for (Option option : question.getOptions()) {
            // 원본 내용 유지, [번역] 접두사 사용하지 않음
            option.setContent(option.getContent());
        }
        
        return question;
    }
} 