/**
 * RepairTimeStrategy (수리 시간 계산 전략) 인터페이스
 * 고장 사유에 따라 수리 시간을 계산하는 알고리즘을 캡슐화합니다.
 */
interface RepairTimeStrategy {
    /**
     * @param reason 고장 사유
     * @return 계산된 수리 시간 (시뮬레이션에서는 초 단위)
     */
    int calculateRepairTime(BreakdownReason reason);
}