/**
 * 전략 패턴 - 자전거 요금 계산 (간단 버전)
 */

// 전략 인터페이스
interface PricingStrategy {
    int calculatePrice(int minutes);
}

// 일반 요금
class RegularPricing implements PricingStrategy {
    public int calculatePrice(int minutes) {
        return 1000 + minutes * 100; // 기본 1000원 + 분당 100원
    }
}

// 학생 요금 (50% 할인)
class StudentPricing implements PricingStrategy {
    public int calculatePrice(int minutes) {
        return 500 + minutes * 50; // 기본 500원 + 분당 50원
    }
}

// 전략 사용 클래스
class PricingContext {
    private PricingStrategy strategy;
    
    public PricingContext(PricingStrategy strategy) {
        this.strategy = strategy;
    }
    
    public void setStrategy(PricingStrategy strategy) {
        this.strategy = strategy;
    }
    
    public int calculatePrice(int minutes) {
        return strategy.calculatePrice(minutes);
    }
}