package com.certmaster.aws.domain.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AWS 자격증 정보를 나타내는 엔티티 클래스
 */
@Entity
@Table(name = "certifications")
public class Certification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String code; // 자격증 코드
    
    @Column(nullable = false)
    private String name; // 자격증 이름
    
    @Column(length = 2000)
    private String description; // 자격증 설명
    
    private Double examCost; // 시험 비용
    
    private String supportedLanguages; // 지원 언어 (쉼표로 구분)
    
    private String level; // 자격증 레벨 (Associate, Professional 등)
    
    @OneToMany(mappedBy = "certification", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();
    
    // 기본 생성자
    protected Certification() {
    }
    
    // 일반 생성자
    public Certification(String code, String name, String description, Double examCost, 
                         String supportedLanguages, String level) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.examCost = examCost;
        this.supportedLanguages = supportedLanguages;
        this.level = level;
    }
    
    // Getter 및 Setter
    public Long getId() {
        return id;
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
    
    public List<Question> getQuestions() {
        return questions;
    }
    
    // 문제 추가 메서드
    public void addQuestion(Question question) {
        questions.add(question);
        question.setCertification(this);
    }
    
    // 문제 제거 메서드
    public void removeQuestion(Question question) {
        questions.remove(question);
        question.setCertification(null);
    }
    
    @Override
    public String toString() {
        return "Certification{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", level='" + level + '\'' +
                '}';
    }
} 