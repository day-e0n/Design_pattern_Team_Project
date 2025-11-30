package observer;

import java.util.List;
import state.BikeState;

/**
 * 자전거 대여 Observer
 * 상태 변경 로직을 제거하고 단순 알림 수신 로그만 출력합니다.
 */
public class RentalServiceObserver implements ObserverInterface {

    @Override
    public void update(SubjectInterface subject) {
        // (1) 고장 신고 이벤트 수신
        if (subject instanceof BreakdownReportSubject) {
            BreakdownReportSubject report = (BreakdownReportSubject) subject;
            String bikeId = report.getBikeId();
            List<BreakdownReason> reasons = report.getReasons();
            
            // 상태 변경은 ConsoleInterface에서 이미 수행되었으므로 로그만 출력
            System.out.println("  [Observer: RentalService] " + bikeId + " 고장 알림 수신 (사유: " + reasons + ")");
        }
        // (2) 수리 완료 이벤트 수신
        else if (subject instanceof RepairCompleteSubject) {
            RepairCompleteSubject report = (RepairCompleteSubject) subject;
            String bikeId = report.getBikeId();

            System.out.println("  [Observer: RentalService] " + bikeId + " 수리 완료 알림 수신");
        }
    }
}