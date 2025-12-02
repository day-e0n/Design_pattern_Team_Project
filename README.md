# 디자인패턴 2조

## 🚀 실행 방법

```bash
# 1. 컴파일
javac *.java

# 2. 실행  
# 디자인패턴 팀 프로젝트 — Beginner Bicycle System

이 저장소는 학습 목적으로 만든 자전거 공유 시스템 예제입니다. 여러 디자인 패턴(Factory, Strategy, Decorator, Observer, State 등)을 작은 콘솔 기반 애플리케이션에서 통합하여 보여줍니다.

요약:
- 언어: Java
- 실행: 콘솔 기반 (메인 클래스: `core.BeginnerBicycleSystem`)
- 저장: 간단한 CSV(`users.csv`)를 사용자 저장소로 사용

## 포함된 디자인 패턴 (주요)
- Factory: `core/BicycleFactory.java` — 일반/전기 자전거 생성
- Strategy: `strategy/*` — 요금(정책) 및 수리 시간 계산
- Decorator: `decorator/*` — GPS, 스마트잠금 등 자전거 기능 확장
- Observer: `observer/*` + `core/LocationManager.java` — 고장 신고/수리 알림과 위치 알림
- State: `state/*` — 자전거의 상태(대여/수리/고장 등)를 캡슐화

## 프로젝트 구조 (간략)

beginner_only/
- bin/ (예비 스크립트/도움용 폴더)
- core/ (도메인 모델, 콘솔 UI, 매니저들)
- decorator/ (데코레이터 패턴 구현)
- observer/ (옵저버 패턴 구현)
- state/ (State 패턴 구현)
- strategy/ (요금·수리 전략)
- README.md

더 자세한 파일 목록은 저장소의 폴더를 확인하세요. 주요 파일은 `core/` 하위에 있고, 패턴별 구현은 각 패키지에 정리되어 있습니다.

## 빌드 & 실행 (로컬)

JDK가 설치되어 있고 `javac`/`java`가 PATH에 등록되어 있다고 가정합니다.

```bash
# 프로젝트 루트에서 (beginner_only)
find . -name "*.java" > sources.txt
javac -d out @sources.txt

# 실행
java -cp out core.BeginnerBicycleSystem
```

데모 모드(`BeginnerBicycleSystem.runDemoMode`)도 포함되어 있어 패턴 동작을 빠르게 확인할 수 있습니다.

## 관리자 계정 안내 (안전하게 Git에 올리기)

프로젝트는 관리자 계정 하나를 자동으로 보장합니다. 목적은 리포지토리에 평문 비밀번호를 두지 않고도 관리자 접근을 유지하기 위함입니다.

- 우선순위로 환경변수 사용:
	- `ADMIN_USER` : 관리자 아이디 (옵션, 기본 `admin`)
	- `ADMIN_PASS` : 관리자 비밀번호 (옵션)
	- `ADMIN_PASS_HASH` : 미리 계산한 SHA-256 해시(옵션)
- 동작:
	- `users.csv`에 이미 `userType`이 `admin`인 항목이 있으면 생성 안 함.
	- 없으면 환경변수에서 관리자 정보를 읽어 생성.
	- 환경변수도 없으면 랜덤 비밀번호를 생성해 콘솔에 한 번 출력합니다(개발·테스트용).

운영 환경에서는 `ADMIN_USER`/`ADMIN_PASS`를 CI/CD/호스팅 비밀(Secrets)으로 설정하세요.

## 콘솔 사용 개요

- 메인 메뉴에서 `관리자 모드` 또는 `사용자 모드` 선택
- 관리자 모드는 로그인(관리자 계정) 필요 — 자전거 추가/삭제/상태 변경/고장 신고 접수 등
- 사용자 모드는 회원가입/로그인 후 대여·반납·요금 계산 기능 사용

## 테스트 / 개발 팁

- `users.csv`를 지우고 실행하면(또는 환경변수 없이 실행) 초기 관리자 계정이 생성되어 콘솔에 비밀번호가 출력됩니다.
- State/Observer/Strategy 흐름은 콘솔 UI로 수동 테스트 가능합니다. 데모 모드(`runDemoMode`)도 활용하세요.

## 기여 및 확장 제안

- 요금 계산에 `BigDecimal` 도입(통화 정밀도 향상)
- 데이터 영속화를 파일→간단한 DB(예: SQLite)로 전환
- 웹/REST 인터페이스 추가(간단한 서버로 확장)

문제가 있거나 README 개선점이 있으면 알려주세요 — 원하는 형식으로 더 다듬어 드리겠습니다.
