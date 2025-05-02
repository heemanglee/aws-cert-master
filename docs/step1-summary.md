# AWS 자격증 덤프 문제 애플리케이션 - Step 1 개발 요약

## 구현 내용

AWS 자격증 덤프 문제 애플리케이션의 기본 프로젝트를 설정하고 필요한 엔티티 설계 및 구현을 완료했습니다.

### 기술 스택

- **백엔드**: Spring Boot 3.4.5 (Gradle), Java 17
- **프론트엔드**: Thymeleaf (아직 구현 안 됨)
- **데이터베이스**: H2 (개발), MySQL (운영)

### 아키텍처

Clean Architecture 원칙에 따라 다음과 같은 패키지 구조로 구현하였습니다:

- **domain/entity**: 핵심 도메인 엔티티
- **domain/repository**: 데이터 액세스 인터페이스
- **domain/service**: 도메인 서비스 인터페이스
- **application/service**: 도메인 서비스 구현체
- **application/dto**: 데이터 전송 객체 (추후 구현)
- **infrastructure/config**: 애플리케이션 설정
- **infrastructure/datasource**: 데이터 소스 설정 (추후 구현)
- **presentation/controller**: 웹 컨트롤러 (추후 구현)

### 구현된 기능

1. **도메인 모델 설계**:
   - Certification (자격증)
   - Question (문제)
   - Option (보기)
   - UserProgress (사용자 진행 상황)

2. **리포지토리 인터페이스 구현**:
   - CertificationRepository
   - QuestionRepository
   - UserProgressRepository

3. **자격증 서비스 구현**:
   - 모든 자격증 조회
   - ID, 코드, 레벨로 자격증 조회
   - 외부 API(현재는 하드코딩)에서 AWS 자격증 정보 가져오기

4. **초기 데이터 로딩**:
   - 애플리케이션 시작 시 AWS 자격증 정보 자동 로드

5. **단위 테스트**:
   - 자격증 서비스 테스트 케이스 구현

## 다음 단계 계획

- Step 2에서 메인 페이지 및 자격증 목록 기능을 구현할 예정입니다.
- Thymeleaf 템플릿을 이용한 UI 구현
- 컨트롤러 및 DTO 계층 추가
- 덤프 파일 파싱 로직 구현 