package strategy;

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