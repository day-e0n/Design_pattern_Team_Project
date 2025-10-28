
public class BeginnerBicycleSystem {
    
    public static void main(String[] args) {
        System.out.println("==== 자전거 공유 시스템 ====\n");
        
        // 1. 전략 패턴 데모
        System.out.println("1️전략 패턴 - 요금 계산");
        PricingContext pricing = new PricingContext(new RegularPricing());
        System.out.println("일반 요금 (30분): " + pricing.calculatePrice(30) + "원");
        
        pricing.setStrategy(new StudentPricing());
        System.out.println("학생 요금 (30분): " + pricing.calculatePrice(30) + "원");
        System.out.println();
        
        // 2. 팩토리 패턴 데모
        System.out.println("팩토리 패턴 - 자전거 생성");
        BicycleFactory regularFactory = new RegularBicycleFactory();
        BicycleFactory electricFactory = new ElectricBicycleFactory();
        
        Bicycle bike1 = regularFactory.createBicycle("B001");
        Bicycle bike2 = electricFactory.createBicycle("E001");
        
        System.out.println("생성된 자전거: " + bike1);
        System.out.println("생성된 자전거: " + bike2);
        System.out.println();
        
        // 3. 데코레이터 패턴 데모
        System.out.println("데코레이터 패턴 - 기능 추가");
        SimpleBicycle basic = new SimpleBicycle("B001");
        System.out.println("기본: " + basic.getInfo());
        
        SimpleBicycle withLock = new LockDecorator(withGPS);
        System.out.println("잠금 추가: " + withLock.getInfo());
        System.out.println();
        
        // 4. 옵저버 패턴 데모
        System.out.println("옵저버 패턴 - 상태 알림");
        BicycleStatus status = new BicycleStatus("B001");
        
        status.addObserver(new UserObserver("김철수"));
        status.addObserver(new AdminObserver());
        
        status.rent();
        status.returnBike();
        
        System.out.println("\n==== 완료 ====");
    }
}