package observer;

/**
 * 고장 사유 Enum
 * 각 사유별 기본 수리 시간을 Enum 내부에 직접 저장합니다.
 */
public enum BreakdownReason {
    FLAT_TIRE(5),     // 펑크 (기본 5초)
    BROKEN_CHAIN(8),  // 체인 고장 (기본 8초)
    BRAKE_ISSUE(6),   // 브레이크 문제 (기본 6초)
    BATTERY(5),       // 배터리 문제 (전기자전거 한정, 기본 5초)
    OTHER(4);         // 기타 (기본 4초)

    private final int baseTime; // 기본 수리 시간 (초)

    // Enum 생성자
    BreakdownReason(int baseTime) {
        this.baseTime = baseTime;
    }

    // 기본 수리 시간을 반환하는 Getter
    public int getBaseTime() {
        return baseTime;
    }
}