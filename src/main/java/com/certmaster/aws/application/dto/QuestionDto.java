package com.certmaster.aws.application.dto;

import com.certmaster.aws.domain.entity.Option;
import com.certmaster.aws.domain.entity.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 문제 정보를 담는 DTO 클래스
 */
public class QuestionDto {
    private Long id;
    private String content;
    private String explanation;
    private List<OptionDto> options = new ArrayList<>();
    private Long certificationId;
    private String certificationName;
    private boolean alreadySolved; // 사용자가 이미 푼 문제인지 여부
    
    // 기본 생성자
    public QuestionDto() {
    }
    
    // 엔티티에서 DTO 생성
    public static QuestionDto fromEntity(Question question) {
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setContent(question.getContent());
        dto.setExplanation(question.getExplanation());
        
        if (question.getCertification() != null) {
            dto.setCertificationId(question.getCertification().getId());
            dto.setCertificationName(question.getCertification().getName());
        }
        
        // 보기 변환
        List<OptionDto> optionDtos = question.getOptions().stream()
                .map(OptionDto::fromEntity)
                .collect(Collectors.toList());
        dto.setOptions(optionDtos);
        
        return dto;
    }
    
    // Getter 및 Setter
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getExplanation() {
        return explanation;
    }
    
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
    
    public List<OptionDto> getOptions() {
        return options;
    }
    
    public void setOptions(List<OptionDto> options) {
        this.options = options;
    }
    
    public Long getCertificationId() {
        return certificationId;
    }
    
    public void setCertificationId(Long certificationId) {
        this.certificationId = certificationId;
    }
    
    public String getCertificationName() {
        return certificationName;
    }
    
    public void setCertificationName(String certificationName) {
        this.certificationName = certificationName;
    }
    
    public boolean isAlreadySolved() {
        return alreadySolved;
    }
    
    public void setAlreadySolved(boolean alreadySolved) {
        this.alreadySolved = alreadySolved;
    }
    
    /**
     * 이 문제의 정답인 보기 ID 목록을 반환합니다.
     * 
     * @return 정답 보기 ID 목록
     */
    public List<Long> getCorrectOptionIds() {
        return options.stream()
                .filter(OptionDto::isCorrect)
                .map(OptionDto::getId)
                .collect(Collectors.toList());
    }
} 