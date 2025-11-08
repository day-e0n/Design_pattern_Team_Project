/**
 * 자전거 클래스 (State 패턴의 Context)
 * 현재 자신의 상태(State) 객체를 가집니다.
 */
class Bike {
    private final String bikeId;
    private BikeState state; // 현재 상태

    public Bike(String bikeId) {
        this.bikeId = bikeId;
        this.state = new AvailableState(); // 초기 상태는 '사용 가능'
    }

    // 상태 변경은 Bike 객체 자신만이 할 수 있도록 package-private 설정
    void setState(BikeState state) {
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

/**
 * 1. 사용 가능 상태
 */
class AvailableState implements BikeState {
    @Override
    public void reportBroken(Bike bike, BreakdownReason reason) {
        System.out.println(">> [State Change] " + bike.getBikeId() + " : '사용 가능' -> '고장'");
        bike.setState(new BrokenState());
    }

    // 사용 가능한 자전거는 수리/수리완료/스테이션이동 불가
    @Override
    public void startRepair(Bike bike) { /* Do nothing */ }
    @Override
    public void completeRepair(Bike bike) { /* Do nothing */ }
    @Override
    public void moveToStation(Bike bike) { /* Do nothing */ }
    @Override
    public String getStatus() { return "사용 가능"; }
}

/**
 * 2. 고장 상태
 */
class BrokenState implements BikeState {
    @Override
    public void startRepair(Bike bike) {
        System.out.println(">> [State Change] " + bike.getBikeId() + " : '고장' -> '수리 중'");
        bike.setState(new RepairingState());
    }

    // 고장난 자전거는 고장신고/수리완료/스테이션이동 불가
    @Override
    public void reportBroken(Bike bike, BreakdownReason reason) { /* Already broken */ }
    @Override
    public void completeRepair(Bike bike) { /* Do nothing */ }
    @Override
    public void moveToStation(Bike bike) { /* Do nothing */ }
    @Override
    public String getStatus() { return "고장"; }
}

/**
 * 3. 수리 중 상태
 */
class RepairingState implements BikeState {
    @Override
    public void completeRepair(Bike bike) {
        System.out.println(">> [State Logic] " + bike.getBikeId() + " : 수리 작업 완료.");
        // 상태 변경은 '수리 완료' 알림을 받은 RentalService가
        // N분 후 'moveToStation'을 호출할 때 발생합니다.
    }

    @Override
    public void moveToStation(Bike bike) {
        System.out.println(">> [State Change] " + bike.getBikeId() + " : '수리 중' -> '사용 가능' (스테이션 전달 완료)");
        bike.setState(new AvailableState());
    }

    // 수리 중인 자전거는 고장신고/수리시작 불가
    @Override
    public void reportBroken(Bike bike, BreakdownReason reason) { /* Already in repair */ }
    @Override
    public void startRepair(Bike bike) { /* Already in repair */ }
    @Override
    public String getStatus() { return "수리 중"; }
}