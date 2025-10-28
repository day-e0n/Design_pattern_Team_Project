/**
 * 전략 패턴
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