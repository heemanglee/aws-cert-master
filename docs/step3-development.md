# Step 3: 문제 풀이 및 채점 기능 개발

## 구현 내용

### 1. 문제 풀이 관련 컨트롤러 개발
- `QuestionController` 구현: 문제 목록 표시, 개별 문제 풀이, 채점 기능 처리
- 랜덤 문제 풀이 모드 추가: 자격증별로 무작위 문제를 선택하여 제공

### 2. 사용자 진행 상황 관리 기능 개발
- `UserProgressService` 인터페이스 및 구현체 작성
- 문제 풀이 결과 저장 및 통계 기능 추가
- 진행 상황 조회 기능 구현 (시도한 문제, 정답 문제 등)

### 3. 사용자 인터페이스 구현
- 문제 목록 페이지 구현: `/questions?certificationId={id}`
- 문제 풀이 페이지 구현: `/questions/{id}`
- 결과 페이지 구현: `/questions/{id}/submit`
- 랜덤 문제 풀이 페이지 구현: `/questions/random?certificationId={id}`

### 4. 채점 및 피드백 기능
- 문제 풀이 결과 채점 기능 구현
- 정답/오답 시각적 표시 기능 구현
- 해설 표시 기능 추가

## 기술적 구현 상세

### 문제 풀이 프로세스
1. 사용자가 특정 자격증의 문제 목록 또는 랜덤 문제 세트를 선택
2. 개별 문제 페이지에서 답안 체크 후 제출
3. 채점 알고리즘이 사용자 선택과 정답을 비교하여 결과 판정
4. 결과 페이지에서 정답 여부, 정확한 답안, 해설 표시
5. 사용자 진행 상황 DB에 저장

### 클린 아키텍처 적용
- Domain Layer: `UserProgressService` 인터페이스로 비즈니스 규칙 정의
- Application Layer: `UserProgressServiceImpl`로 비즈니스 로직 구현
- Presentation Layer: `QuestionController`로 사용자 요청 처리
- Infrastructure Layer: `UserProgressRepository`로 데이터 영속성 처리

### 데이터 모델
- `UserProgress` 엔티티를 활용하여 사용자별 문제 풀이 진행 상황 추적
- 시도 횟수, 정답 횟수, 첫 시도 시간, 마지막 시도 시간, 마지막 정답 시간 등 기록

## 개선 사항 및 향후 계획
- 사용자 인증 시스템 연동 (현재는 임시 사용자 ID 사용)
- 오답 노트 기능 추가
- 문제 북마크 기능 구현
- 시험 모드 구현 (실제 시험 환경과 유사한 시간 제한 등)
- 학습 분석 대시보드 추가 