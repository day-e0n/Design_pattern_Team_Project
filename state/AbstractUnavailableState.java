package state;

import core.BicycleStatus;

/**
 * 대여/이동/삭제가 불가능한 상태들의 공통 부모 클래스
 * Template Method 패턴과 유사하게 공통 로직(오류 메시지 출력)을 관리합니다.
 */
public abstract class AbstractUnavailableState implements BikeStateInterface {

    // 공통된 거부 메시지 출력 로직
    private void printRefusalMessage(String action) {
        System.out.println("자전거가 현재 [" + getStatus() + "] 상태라 " + action + "할 수 없습니다.");
    }

    @Override
    public boolean canRent() {
        printRefusalMessage("대여");
        return false;
    }

    @Override
    public boolean canDelete() {
        printRefusalMessage("삭제");
        return false;
    }

    @Override
    public boolean canMove() {
        printRefusalMessage("이동");
        return false;
    }

    @Override
    public boolean canReport() {
        printRefusalMessage("신고");
        return false;
    }
    
    @Override
    public abstract String getStatus();
    
    @Override
    public abstract BicycleStatus getBicycleStatus();
}