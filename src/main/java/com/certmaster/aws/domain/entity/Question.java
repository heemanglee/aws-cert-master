package com.certmaster.aws.domain.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AWS 자격증 시험 문제를 나타내는 엔티티 클래스
 */
@Entity
@Table(name = "questions")
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 5000, nullable = false)
    private String content; // 문제 내용
    
    private String explanation; // 문제 해설
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_id")
    private Certification certification; // 연관된 자격증
    
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();
    
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<UserProgress> userProgresses = new ArrayList<>();
    
    // 기본 생성자
    protected Question() {
    }
    
    // 일반 생성자
    public Question(String content, String explanation) {
        this.content = content;
        this.explanation = explanation;
    }
    
    // Getter 및 Setter
    public Long getId() {
        return id;
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
    
    public Certification getCertification() {
        return certification;
    }
    
    public void setCertification(Certification certification) {
        this.certification = certification;
    }
    
    public List<Option> getOptions() {
        return options;
    }
    
    // 보기 추가 메서드
    public void addOption(Option option) {
        options.add(option);
        option.setQuestion(this);
    }
    
    // 보기 제거 메서드
    public void removeOption(Option option) {
        options.remove(option);
        option.setQuestion(null);
    }
    
    public List<UserProgress> getUserProgresses() {
        return userProgresses;
    }
    
    // 문제의 정답 보기를 반환하는 메서드
    public List<Option> getCorrectOptions() {
        List<Option> correctOptions = new ArrayList<>();
        for (Option option : options) {
            if (option.isCorrect()) {
                correctOptions.add(option);
            }
        }
        return correctOptions;
    }
    
    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", content='" + (content != null && content.length() > 50 
                    ? content.substring(0, 47) + "..." 
                    : content) + '\'' +
                '}';
    }
} 