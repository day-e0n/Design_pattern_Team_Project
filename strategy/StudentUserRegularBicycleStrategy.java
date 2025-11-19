package strategy;

public class StudentUserRegularBicycleStrategy implements PricingStrategy {
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