package state;

import core.BicycleStatus;
import java.util.List;
import observer.BreakdownReason;

// 고장 상태
public class BrokenState extends AbstractUnavailableState {
    @Override
    public void reportBroken(BikeState bike, List<BreakdownReason> reasons) {
        System.out.println("이미 고장난 상태입니다.");
    }

    // can 메서드 오류 메시지는 부모 클래스(AbstractUnavailableState)에서 처리

    @Override
    public String getStatus() { return "고장"; }

    @Override
    public BicycleStatus getBicycleStatus() {
        return BicycleStatus.BROKEN;
    }
}