package strategy;

public class RegularUserRegularBicycleStrategy implements PricingStrategy {
    @Override
    public int calculatePrice(int minutes) {
        return 1000 + minutes * 100; // 분당 100원
    }
    @Override
    public String getStrategyName() { 
        return "일반 요금(일반)";
    }
}