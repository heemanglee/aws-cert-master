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
    
    @Column(nullable = false)
    private int attemptCount; // 시도 횟수
    
    @Column(nullable = false)
    private int correctCount; // 정답 횟수
    
    private boolean flaggedForReview; // 오답 노트에 추가 여부
    
    private String reviewNote; // 오답 노트에 추가할 메모
    
    @Column(name = "first_attempt_time")
    private LocalDateTime firstAttemptTime; // 첫 시도 시간
    
    @Column(name = "last_attempt_time")
    private LocalDateTime lastAttemptTime; // 마지막 시도 시간
    
    @Column(name = "last_correct_time")
    private LocalDateTime lastCorrectTime; // 마지막 정답 시간
    
    @Column(name = "last_review_time")
    private LocalDateTime lastReviewTime; // 마지막 오답 노트 확인 시간
    
    // 기본 생성자
    protected UserProgress() {
    }
    
    // 일반 생성자
    public UserProgress(String userId, Question question) {
        this.userId = userId;
        this.question = question;
        this.attemptCount = 0;
        this.correctCount = 0;
        this.flaggedForReview = false;
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
    
    public int getAttemptCount() {
        return attemptCount;
    }
    
    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }
    
    public int getCorrectCount() {
        return correctCount;
    }
    
    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }
    
    public boolean isCorrect() {
        return correctCount > 0;
    }
    
    public double getCorrectRate() {
        return attemptCount > 0 ? (double) correctCount / attemptCount : 0;
    }
    
    public boolean isFlaggedForReview() {
        return flaggedForReview;
    }
    
    public void setFlaggedForReview(boolean flaggedForReview) {
        this.flaggedForReview = flaggedForReview;
        if (flaggedForReview) {
            this.lastReviewTime = LocalDateTime.now();
        }
    }
    
    public String getReviewNote() {
        return reviewNote;
    }
    
    public void setReviewNote(String reviewNote) {
        this.reviewNote = reviewNote;
    }
    
    public LocalDateTime getFirstAttemptTime() {
        return firstAttemptTime;
    }
    
    public void setFirstAttemptTime(LocalDateTime firstAttemptTime) {
        this.firstAttemptTime = firstAttemptTime;
    }
    
    public LocalDateTime getLastAttemptTime() {
        return lastAttemptTime;
    }
    
    public void setLastAttemptTime(LocalDateTime lastAttemptTime) {
        this.lastAttemptTime = lastAttemptTime;
    }
    
    public LocalDateTime getLastCorrectTime() {
        return lastCorrectTime;
    }
    
    public void setLastCorrectTime(LocalDateTime lastCorrectTime) {
        this.lastCorrectTime = lastCorrectTime;
    }
    
    public LocalDateTime getLastReviewTime() {
        return lastReviewTime;
    }
    
    public void setLastReviewTime(LocalDateTime lastReviewTime) {
        this.lastReviewTime = lastReviewTime;
    }
    
    /**
     * 문제 풀이 시도를 기록합니다.
     * 
     * @param isCorrect 정답 여부
     */
    public void recordAttempt(boolean isCorrect) {
        this.attemptCount++;
        
        if (isCorrect) {
            this.correctCount++;
            this.lastCorrectTime = LocalDateTime.now();
        }
        
        if (this.firstAttemptTime == null) {
            this.firstAttemptTime = LocalDateTime.now();
        }
        
        this.lastAttemptTime = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "UserProgress{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", attemptCount=" + attemptCount +
                ", correctCount=" + correctCount +
                ", flaggedForReview=" + flaggedForReview +
                '}';
    }
} 