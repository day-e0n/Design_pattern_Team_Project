package state;

import observer.BreakdownReason;

// 수리 중인 상태
class RepairingState implements BikeStateInterface {
    @Override
    public void completeRepair(BikeState bike) {
        System.out.println(">> [State Logic] " + bike.getBikeId() + " : 수리 작업 완료.");
        // 상태 변경은 '수리 완료' 알림을 받은 RentalService가
        // N분 후 'moveToStation'을 호출할 때 발생합니다.
    }

    @Override
    public void moveToStation(BikeState bike) {
        System.out.println(">> [State Change] " + bike.getBikeId() + " : '수리 중' -> '사용 가능' (스테이션 전달 완료)");
        bike.setState(new AvailableState());
    }

    // 수리 중인 자전거는 고장신고/수리시작 불가
    @Override
    public void reportBroken(BikeState bike, BreakdownReason reason) { /* Already in repair */ }
    @Override
    public void startRepair(BikeState bike) { /* Already in repair */ }
    @Override
    public String getStatus() { return "수리 중"; }
}