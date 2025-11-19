package state;

import observer.BreakdownReason;

/**
 * 자전거의 상태에 따라 동작이 달라집니다.
 */
public interface BikeStateInterface {
    // 각 상태에서 이뤄질 수 있는 행동들을 정의합니다.
    void reportBroken(BikeState bike, BreakdownReason reason);
    void startRepair(BikeState bike);
    void completeRepair(BikeState bike);
    void moveToStation(BikeState bike);
    String getStatus();
}