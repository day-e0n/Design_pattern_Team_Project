/**
 * 팩토리 메소드 패턴 - 자전거 생성 (간단 버전)
 */
class Bicycle {
    protected String id;
    protected String type;
    
    public Bicycle(String id, String type) {
        this.id = id;
        this.type = type;
    }
    
    public String getId() { return id; }
    public String getType() { return type; }
    
    public String toString() {
        return type + " (ID: " + id + ")";
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