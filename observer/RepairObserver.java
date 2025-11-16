package observer;

import strategy.*;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 1. 자전거 대여 Observer
 * (고장 신고) -> 자전거 상태 '고장'으로 즉시 변경
 * (수리 완료) -> N초(분) 후 자전거 상태 '사용 가능'으로 변경
 */
class RentalService implements Observer {
    private final Map<String, Bike> bikeDatabase;
    private final ScheduledExecutorService scheduler;
    private final Random random = new Random();

    public RentalService(Map<String, Bike> bikeDatabase, ScheduledExecutorService scheduler) {
        this.bikeDatabase = bikeDatabase;
        this.scheduler = scheduler;
    }

    @Override
    public void update(Subject subject) {
        // (1) 고장 신고 이벤트 처리
        if (subject instanceof BreakdownReportSubject) {
            // Pull: 필요한 데이터(bikeId, isBroken)만 가져옴
            BreakdownReportSubject report = (BreakdownReportSubject) subject;
            String bikeId = report.getBikeId();
            boolean isBroken = report.isBroken();

            System.out.println("  [Observer: RentalService] " + bikeId + " 고장 알림 수신 (고장 여부: " + isBroken + ")");

            Bike bike = bikeDatabase.get(bikeId);
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

            Bike bike = bikeDatabase.get(bikeId);
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

/**
 * 2. 수리 서비스 Observer
 * (고장 신고) -> (3) N초(분) 후 수리 시작 -> (3) 전략 패턴으로 수리 시간 계산 -> 수리 완료 Subject 실행
 */
class RepairService implements Observer {
    private final Map<String, Bike> bikeDatabase;
    private final ScheduledExecutorService scheduler;
    private final RepairTimeStrategy repairStrategy;
    private final Random random = new Random();
    
    // 수리 완료 시 RentalService에게 알려야 하므로, 
    // 여기서는 간단하게 RentalService 참조를 받습니다.
    // (더 큰 시스템에서는 EventBus나 DI를 통해 주입받습니다.)
    private final RentalService rentalService; 

    public RepairService(Map<String, Bike> bikeDatabase, ScheduledExecutorService scheduler, RepairTimeStrategy repairStrategy, RentalService rentalService) {
        this.bikeDatabase = bikeDatabase;
        this.scheduler = scheduler;
        this.repairStrategy = repairStrategy;
        this.rentalService = rentalService;
    }

    @Override
    public void update(Subject subject) {
        // (1) 고장 신고 이벤트만 처리
        if (subject instanceof BreakdownReportSubject) {
            // Pull: 필요한 데이터(bikeId, reason)만 가져옴
            BreakdownReportSubject report = (BreakdownReportSubject) subject;
            String bikeId = report.getBikeId();
            BreakdownReason reason = report.getReason();

            System.out.println("  [Observer: RepairService] " + bikeId + " 고장 알림 수신 (사유: " + reason + ")");
            
            Bike bike = bikeDatabase.get(bikeId);
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
    private void startRepairProcess(Bike bike, BreakdownReason reason) {
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
    private void finishRepairProcess(Bike bike) {
        bike.completeRepair(); // State 패턴에 따라 수리 완료 로직 수행
        
        // "수리 완료 Subject 객체가 알림 생성"
        RepairCompleteSubject completeSubject = new RepairCompleteSubject(bike.getBikeId());
        
        // 이 알림은 RentalService만 받으면 됨
        completeSubject.addObserver(rentalService);
        
        // 수리 완료 이벤트 발생!
        completeSubject.complete();
    }
}