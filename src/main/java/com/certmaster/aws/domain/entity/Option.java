package com.certmaster.aws.domain.entity;

import jakarta.persistence.*;

/**
 * 문제의 선택지(보기)를 나타내는 엔티티 클래스
 */
@Entity
@Table(name = "options")
public class Option {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 2000, nullable = false)
    private String content; // 보기 내용
    
    private boolean correct; // 정답 여부
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question; // 연관된 문제
    
    // 기본 생성자
    protected Option() {
    }
    
    // 일반 생성자
    public Option(String content, boolean correct) {
        this.content = content;
        this.correct = correct;
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
    
    public boolean isCorrect() {
        return correct;
    }
    
    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
    
    public Question getQuestion() {
        return question;
    }
    
    public void setQuestion(Question question) {
        this.question = question;
    }
    
    @Override
    public String toString() {
        return "Option{" +
                "id=" + id +
                ", content='" + (content != null && content.length() > 30 
                    ? content.substring(0, 27) + "..." 
                    : content) + '\'' +
                ", correct=" + correct +
                '}';
    }
} 