package observer;

import state.BikeState;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 자전거 대여 Observer
 * (고장 신고) -> 자전거 상태 '고장'으로 즉시 변경
 * (수리 완료) -> N초(분) 후 자전거 상태 '사용 가능'으로 변경
 */
public class RentalServiceObserver implements ObserverInterface {
    private final Map<String, BikeState> bikeDatabase;
    private final ScheduledExecutorService scheduler;
    private final Random random = new Random();

    public RentalServiceObserver(Map<String, BikeState> bikeDatabase, ScheduledExecutorService scheduler) {
        this.bikeDatabase = bikeDatabase;
        this.scheduler = scheduler;
    }

    @Override
    public void update(SubjectInterface subject) {
        // (1) 고장 신고 이벤트 처리
        if (subject instanceof BreakdownReportSubject) {
            // Pull: 필요한 데이터(bikeId, isBroken)만 가져옴
            BreakdownReportSubject report = (BreakdownReportSubject) subject;
            String bikeId = report.getBikeId();
            boolean isBroken = report.isBroken();

            System.out.println("  [Observer: RentalService] " + bikeId + " 고장 알림 수신 (고장 여부: " + isBroken + ")");

            BikeState bike = bikeDatabase.get(bikeId);
            if (bike != null && isBroken) {
                // (2) 자전거 상태 '고장'으로 변경 (State 패턴)
                bike.reportBroken(report.getReason());
            }
        }
        // (2) 수리 완료 이벤트 처리
        else if (subject instanceof RepairCompleteSubject) {
            // Pull: 필요한 데이터(bikeId, isBroken)만 가져옴
            RepairCompleteSubject report = (RepairCompleteSubject) subject;
            String bikeId = report.getBikeId();

            System.out.println("  [Observer: RentalService] " + bikeId + " 수리 완료 알림 수신 (고장 여부: " + report.isBroken() + ")");

            BikeState bike = bikeDatabase.get(bikeId);
            if (bike != null) {
                int delay = random.nextInt(3, 6); // N(3~5)초(분) 후 스테이션 전달
                System.out.println("  [Observer: RentalService] " + delay + "초 후 스테이션에 전달하여 '사용 가능' 상태로 변경합니다.");
                
                // (2) 비동기 스케줄링으로 N초(분) 후 상태 변경
                scheduler.schedule(() -> {
                    bike.moveToStation();
                    System.out.println("  [Scheduler] " + bikeId + " 상태: " + bike.getStatus());
                }, delay, TimeUnit.SECONDS);
            }
        }
    }
}