package strategy;

import java.util.Random;
import observer.BreakdownReason;

public class RepairStrategy implements RepairStrategyInterface {
    private final Random random = new Random();

    @Override
    public int calculateRepairTime(BreakdownReason reason) {
        int baseTime = reason.getBaseTime();

        // 특정 확률로 지연 혹은 조기 수리
        double chance = random.nextDouble(); // 0.0 ~ 1.0
        
        if (chance < 0.1) { // 10% 확률로 50% 조기 수리
            return (int) (baseTime * 0.5);
        } else if (chance < 0.3) { // 20% 확률(0.1~0.3)로 50% 지연
            return (int) (baseTime * 1.5);
        }
        
        return baseTime; // 70% 확률로 기본 시간
    }
}