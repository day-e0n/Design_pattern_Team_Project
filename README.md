# 디자인패턴 2조

## 📚 포함된 디자인 패턴

### 1️⃣ 전략 패턴 (Strategy Pattern)
- **파일**: `PricingStrategy.java`
- **기능**: 일반/학생 요금 계산 방법을 바꿀 수 있음
- **핵심**: 알고리즘을 런타임에 선택

### 2️⃣ 팩토리 메소드 패턴 (Factory Method Pattern)  
- **파일**: `BicycleFactory.java`
- **기능**: 일반/전기 자전거를 생성
- **핵심**: 객체 생성 방법을 캡슐화

### 3️⃣ 데코레이터 패턴 (Decorator Pattern)
- **파일**: `SimpleDecorator.java` 
- **기능**: 자전거에 GPS, 잠금 기능을 동적으로 추가
- **핵심**: 기존 객체에 새로운 기능을 덧붙임

### 4️⃣ 옵저버 패턴 (Observer Pattern)
- **파일**: `SimpleObserver.java`
- **기능**: 자전거 상태가 변하면 여러 곳에 알림
- **핵심**: 일대다 의존성, 상태 변화 자동 알림


## 🚀 빌드 & 실행 (로컬)

프로젝트 루트(`beginner_only`)에서 Java 소스 전체를 컴파일하고 `out` 폴더로 클래스 파일을 생성합니다.

```bash
# 1) 모든 Java 파일을 찾아 컴파일
find . -name "*.java" > sources.txt
javac -d out @sources.txt

# 2) 실행 (클래스패스에 컴파일 결과를 포함)
java -cp out core.BeginnerBicycleSystem
```

참고: 위 명령은 JDK가 설치되어 있고 `javac`/`java` 명령이 PATH에 등록되어 있어야 합니다.

## 관리자 계정 (안전한 공개 저장소 업로드 목적)

이 저장소는 **관리자 계정 하나**를 애플리케이션 시작 시 자동으로 생성합니다. 목적은 리포지토리에 비밀번호를 직접 저장하지 않고도 관리자 접근을 유지하기 위함입니다.

- 우선순위로 **환경변수**를 읽습니다:
	- `ADMIN_USER` : 관리자 아이디 (옵션, 기본값 `admin`)
	- `ADMIN_PASS` : 관리자 비밀번호 (옵션)
	- `ADMIN_PASS_HASH` : 비밀번호 해시(SHA-256)로 미리 설정할 경우 사용

- 동작 방식:
	- `users.csv` 파일에 `userType`이 `admin`인 항목이 이미 있으면 아무것도 하지 않습니다.
	- 없으면 `ADMIN_USER`/`ADMIN_PASS`(또는 `ADMIN_PASS_HASH`)으로 관리자 계정을 생성합니다.
	- 환경변수가 없으면 안전한 랜덤 비밀번호를 생성하고 콘솔에 한 번 출력합니다(개발용).

※ 주의: 콘솔에 출력된 비밀번호는 개발·테스트 편의를 위한 것이라 Git에 올려도 저장소에서 비밀번호가 고정되어 노출되지는 않습니다. 실제 운영 시에는 `ADMIN_USER`와 `ADMIN_PASS`를 CI/CD나 호스팅 환경의 비밀(Secrets)으로 넣어주세요.

## 추가 참고

- `users.csv` 파일은 애플리케이션 루트에 생성됩니다. CSV 칼럼: `userid,passwordhash,name,phoneNumber,location,userType`.
- 관리자 모드 접근 시 `ConsoleInterface`는 로그인 절차로 관리자 권한을 확인합니다.
- 추가 설정이나 실행 문제가 있으면 알려주시면 바로 도와드리겠습니다.

## 실행 결과 예시

```
==== 자전거 공유 시스템 ====

1️⃣ 전략 패턴 - 요금 계산
일반 요금 (30분): 4000원
학생 요금 (30분): 2000원

2️⃣ 팩토리 패턴 - 자전거 생성  
생성된 자전거: 일반자전거 (ID: B001)
생성된 자전거: 전기자전거 (ID: E001)

3️⃣ 데코레이터 패턴 - 기능 추가
기본: 자전거 B001
GPS 추가: 자전거 B001 + GPS  
잠금 추가: 자전거 B001 + GPS + 스마트잠금

4️⃣ 옵저버 패턴 - 상태 알림
[사용자 김철수] B001 대여됨
[관리자] B001 대여됨
[사용자 김철수] B001 반납됨  
[관리자] B001 반납됨

==== 데모 완료 ====
```

## 📁 파일 구조 (총 5개 파일)

```
beginner_only/
├── BeginnerBicycleSystem.java  # 메인 실행 파일
├── PricingStrategy.java        # 전략 패턴
├── BicycleFactory.java         # 팩토리 메소드 패턴  
├── SimpleDecorator.java        # 데코레이터 패턴
└── SimpleObserver.java         # 옵저버 패턴
```
