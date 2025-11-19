package strategy;

public class StudentUserElectricBicycleStrategy implements PricingStrategy {
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