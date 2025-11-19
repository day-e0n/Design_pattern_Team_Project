package core;

/**
 * 자전거 상태 enum
 */
public enum BicycleStatus {
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
