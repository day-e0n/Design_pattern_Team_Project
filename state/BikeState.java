package state;

import observer.BreakdownReason;

/**
 * 자전거 클래스 (State 패턴의 Context)
 * 현재 자신의 상태(State) 객체를 가집니다.
 */
public class BikeState {
    private final String bikeId;
    private BikeStateInterface state; // 현재 상태

    public BikeState(String bikeId) {
        this.bikeId = bikeId;
        this.state = new AvailableState(); // 초기 상태는 '사용 가능'
    }

    // 상태 변경은 Bike 객체 자신만이 할 수 있도록 package-private 설정
    void setState(BikeStateInterface state) {
        this.state = state;
    }

    // 모든 행동을 현재 상태 객체에 위임
    public void reportBroken(BreakdownReason reason) {
        state.reportBroken(this, reason);
    }

    public void startRepair() {
        state.startRepair(this);
    }

    public void completeRepair() {
        state.completeRepair(this);
    }

    public void moveToStation() {
        state.moveToStation(this);
    }

    public String getBikeId() {
        return bikeId;
    }

    public String getStatus() {
        return state.getStatus();
    }
}