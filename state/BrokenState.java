package state;

import observer.BreakdownReason;

// 고장 상태
class BrokenState implements BikeStateInterface {
    @Override
    public void startRepair(BikeState bike) {
        System.out.println(">> [State Change] " + bike.getBikeId() + " : '고장' -> '수리 중'");
        bike.setState(new RepairingState());
    }

    // 고장난 자전거는 고장신고/수리완료/스테이션이동 불가
    @Override
    public void reportBroken(BikeState bike, BreakdownReason reason) { /* Already broken */ }
    @Override
    public void completeRepair(BikeState bike) { /* Do nothing */ }
    @Override
    public void moveToStation(BikeState bike) { /* Do nothing */ }
    @Override
    public String getStatus() { return "고장"; }
}