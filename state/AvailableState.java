package state;

import observer.BreakdownReason;

// 사용 가능 상태
class AvailableState implements BikeStateInterface {
    @Override
    public void reportBroken(BikeState bike, BreakdownReason reason) {
        System.out.println(">> [State Change] " + bike.getBikeId() + " : '사용 가능' -> '고장'");
        bike.setState(new BrokenState());
    }

    // 사용 가능한 자전거는 수리/수리완료/스테이션이동 불가
    @Override
    public void startRepair(BikeState bike) { /* Do nothing */ }
    @Override
    public void completeRepair(BikeState bike) { /* Do nothing */ }
    @Override
    public void moveToStation(BikeState bike) { /* Do nothing */ }
    @Override
    public String getStatus() { return "사용 가능"; }
}