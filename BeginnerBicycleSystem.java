
/**
 * 자전거 공유 시스템 메인 클래스
 * 콘솔 기반 인터페이스로 동작하는 완전한 자전거 관리 시스템
 * 
 * 포함된 디자인 패턴:
 * - 팩토리 메소드 패턴: 자전거 객체 생성
 * - 전략 패턴: 요금 계산 전략
 * - 데코레이터 패턴: 자전거 기능 확장
 * - 옵저버 패턴: 상태 변경 알림
 */
public class BeginnerBicycleSystem {
    
    public static void main(String[] args) {
        // 콘솔 인터페이스 시작
        ConsoleInterface console = new ConsoleInterface();
        console.start();
    }
    
    /**
     * 데모 모드 - 디자인 패턴들의 동작을 보여주는 메소드
     * 개발/테스트 목적으로 사용
     */
    public static void runDemoMode() {
        System.out.println("==== 디자인 패턴 데모 모드 ====\n");
        
        // 1. 팩토리 패턴 데모
        System.out.println("1. 팩토리 메소드 패턴 - 자전거 생성");
        BicycleFactory regularFactory = new RegularBicycleFactory();
        BicycleFactory electricFactory = new ElectricBicycleFactory();
        
        Bicycle bike1 = regularFactory.createBicycle("DEMO001");
        Bicycle bike2 = electricFactory.createBicycle("DEMO002");
        
        System.out.println("생성된 자전거: " + bike1);
        System.out.println("생성된 자전거: " + bike2);
        System.out.println();
        
        // 2. 전략 패턴 데모
        System.out.println("2. 전략 패턴 - 요금 계산");
        PricingContext pricing = new PricingContext(new RegularUserRegularBicycleStrategy());
        System.out.println("일반 회원 일반 자전거 요금 (60분): " + pricing.calculatePrice(60) + "원");
        
        pricing.setStrategy(new StudentUserRegularBicycleStrategy());
        System.out.println("학생 일반 자전거 요금 (60분): " + pricing.calculatePrice(60) + "원");
        System.out.println();

        pricing.setStrategy(new RegularUserElectricBicycleStrategy());
        System.out.println("일반 회원 전기 자전거 요금 (60분): " + pricing.calculatePrice(60) + "원");

        pricing.setStrategy(new StudentUserElectricBicycleStrategy());
        System.out.println("학생 전기 자전거 요금 (60분): " + pricing.calculatePrice(60) + "원");
        System.out.println();

        
        // 3. 데코레이터 패턴 데모
        System.out.println("3. 데코레이터 패턴 - 기능 추가");
        
        SimpleBicycle basic = new SimpleBicycle("DEMO001");
        System.out.println("기본: " + basic.getInfo());

        SimpleBicycle withGPS = new GPSDecorator(basic);
        System.out.println("GPS 추가: " + withGPS.getInfo());

        SmartLockDecorator withGPSAndLock = new SmartLockDecorator(withGPS);
        System.out.println("GPS + 스마트잠금 추가: " + withGPSAndLock.getInfo());
        System.out.println();

        // 스마트 잠금 사용 흐름
        withGPSAndLock.unlock(); // 코드 필요 안내
        String code = withGPSAndLock.getCurrentCodeForTest(); // 테스트 편의용
        System.out.println("테스트용 현재 코드: " + code);

        boolean ok = withGPSAndLock.unlockWithCode("0000");
        System.out.println("해제 결과(오류 코드): " + ok);

        ok = withGPSAndLock.unlockWithCode(code);
        System.out.println("해제 결과(정상 코드): " + ok);

        System.out.println("상태: " + withGPSAndLock.getInfo());
        withGPSAndLock.lock(); // 재잠금 → 새 코드 생성
        System.out.println("재잠금 후: " + withGPSAndLock.getInfo());


        // 4. 옵저버 패턴 데모
        System.out.println("4. 옵저버 패턴 - 상태 알림");
        BicycleStatus status = new BicycleStatus("DEMO001");
        
        status.addObserver(new UserObserver("김철수"));
        status.addObserver(new AdminObserver());
        
        status.rent();
        status.returnBike();
        
        System.out.println("\n==== 데모 완료 ====");
    }
}