package observer;

/**
 * 자전거 수리 완료 Subject
 */
public class RepairCompleteSubject extends AbstractSubject {
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