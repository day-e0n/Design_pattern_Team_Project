package strategy;

import java.util.List;
import observer.BreakdownReason;

public interface RepairStrategyInterface {
    /**
     * 수리 시간을 계산합니다.
     * @param isElectric 전기 자전거 여부
     * @param reasons 고장 사유 리스트
     * @return 계산된 수리 시간 (초)
     */
    int calculateRepairTime(boolean isElectric, List<BreakdownReason> reasons);
}