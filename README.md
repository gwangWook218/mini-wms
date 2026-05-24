# 📦 Mini-WMS (경량형 창고 관리 시스템)

스프링 부트와 JPA를 활용하여 개발하는 경량형 창고 관리 시스템(WMS, Warehouse Management System) 백엔드 애플리케이션입니다.  
실무적 관점에서 **물류 데이터의 무결성**과 **시스템의 안정성**을 최우선으로 고려하여 아키텍처를 설계하고 있습니다.

---

## 🛠️ Tech Stack

- **Backend:** Java 17, Spring Boot 4.0.6, Spring Data JPA
- **Database:** MySQL 8.0
- **Build Tool:** Gradle
- **Tools:** IntelliJ IDEA, Postman, Git

---

## 📌 주요 기능 및 개발 현황

- [x] **프로젝트 초기 아키텍처 세팅**
  - Spring Boot 3.x 환경 구축 및 MySQL 연동 완료
  - 외부 커넥션 최외곽 최적화를 위해 OSIV(`spring.jpa.open-in-view`) 옵션 비활성화(`false`) 처리
- [x] **상품(Product) 마스터 데이터 등록 API 개발**
  - 빌더 패턴을 적용하여 데이터 오염을 방지하고 불변 객체 설계 기법 적용
  - `Validation` 라이브러리를 활용해 백엔드 진입 단계에서 데이터 검증(가격 및 재고 수량의 음수 진입 차단)
- [x] **상품 상세 및 전체 목록 조회 API 개발**
  - 엔티티 직접 노출을 차단하기 위해 응답 전용 DTO(`ProductResponse`) 분리
  - `@Transactional(readOnly = true)` 설정을 통한 DB 스냅샷 최적화 및 조회 성능 향상
- [ ] **입고(Inbound) 재고 연산 로직 구현 예정**
- [ ] **출고(Outbound) 검증 및 재고 차감 로직 구현 예정**

---

## 💡 Architecture & Design Focus (포트폴리오 어필 포인트)

1. **안정성을 위한 캡슐화와 빌더 패턴 설계**
   - 엔티티 클래스 전체에 `@Builder`를 남용하지 않고, `id` 필드를 제외한 핵심 생성자에만 조준하여 `@Builder`를 적용했습니다. 이를 통해 시스템 내부에서 자동으로 생성되어야 하는 고유 ID 데이터가 외부 요청에 의해 임의로 변환되는 실수를 원천 차단했습니다.

2. **철저한 데이터 무결성 검증**
   - HTTP 요청을 받는 Controller 레이어에서 `@Valid` 어노테이션과 DTO 내 검증 규칙(`@NotBlank`, `@Min`)을 연동했습니다. 잘못된 형식의 데이터가 유입될 경우 비즈니스 로직(Service) 단계로 넘어가기 전에 예외를 차단하여 서버 자원의 낭비를 막았습니다.

3. **엔티티와 DTO의 엄격한 분리**
   - 데이터베이스 테이블과 직접 매핑되는 엔티티 객체가 외부 API 스펙 변화에 흔들리지 않도록 요청(`ProductCreateRequest`)과 응답(`ProductResponse`) 객체를 완전히 분리하여 유지보수성을 극대화했습니다.
