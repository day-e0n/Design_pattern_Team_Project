package core;

/**
 * 팩토리 메소드 패턴
 */

// 자전거 상태 enum
enum BicycleStatus {
    AVAILABLE("대여가능"),
    RENTED("대여중"),
    MAINTENANCE("정비중"),
    BROKEN("고장");
    
    private String description;
    
    BicycleStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}

class Bicycle {
    protected String id;
    protected String type;
    protected BicycleStatus status;
    protected String location;
    protected String registrationDate;
    protected String lastMaintenanceDate;
    
    public Bicycle(String id, String type) {
        this.id = id;
        this.type = type;
        this.status = BicycleStatus.AVAILABLE;
        this.location = "본부";
        this.registrationDate = java.time.LocalDate.now().toString();
        this.lastMaintenanceDate = this.registrationDate;
    }
    
    // Getter 메소드들 (뺄지 말지 고민)
    public String getId() { return id; }
    public String getType() { return type; }
    public BicycleStatus getStatus() { return status; }
    public String getLocation() { return location; }
    public String getRegistrationDate() { return registrationDate; }
    public String getLastMaintenanceDate() { return lastMaintenanceDate; }
    
    // Setter 메소드들
    public void setStatus(BicycleStatus status) { this.status = status; }
    public void setLocation(String location) { this.location = location; }
    public void setLastMaintenanceDate(String date) { this.lastMaintenanceDate = date; }
    
    // 사용자가 보는 자전거 정보
    public String toString() {
        return String.format("%s (ID: %s, 상태: %s, 위치: %s)", 
                           type, id, status.getDescription(), location);
    }
    // 관리자가 보는 자전거 정보 (상세한 정보임)
    public String getDetailedInfo() {
        return String.format("ID: %s\n유형: %s\n상태: %s\n위치: %s\n등록일: %s\n마지막 정비일: %s", 
                           id, type, status.getDescription(), location, registrationDate, lastMaintenanceDate);
    }
}

class RegularBicycle extends Bicycle {
    public RegularBicycle(String id) {
        super(id, "일반자전거");
    }
}

class ElectricBicycle extends Bicycle {
    public ElectricBicycle(String id) {
        super(id, "전기자전거");
    }
}

// 팩토리 클래스
abstract class BicycleFactory {
    public abstract Bicycle createBicycle(String id);
}

class RegularBicycleFactory extends BicycleFactory {
    public Bicycle createBicycle(String id) {
        return new RegularBicycle(id);
    }
}

class ElectricBicycleFactory extends BicycleFactory {
    public Bicycle createBicycle(String id) {
        return new ElectricBicycle(id);
    }
}