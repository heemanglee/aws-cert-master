package com.certmaster.aws.application.dto;

import com.certmaster.aws.domain.entity.Certification;

/**
 * 자격증 정보를 담는 DTO 클래스
 */
public class CertificationDto {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Double examCost;
    private String supportedLanguages;
    private String level;
    private long questionCount;
    
    // 기본 생성자
    public CertificationDto() {
    }
    
    // 엔티티에서 DTO 생성
    public static CertificationDto fromEntity(Certification certification) {
        CertificationDto dto = new CertificationDto();
        dto.setId(certification.getId());
        dto.setCode(certification.getCode());
        dto.setName(certification.getName());
        dto.setDescription(certification.getDescription());
        dto.setExamCost(certification.getExamCost());
        dto.setSupportedLanguages(certification.getSupportedLanguages());
        dto.setLevel(certification.getLevel());
        dto.setQuestionCount(certification.getQuestions().size());
        return dto;
    }
    
    // 엔티티에서 DTO 생성 (문제 수 직접 지정)
    public static CertificationDto fromEntity(Certification certification, long questionCount) {
        CertificationDto dto = fromEntity(certification);
        dto.setQuestionCount(questionCount);
        return dto;
    }
    
    // Getter 및 Setter
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Double getExamCost() {
        return examCost;
    }
    
    public void setExamCost(Double examCost) {
        this.examCost = examCost;
    }
    
    public String getSupportedLanguages() {
        return supportedLanguages;
    }
    
    public void setSupportedLanguages(String supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
    }
    
    public String getLevel() {
        return level;
    }
    
    public void setLevel(String level) {
        this.level = level;
    }
    
    public long getQuestionCount() {
        return questionCount;
    }
    
    public void setQuestionCount(long questionCount) {
        this.questionCount = questionCount;
    }
} 