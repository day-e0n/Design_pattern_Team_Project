package state;

import core.BicycleStatus;
import observer.BreakdownReason;
import java.util.List;

// 대여 중 상태
public class RentedState extends AbstractUnavailableState {
    @Override
    public void reportBroken(BikeState bike, List<BreakdownReason> reasons) {
        // canReport()가 false이므로 Console에서 이 메서드를 호출하지 않도록 제어하지만,
        // 혹시 모를 호출에 대비해 메시지 출력
        System.out.println("대여 중인 자전거는 신고할 수 없습니다.");
    }

    @Override
    public String getStatus() { return "대여 중"; }

    @Override
    public BicycleStatus getBicycleStatus() {
        return BicycleStatus.RENTED;
    }
}