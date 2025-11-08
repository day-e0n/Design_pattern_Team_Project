/**
 * BikeState (자전거 상태) 인터페이스
 * 자전거의 상태에 따라 동작이 달라집니다.
 */
interface BikeState {
    // 각 상태에서 이뤄질 수 있는 행동들을 정의합니다.
    void reportBroken(Bike bike, BreakdownReason reason);
    void startRepair(Bike bike);
    void completeRepair(Bike bike);
    void moveToStation(Bike bike);
    String getStatus();
}