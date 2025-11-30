package core;

/**
 * 자전거 상태 enum
 */
public enum BicycleStatus {
    AVAILABLE("대여 가능"),
    RENTED("대여 중"),
    MAINTENANCE("수리 중"),
    BROKEN("고장");
    
    private String description;
    
    BicycleStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
