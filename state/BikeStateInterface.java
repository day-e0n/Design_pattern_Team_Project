package state;

import core.BicycleStatus;
import java.util.List;
import observer.BreakdownReason;

/**
 * 자전거의 상태에 따라 동작이 달라집니다.
 * OCP 적용: 상태 객체가 직접 자신의 Enum 타입을 반환합니다.
 */
public interface BikeStateInterface {
    void reportBroken(BikeState bike, List<BreakdownReason> reasons);
    
    boolean canRent();
    boolean canDelete();
    boolean canMove();
    boolean canReport();
    
    String getStatus();
    
    BicycleStatus getBicycleStatus();
}