# 백엔드 개발자 과제 - 테스트 기반 구현

## 과제 개요
주어진 테스트 코드를 통과하도록 주문 시스템을 구현해주세요.
Entity, Enum, Repository, Service 껍데기는 제공되며, 비즈니스 로직과 API를 구현하시면 됩니다.

> **안내**: 과제 진행 시 이 저장소를 Fork하지 말고, 상단의 **Use this template** 버튼을 사용해주세요.
> Star/Watch도 지원자 간 노출이 될 수 있으니 삼가해주세요.

---

## 테스트 케이스
1~5번 필수 테스트를 모두 통과하도록 구현해주세요.
6번 동시성 테스트는 선택사항이며, 주석을 해제하고 도전해보세요.

| # | 테스트 | 설명 | 필수 |
|---|--------|------|------|
| 1 | 일반회원_쿠폰없음_정가결제 | 기본 주문 흐름 | O |
| 2 | VIP회원_10퍼센트_할인적용 | 회원 등급별 할인 | O |
| 3 | 최소주문금액_미달시_예외발생 | 주문 검증 로직 | O |
| 4 | 쿠폰_중복적용_불가_오류검증 | 쿠폰 규칙 처리 | O |
| 5 | 주문금액_3만원_미만이면_배송비_3000원_추가 | 배송비 정책 | O |
| 6 | 동시에_100명이_요청해도_재고는_정확히_줄어야한다 | 동시성 제어 | 선택 |

---

## REST API 구현
`controller` 패키지에 관련 API를 자유롭게 설계하여 구현해주세요.
- API 종류, 엔드포인트, 요청/응답 형식 모두 자유
- API 테스트 코드 작성 여부도 자유
- Swagger UI(`http://localhost:8080/`)에서 확인 가능

---

## 제공 항목
- 프로젝트 셋업 (build.gradle, application.yml)
- 테스트 코드 (`OrderServiceTest.java`)
- Entity 클래스 (Member, Product, Order, Coupon)
- Enum (MemberGrade, OrderStatus)
- Repository 인터페이스
- Service 껍데기 (`OrderService.java`)

---

## 기술 스택 및 사용 라이브러리
- Java 17+
- Spring Boot 3.2
- Spring Data JPA
- H2 Database (in-memory)
- Gradle
- JUnit 5
- Lombok
- Swagger (springdoc-openapi)

---

## 제출 방법
1. **Use this template** 버튼으로 본인 계정에 리포지토리 생성 (**Private**)
2. 기능 단위로 커밋 작성
3. 완료 후 GitHub 계정 초대 및 이메일 제출
   - **초대할 계정**: [youyoung-git](https://github.com/youyoung-git)
   - **제출처**: `rg115@youyoung.net`
   - **메일 제목**: `[백엔드 과제 제출] 이름`
   - **본문**:
     - GitHub Repository URL
     - 지원자 이름 / 연락처
     - 간단한 한마디 (선택)

---

## 제출물
`SUBMISSION.md` 파일에 아래 내용을 작성해주세요. (템플릿 제공)
1. REST API 목록
2. 테스트 통과를 위해 고려한 부분 (테스트별)
3. 기본 요구사항 외 추가 구현한 기능이나 기술적 시도
4. 개선 및 고민 사항
5. 소감 및 피드백 (과제에 대한 의견, 문의사항 등)

---

## 제출 기한
- 수령일시로부터 5일 이내
- 질문 사항은 rg115@youyoung.net 으로 문의

## 과도한 설계 지양

이 과제는 **핵심 비즈니스 로직 구현 능력**을 평가합니다.

### 권장
- H2 In-Memory DB 사용
- Spring Boot 기본 설정
- 단순하고 명확한 코드

### 지양
- 외부 인프라 추가 (Redis, Kafka, Docker 등)
- 과도한 추상화 (멀티 모듈, 헥사고날 등)
- 테스트와 무관한 기능 (알림, 로깅, 모니터링 등)

> 만약 특정 기술을 사용해야 한다고 판단하면, README에 **"왜 필요한지"** 작성해주세요.
> 예: "동시성 제어를 위해 Redis Redisson 사용" (단, 필수는 아닙니다)

---

## 자주 묻는 질문
Q. JPA Entity 수정해도 되나요?
A. 네, 필요하다면 수정 가능합니다.

Q. 외부 라이브러리 추가 가능한가요?
A. build.gradle에 명시된 것 외 추가 가능하지만, 꼭 필요한 경우만 사용해주세요.