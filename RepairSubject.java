import java.util.ArrayList;
import java.util.List;

/**
 * 고장 사유 Enum
 * 각 사유별 기본 수리 시간을 Enum 내부에 직접 저장합니다.
 */
enum BreakdownReason {
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

/**
 * 공통 Subject 로직 (옵저버 관리)
 */
abstract class AbstractSubject implements Subject {
    private List<Observer> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        // Pull 방식이므로 Subject 자신(this)을 전달
        for (Observer observer : observers) {
            observer.update(this);
        }
    }
}

/**
 * 1. 자전거 고장 신고 Subject
 * Pull 방식: Observer가 필요한 데이터를 가져갈 수 있도록 Getter를 제공합니다.
 */
class BreakdownReportSubject extends AbstractSubject {
    private final String bikeId;
    private final BreakdownReason reason;
    private final boolean isBroken = true; // 고장 신고는 항상 true

    public BreakdownReportSubject(String bikeId, BreakdownReason reason) {
        this.bikeId = bikeId;
        this.reason = reason;
    }

    // --- Pull을 위한 Getter ---
    public String getBikeId() { return bikeId; }
    public BreakdownReason getReason() { return reason; }
    public boolean isBroken() { return isBroken; }
    // -------------------------

    // 이벤트 발생
    public void report() {
        System.out.println("\n[EVENT] ---------------------------------");
        System.out.println("[Subject] '" + bikeId + "' 고장 신고 발생! (사유: " + reason + ")");
        System.out.println("[Subject] 옵저버에게 알림 전송...");
        notifyObservers(); // 등록된 모든 옵저버에게 알림
        System.out.println("-----------------------------------------\n");
    }
}

/**
 * 2. 자전거 수리 완료 Subject
 */
class RepairCompleteSubject extends AbstractSubject {
    private final String bikeId;
    private final boolean isBroken = false; // 수리 완료는 항상 false

    public RepairCompleteSubject(String bikeId) {
        this.bikeId = bikeId;
    }

    // --- Pull을 위한 Getter ---
    public String getBikeId() { return bikeId; }
    public boolean isBroken() { return isBroken; }
    // -------------------------

    // 이벤트 발생
    public void complete() {
        System.out.println("\n[EVENT] ---------------------------------");
        System.out.println("[Subject] '" + bikeId + "' 수리 완료!");
        System.out.println("[Subject] 옵저버에게 알림 전송...");
        notifyObservers();
        System.out.println("-----------------------------------------\n");
    }
}