package strategy;

/**
 * 전략 패턴 - 인터페이스
 * (구체적인 전략 클래스들은 별도 파일로 분리되었습니다)
 */
public interface PricingStrategy {
    int calculatePrice(int minutes);
    String getStrategyName();
}