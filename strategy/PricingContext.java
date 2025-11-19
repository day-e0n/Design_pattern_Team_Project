package strategy;

/**
 * 전략 패턴 - 전략 사용 클래스 (Context)
 */
public class PricingContext {
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
    
    public String getStrategyName() {
        return strategy.getStrategyName();
    }
}
