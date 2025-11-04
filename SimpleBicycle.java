/**
 * 데코레이터 패턴
 */

public class SimpleBicycle {
    protected final String id;
    private boolean locked = true; // 기본 잠금

    public SimpleBicycle(String id) {
        this.id = id;
    }

    public String getId() { return id; }

    public String getInfo() {
        return "자전거 " + id + (locked ? " [잠김]" : " [해제]");
    }

    public void lock() {
        if (!locked) {
            locked = true;
            System.out.println("[" + id + "] 잠금 완료");
        } else {
            System.out.println("[" + id + "] 이미 잠금 상태");
        }
    }

    public void unlock() {
        if (locked) {
            locked = false;
            System.out.println("[" + id + "] 잠금 해제");
        } else {
            System.out.println("[" + id + "] 이미 해제 상태");
        }
    }

    public boolean isLocked() { return locked; }
}
