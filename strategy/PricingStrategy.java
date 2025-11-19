package strategy;

/**
 * 전략 패턴
 */

// 전략 인터페이스
public interface PricingStrategy {
    int calculatePrice(int minutes);
    String getStrategyName(); /*11월 16일 추가사항: 각 요금제의 이름도 같이 출력하로독 변경 */
}

// 구체적인 요금제 전략 클래스
//1. 일반 사용자 + 일반 자전거
class RegularUserRegularBicycleStrategy implements PricingStrategy {
    @Override
    public int calculatePrice(int minutes) {
        return 1000 + minutes * 100; // 분당 100원
    }
    @Override
    public String getStrategyName() { 
        return "일반 요금(일반)";
    }
}

//2. 일반 사용자 + 전기 자전거
class RegularUserElectricBicycleStrategy implements PricingStrategy {
    @Override
    public int calculatePrice(int minutes) {
        return 1500 + minutes * 150; // 분당 150원
    }
    @Override
    public String getStrategyName() { 
        return "일반 요금(전기)";
    }
}

//3. 학생 사용자 + 일반 자전거
class StudentUserRegularBicycleStrategy implements PricingStrategy {
    @Override
    public int calculatePrice(int minutes) {
        int regularPrice = 1000 + minutes * 100;
        return (int)(regularPrice * 0.8); // 20% 할인
    }
    @Override
    public String getStrategyName() { 
        return "학생 요금(일반)";
    }
}
//4. 학생 사용자 + 전기 자전거
class StudentUserElectricBicycleStrategy implements PricingStrategy {
    @Override
    public int calculatePrice(int minutes) {
        int regularPrice = 1500 + minutes * 150;
        return (int)(regularPrice * 0.8); // 20% 할인
    }
    @Override
    public String getStrategyName() {
        return "학생 요금(전기)";
    }
}