package observer;

import state.BikeState;
import strategy.BreakdownReason;
import strategy.RepairStrategyInterface;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 2. 수리 서비스 Observer
 * (고장 신고) -> (3) N초(분) 후 수리 시작 -> (3) 전략 패턴으로 수리 시간 계산 -> 수리 완료 Subject 실행
 */
public class RepairServiceObserver implements Observer {
    private final Map<String, BikeState> bikeDatabase;
    private final ScheduledExecutorService scheduler;
    private final RepairStrategyInterface repairStrategy;
    private final Random random = new Random();
    
    // 수리 완료 시 RentalService에게 알려야 하므로 참조를 받음
    private final RentalServiceObserver rentalService; 

    public RepairServiceObserver(Map<String, BikeState> bikeDatabase, ScheduledExecutorService scheduler, RepairStrategyInterface repairStrategy, RentalServiceObserver rentalService) {
        this.bikeDatabase = bikeDatabase;
        this.scheduler = scheduler;
        this.repairStrategy = repairStrategy;
        this.rentalService = rentalService;
    }

    @Override
    public void update(SubjectInterface subject) {
        // (1) 고장 신고 이벤트만 처리
        if (subject instanceof BreakdownReportSubject) {
            // Pull: 필요한 데이터(bikeId, reason)만 가져옴
            BreakdownReportSubject report = (BreakdownReportSubject) subject;
            String bikeId = report.getBikeId();
            BreakdownReason reason = report.getReason();

            System.out.println("  [Observer: RepairService] " + bikeId + " 고장 알림 수신 (사유: " + reason + ")");
            
            BikeState bike = bikeDatabase.get(bikeId);
            if (bike != null) {
                // (2) N(random)초 후 수리 시작
                int delay = random.nextInt(3, 6); // N(3~5)초(분)
                System.out.println("  [Observer: RepairService] " + delay + "초 후 수리를 시작합니다.");
                
                scheduler.schedule(() -> {
                    startRepairProcess(bike, reason);
                }, delay, TimeUnit.SECONDS);
            }
        }
    }

    /**
     * N초(분) 지연 후 실제 수리 프로세스 시작
     */
    private void startRepairProcess(BikeState bike, BreakdownReason reason) {
        // (2) 자전거 상태 '수리 중'으로 변경 (State 패턴)
        bike.startRepair();
        System.out.println("  [Scheduler] " + bike.getBikeId() + " 상태: " + bike.getStatus());

        // (3) 전략 패턴으로 수리 시간 계산
        int repairTime = repairStrategy.calculateRepairTime(reason);
        System.out.println("  [Observer: RepairService] " + bike.getBikeId() + " 수리 시작. (사유: " + reason + ", 예상 시간: " + repairTime + "초)");

        // (3) 계산된 시간(repairTime) 후 수리 완료
        scheduler.schedule(() -> {
            finishRepairProcess(bike);
        }, repairTime, TimeUnit.SECONDS);
    }

    /**
     * 수리 완료 처리 및 '수리 완료 Subject' 알림 생성
     */
    private void finishRepairProcess(BikeState bike) {
        bike.completeRepair(); // State 패턴에 따라 수리 완료 로직 수행
        
        // "수리 완료 Subject 객체가 알림 생성"
        RepairCompleteSubject completeSubject = new RepairCompleteSubject(bike.getBikeId());
        
        // 이 알림은 RentalService만 받으면 됨
        completeSubject.addObserver(rentalService);
        
        // 수리 완료 이벤트 발생!
        completeSubject.complete();
    }
}