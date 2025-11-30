package state;

import core.BicycleStatus;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 상태 객체 생성을 전담하는 팩토리 클래스
 * BicycleStatus Enum에 대응하는 BikeState 구현체를 반환합니다.
 * Map을 사용하여 OCP를 준수하며, 새로운 상태 추가 시 이 파일의 등록 로직만 수정하면 됩니다.
 */
public class BikeStateFactory {
    // 상태별 생성자를 저장하는 Map
    private static final Map<BicycleStatus, Supplier<BikeStateInterface>> stateMap = new HashMap<>();

    static {
        // 상태 객체 매핑 등록
        stateMap.put(BicycleStatus.AVAILABLE, AvailableState::new);
        stateMap.put(BicycleStatus.BROKEN, BrokenState::new);
        stateMap.put(BicycleStatus.MAINTENANCE, RepairingState::new);
        stateMap.put(BicycleStatus.RENTED, RentedState::new);
    }

    /**
     * Enum 상태에 맞는 State 객체를 생성하여 반환
     */
    public static BikeStateInterface create(BicycleStatus status) {
        Supplier<BikeStateInterface> stateSupplier = stateMap.get(status);
        
        if (stateSupplier == null) {
            // 매핑되지 않은 상태가 들어올 경우 기본값으로 '사용 가능' 혹은 예외 처리를 할 수 있습니다.
            // 여기서는 안전하게 AvailableState를 반환하거나 로그를 남길 수 있습니다.
            System.out.println(">> [오류] 알 수 없는 상태입니다: " + status + ". 기본 상태로 설정합니다.");
            return new AvailableState();
        }
        
        return stateSupplier.get();
    }
}