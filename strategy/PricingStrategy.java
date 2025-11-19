package strategy;

public interface PricingStrategy {
    int calculatePrice(int minutes);
    String getStrategyName();
}