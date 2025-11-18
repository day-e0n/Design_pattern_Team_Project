package observer;

/**
 * 자전거 고장 신고 Subject
 * Pull 방식: Observer가 필요한 데이터를 가져갈 수 있도록 Getter를 제공합니다.
 */
public class BreakdownReportSubject extends AbstractSubject {
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