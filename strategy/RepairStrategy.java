package strategy;

import java.util.List;
import java.util.Random;
import observer.BreakdownReason;

public class RepairStrategy implements RepairStrategyInterface {
    private final Random random = new Random();

    @Override
    public int calculateRepairTime(boolean isElectric, List<BreakdownReason> reasons) {
        int totalBaseTime = 0;

        for (BreakdownReason reason : reasons) {
            int time = reason.getBaseTime();
            // 배터리 문제는 전기자전거만 가능하며, 배터리 제외하고 전기자전거는 2배 시간
            if (isElectric && reason != BreakdownReason.BATTERY) {
                time *= 2;
            }
            totalBaseTime += time;
        }

        // 확률적 조기/지연 수리 적용
        double chance = random.nextDouble(); // 0.0 ~ 1.0
        
        if (chance < 0.1) { // 10% 확률로 조기 수리 (20% 시간 감소)
            return (int) (totalBaseTime * 0.8);
        } else if (chance < 0.3) { // 20% 확률(0.1~0.3)로 지연 수리 (20% 시간 증가)
            return (int) (totalBaseTime * 1.2);
        }
        
        return totalBaseTime; // 나머지 70%는 기본 시간
    }
}