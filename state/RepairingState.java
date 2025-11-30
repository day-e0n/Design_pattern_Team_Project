package state;

import core.BicycleStatus;
import java.util.List;
import observer.BreakdownReason;

// 수리 중인 상태
public class RepairingState extends AbstractUnavailableState {
    @Override
    public void reportBroken(BikeState bike, List<BreakdownReason> reasons) {
        System.out.println("현재 수리 중인 자전거입니다.");
    }

    // can 메서드 오류 메시지는 부모 클래스(AbstractUnavailableState)에서 처리

    @Override
    public String getStatus() { return "수리 중"; }

    @Override
    public BicycleStatus getBicycleStatus() {
        return BicycleStatus.MAINTENANCE;
    }
}