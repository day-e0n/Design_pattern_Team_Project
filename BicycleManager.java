import java.util.*;
import java.time.LocalDate;

/*
 * 자전거 관리 시스템
 * - 자전거 추가, 삭제, 조회, 수정 기능
 * - 상태 관리 (정상/고장/대여중/정비중)
 */
class BicycleManager {
    private Map<String, Bicycle> bicycles;
    private BicycleFactory regularFactory;
    private BicycleFactory electricFactory;
    private LocationManager locationManager;  // 위치 관리자 추가
    
    public BicycleManager() {
        this.bicycles = new HashMap<>();
        this.regularFactory = new RegularBicycleFactory();
        this.electricFactory = new ElectricBicycleFactory();
        this.locationManager = LocationManager.getInstance();  // 싱글톤 인스턴스
        
        // 초기 데이터 추가
        addInitialBicycles();
    }
    
    private void addInitialBicycles() {
        // 기본 자전거 몇 대 추가 (위치는 임의로 지정: 보정, 상현, 죽전, 성복)
        addBicycle("R001", "regular", "보정동");
        addBicycle("R002", "regular", "상현동");
        addBicycle("E001", "electric", "죽전동");
        addBicycle("E002", "electric", "성복동");
    }
    
    // 자전거 추가
    public boolean addBicycle(String id, String type, String location) {
        if (bicycles.containsKey(id)) {
            System.out.println("오류: 이미 존재하는 자전거 ID입니다.");
            return false;
        }
        
        Bicycle bicycle;
        if (type.equals("regular")) {
            bicycle = regularFactory.createBicycle(id);
        } else if (type.equals("electric")) {
            bicycle = electricFactory.createBicycle(id);
        } else {
            System.out.println("오류: 잘못된 자전거 유형입니다. (regular 또는 electric)");
            return false;
        }
        
        bicycle.setLocation(location);
        bicycles.put(id, bicycle);
        
        // 위치 관리자에 등록
        locationManager.registerBicycle(id, location);
        
        System.out.println("자전거가 성공적으로 추가되었습니다: " + bicycle);
        return true;
    }
    
    // 자전거 삭제
    public boolean removeBicycle(String id) {
        if (!bicycles.containsKey(id)) {
            System.out.println("오류: 존재하지 않는 자전거 ID입니다.");
            return false;
        }
        
        Bicycle bicycle = bicycles.get(id);
        if (bicycle.getStatus() == BicycleStatus.RENTED) {
            System.out.println("오류: 대여 중인 자전거는 삭제할 수 없습니다.");
            return false;
        }
        
        bicycles.remove(id);
        
        // 위치 관리자에서도 제거
        locationManager.removeBicycle(id);
        
        System.out.println("자전거가 성공적으로 삭제되었습니다: " + id);
        return true;
    }
    
    // 자전거 조회
    public Bicycle getBicycle(String id) {
        return bicycles.get(id);
    }
    
    // 모든 자전거 목록 조회
    public void listAllBicycles() {
        if (bicycles.isEmpty()) {
            System.out.println("등록된 자전거가 없습니다.");
            return;
        }
        
        System.out.println("\n==== 전체 자전거 목록 ====");
        for (Bicycle bicycle : bicycles.values()) {
            System.out.println(bicycle);
        }
    }
    
    // 상태별 자전거 조회
    public void listBicyclesByStatus(BicycleStatus status) {
        System.out.println("\n==== " + status.getDescription() + " 자전거 목록 ====");
        boolean found = false;
        for (Bicycle bicycle : bicycles.values()) {
            if (bicycle.getStatus() == status) {
                System.out.println(bicycle);
                found = true;
            }
        }
        if (!found) {
            System.out.println("해당 상태의 자전거가 없습니다.");
        }
    }
    
    // 자전거 상태 변경
    public boolean changeBicycleStatus(String id, BicycleStatus newStatus) {
        Bicycle bicycle = bicycles.get(id);
        if (bicycle == null) {
            System.out.println("오류: 존재하지 않는 자전거 ID입니다.");
            return false;
        }
        
        BicycleStatus oldStatus = bicycle.getStatus();
        bicycle.setStatus(newStatus);
        
        // 정비 완료 시 정비일 업데이트
        if (newStatus == BicycleStatus.AVAILABLE && 
            (oldStatus == BicycleStatus.MAINTENANCE || oldStatus == BicycleStatus.BROKEN)) {
            bicycle.setLastMaintenanceDate(LocalDate.now().toString());
        }
        
        System.out.printf("자전거 %s의 상태가 '%s'에서 '%s'로 변경되었습니다.\n", 
                         id, oldStatus.getDescription(), newStatus.getDescription());
        return true;
    }
    
