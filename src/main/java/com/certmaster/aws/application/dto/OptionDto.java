package com.certmaster.aws.application.dto;

import com.certmaster.aws.domain.entity.Option;

/**
 * 문제 보기 정보를 담는 DTO 클래스
 */
public class OptionDto {
    private Long id;
    private String content;
    private boolean correct;
    
    // 기본 생성자
    public OptionDto() {
    }
    
    // 엔티티에서 DTO 생성
    public static OptionDto fromEntity(Option option) {
        OptionDto dto = new OptionDto();
        dto.setId(option.getId());
        dto.setContent(option.getContent());
        dto.setCorrect(option.isCorrect());
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
    
    public boolean isCorrect() {
        return correct;
    }
    
    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
} 