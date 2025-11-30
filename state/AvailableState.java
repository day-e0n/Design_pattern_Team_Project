package state;

import core.BicycleStatus;
import java.util.List;
import observer.BreakdownReason;

// 사용 가능 상태
public class AvailableState implements BikeStateInterface {
    @Override
    public void reportBroken(BikeState bike, List<BreakdownReason> reasons) {
        System.out.println(">> [State Change] " + bike.getBikeId() + " : '사용 가능' -> '고장'");
        bike.setState(new BrokenState());
    }

    @Override
    public boolean canRent() { return true; }
    @Override
    public boolean canDelete() { return true; }
    @Override
    public boolean canMove() { return true; }
    @Override
    public boolean canReport() { return true; }

    @Override
    public String getStatus() { return "사용 가능"; }

    @Override
    public BicycleStatus getBicycleStatus() {
        return BicycleStatus.AVAILABLE;
    }
}