    // 자전거 위치 변경
    public boolean changeBicycleLocation(String id, String newLocation) {
        Bicycle bicycle = bicycles.get(id);
        if (bicycle == null) {
            System.out.println("오류: 존재하지 않는 자전거 ID입니다.");
            return false;
        }
        
        String oldLocation = bicycle.getLocation();
        bicycle.setLocation(newLocation);
        
        // 위치 관리자에 업데이트 (옵저버들에게 자동 알림)
        locationManager.updateBicycleLocation(id, newLocation);
        
        return true;
    }
    
    // 자전거 상세 정보 조회
    public void showBicycleDetails(String id) {
        Bicycle bicycle = bicycles.get(id);
        if (bicycle == null) {
            System.out.println("오류: 존재하지 않는 자전거 ID입니다.");
            return;
        }
        
        System.out.println("\n==== 자전거 상세 정보 ====");
        System.out.println(bicycle.getDetailedInfo());
    }
    
    // 대여 가능한 자전거 목록
    public List<Bicycle> getAvailableBicycles() {
        List<Bicycle> available = new ArrayList<>();
        for (Bicycle bicycle : bicycles.values()) {
            if (bicycle.getStatus() == BicycleStatus.AVAILABLE) {
                available.add(bicycle);
            }
        }
        return available;
    }
    
    // 자전거 대여
    public boolean rentBicycle(String id) {
        Bicycle bicycle = bicycles.get(id);
        if (bicycle == null) {
            System.out.println("오류: 존재하지 않는 자전거 ID입니다.");
            return false;
        }
        
        if (bicycle.getStatus() != BicycleStatus.AVAILABLE) {
            System.out.println("오류: 대여할 수 없는 상태의 자전거입니다. (현재 상태: " + 
                             bicycle.getStatus().getDescription() + ")");
            return false;
        }
        
        bicycle.setStatus(BicycleStatus.RENTED);
        System.out.println("자전거 " + id + "가 대여되었습니다.");
        return true;
    }
    
    // 자전거 반납
    public boolean returnBicycle(String id, String returnLocation) {
        Bicycle bicycle = bicycles.get(id);
        if (bicycle == null) {
            System.out.println("오류: 존재하지 않는 자전거 ID입니다.");
            return false;
        }
        
        if (bicycle.getStatus() != BicycleStatus.RENTED) {
            System.out.println("오류: 대여 중이 아닌 자전거입니다.");
            return false;
        }
        
        bicycle.setStatus(BicycleStatus.AVAILABLE);
        bicycle.setLocation(returnLocation);
        
        // 위치 관리자에 업데이트 (옵저버들에게 자동 알림)
        locationManager.updateBicycleLocation(id, returnLocation);
        
        return true;
    }
    
    // 통계 정보
    public void showStatistics() {
        System.out.println("\n==== 자전거 현황 통계 ====");
        Map<BicycleStatus, Integer> statusCount = new HashMap<>();
        Map<String, Integer> typeCount = new HashMap<>();
        
        for (Bicycle bicycle : bicycles.values()) {
            // 상태별 통계
            statusCount.put(bicycle.getStatus(), 
                          statusCount.getOrDefault(bicycle.getStatus(), 0) + 1);
            // 유형별 통계
            typeCount.put(bicycle.getType(), 
                         typeCount.getOrDefault(bicycle.getType(), 0) + 1);
        }
        
        System.out.println("총 자전거 수: " + bicycles.size());
        System.out.println("\n[상태별 현황]");
        for (BicycleStatus status : BicycleStatus.values()) {
            int count = statusCount.getOrDefault(status, 0);
            System.out.println(status.getDescription() + ": " + count + "대");
        }
        
        System.out.println("\n[유형별 현황]");
        for (Map.Entry<String, Integer> entry : typeCount.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + "대");
        }
    }
}