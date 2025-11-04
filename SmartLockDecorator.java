public class SmartLockDecorator extends FeatureDecorator {
    private boolean smartLockEnabled = true;
    private String unlockCode = generateUnlockCode();
    private int failedAttempts = 0;
    private final int maxFailedAttempts = 3;

    public SmartLockDecorator(SimpleBicycle bicycle) {
        super(bicycle);
    }

    @Override
    public String getInfo() {
        return bicycle.getInfo() + " + 스마트잠금(" + (smartLockEnabled ? "ON" : "OFF") + ")";
    }

    @Override
    public void unlock() {
        if (smartLockEnabled) {
            System.out.println("[스마트 잠금] 코드가 필요합니다. unlockWithCode(code)를 사용하세요.");
            return;
        }
        super.unlock();
    }

    /** 코드로 해제 */
    public boolean unlockWithCode(String inputCode) {
        if (!smartLockEnabled) {
            super.unlock();
            return true;
        }
        if (!isLocked()) {
            System.out.println("[스마트 잠금] 이미 해제 상태입니다.");
            return true;
        }
        if (unlockCode.equals(inputCode)) {
            System.out.println("[스마트 잠금] 코드 일치. 잠금을 해제합니다.");
            super.unlock();
            failedAttempts = 0;
            return true;
        } else {
            failedAttempts++;
            System.out.println("[스마트 잠금] 코드 불일치 (" + failedAttempts + "/" + maxFailedAttempts + ")");
            if (failedAttempts >= maxFailedAttempts) {
                System.out.println("[스마트 잠금] 시도 횟수 초과. 일시적으로 잠금 유지(관리자 알림 필요).");
            }
            return false;
        }
    }

    @Override
    public void lock() {
        super.lock();
        if (smartLockEnabled) {
            this.unlockCode = generateUnlockCode();
            this.failedAttempts = 0;
            System.out.println("[스마트 잠금] 새 잠금 코드 생성: " + unlockCode);
        }
    }

    public void enableSmartLock() {
        this.smartLockEnabled = true;
        this.unlockCode = generateUnlockCode();
        this.failedAttempts = 0;
        System.out.println("[스마트 잠금] 활성화됨. 새 코드: " + unlockCode);
    }

    public void disableSmartLock() {
        this.smartLockEnabled = false;
        System.out.println("[스마트 잠금] 비활성화됨. 일반 잠금/해제 사용");
    }

    public void resetFailedAttempts() {
        this.failedAttempts = 0;
        this.unlockCode = generateUnlockCode();
        System.out.println("[스마트 잠금] 실패 횟수 초기화 및 새 코드 생성: " + unlockCode);
    }

    // 테스트 편의용(실서비스에선 제거 권장)
    public String getCurrentCodeForTest() { return unlockCode; }
    public boolean isSmartLockEnabled() { return smartLockEnabled; }
    public int getFailedAttempts() { return failedAttempts; }

    private String generateUnlockCode() {
        return String.format("%04d", (int)(Math.random() * 10000));
    }
}
