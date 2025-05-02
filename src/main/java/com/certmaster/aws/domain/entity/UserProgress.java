package com.certmaster.aws.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 사용자의 문제 풀이 진행 상황을 나타내는 엔티티 클래스
 */
@Entity
@Table(name = "user_progress")
public class UserProgress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String userId; // 사용자 ID
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question; // 연관된 문제
    
    private boolean correct; // 정답 여부
    
    private boolean flaggedForReview; // 오답 노트에 추가 여부
    
    @Column(name = "attempted_at")
    private LocalDateTime attemptedAt; // 시도 일시
    
    @Column(name = "last_reviewed_at")
    private LocalDateTime lastReviewedAt; // 마지막 리뷰 일시
    
    // 기본 생성자
    protected UserProgress() {
    }
    
    // 일반 생성자
    public UserProgress(String userId, Question question, boolean correct) {
        this.userId = userId;
        this.question = question;
        this.correct = correct;
        this.attemptedAt = LocalDateTime.now();
    }
    
    // Getter 및 Setter
    public Long getId() {
        return id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public Question getQuestion() {
        return question;
    }
    
    public void setQuestion(Question question) {
        this.question = question;
    }
    
    public boolean isCorrect() {
        return correct;
    }
    
    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
    
    public boolean isFlaggedForReview() {
        return flaggedForReview;
    }
    
    public void setFlaggedForReview(boolean flaggedForReview) {
        this.flaggedForReview = flaggedForReview;
        if (flaggedForReview) {
            this.lastReviewedAt = LocalDateTime.now();
        }
    }
    
    public LocalDateTime getAttemptedAt() {
        return attemptedAt;
    }
    
    public void setAttemptedAt(LocalDateTime attemptedAt) {
        this.attemptedAt = attemptedAt;
    }
    
    public LocalDateTime getLastReviewedAt() {
        return lastReviewedAt;
    }
    
    public void setLastReviewedAt(LocalDateTime lastReviewedAt) {
        this.lastReviewedAt = lastReviewedAt;
    }
    
    @Override
    public String toString() {
        return "UserProgress{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", correct=" + correct +
                ", flaggedForReview=" + flaggedForReview +
                ", attemptedAt=" + attemptedAt +
                '}';
    }
} 