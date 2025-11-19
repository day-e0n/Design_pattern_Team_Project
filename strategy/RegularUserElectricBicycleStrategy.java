package strategy;

public class RegularUserElectricBicycleStrategy implements PricingStrategy {
    @Override
    public int calculatePrice(int minutes) {
        return 1500 + minutes * 150; // 분당 150원
    }
    @Override
    public String getStrategyName() { 
        return "일반 요금(전기)";
    }
}