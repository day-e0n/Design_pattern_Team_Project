package observer;

import core.Bicycle;
import core.BicycleManager;
import core.BicycleStatus;
import core.ConsoleInterface;
import core.LocationManager;
import state.AvailableState;
import state.RepairingState;
import strategy.RepairStrategyInterface;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RepairServiceObserver implements ObserverInterface {
    private final BicycleManager bicycleManager;
    private final ScheduledExecutorService scheduler;
    private final RepairStrategyInterface repairStrategy;
    private final Random random = new Random();

    public RepairServiceObserver(BicycleManager bicycleManager, ScheduledExecutorService scheduler, RepairStrategyInterface repairStrategy) {
        this.bicycleManager = bicycleManager;
        this.scheduler = scheduler;
        this.repairStrategy = repairStrategy;
    }

    @Override
    public void update(SubjectInterface subject) {
        if (subject instanceof BreakdownReportSubject) {
            BreakdownReportSubject report = (BreakdownReportSubject) subject;
            String bikeId = report.getBikeId();
            
            Bicycle bike = bicycleManager.getBicycle(bikeId);
            if (bike != null) {
                // 1~5초 무작위 지연 후 수리 센터 이동 시작
                int startDelay = random.nextInt(5) + 1;
                scheduler.schedule(() -> startMoveToCenter(bike, report), startDelay, TimeUnit.SECONDS);
            }
        }
    }

    // 1단계: 수리 센터로 이동
    private void startMoveToCenter(Bicycle bike, BreakdownReportSubject report) {
        bike.getBikeState().setState(new RepairingState());
        
        // LocationManager를 통해 이동 시간 계산
        int moveTime = LocationManager.getInstance().getMoveTime(report.getStation());
        
        printAdminMessage(">> [이동] 자전거 " + bike.getId() + "가 수리 센터로 이동 중입니다.");

        scheduler.schedule(() -> startRepair(bike, report, report.getStation()), moveTime, TimeUnit.SECONDS);
    }

    // 2단계: 수리 진행
    private void startRepair(Bicycle bike, BreakdownReportSubject report, String originStation) {
        printAdminMessage(">> [수리] 자전거 " + bike.getId() + "가 수리 센터에서 수리 중입니다.");

        // Strategy 패턴으로 수리 시간 계산
        int repairTime = repairStrategy.calculateRepairTime(report.isElectric(), report.getReasons());

        scheduler.schedule(() -> moveBackToStation(bike, originStation), repairTime, TimeUnit.SECONDS);
    }

    // 3단계: 스테이션으로 복귀
    private void moveBackToStation(Bicycle bike, String station) {
        int moveTime = LocationManager.getInstance().getMoveTime(station);
        
        printAdminMessage(">> [이동] 자전거 " + bike.getId() + "가 스테이션으로 이동 중입니다.");

        scheduler.schedule(() -> completeRepair(bike), moveTime, TimeUnit.SECONDS);
    }

    // 4단계: 완료
    private void completeRepair(Bicycle bike) {
        bicycleManager.changeBicycleStatus(bike.getId(), BicycleStatus.AVAILABLE);
        bike.getBikeState().setState(new AvailableState());
        printAdminMessage(">> [완료] 자전거 " + bike.getId() + " 수리가 완료되었습니다.");
    }

    // 관리자 모드일 때만 메시지 출력
    private void printAdminMessage(String msg) {
        if (ConsoleInterface.isAdminMode) {
            System.out.println(msg);
        }
    }
}