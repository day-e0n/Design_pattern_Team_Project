/**
 * 데코레이터 패턴 - 자전거에 기능 추가 (간단 버전)
 */

// 기본 자전거
class SimpleBicycle {
    protected String id;
    
    public SimpleBicycle(String id) {
        this.id = id;
    }
    
    public String getInfo() {
        return "자전거 " + id;
    }
}

// 데코레이터 추상 클래스
abstract class FeatureDecorator extends SimpleBicycle {
    protected SimpleBicycle bicycle;
    
    public FeatureDecorator(SimpleBicycle bicycle) {
        super(bicycle.id);
        this.bicycle = bicycle;
    }
}

// GPS 기능 추가
class GPSDecorator extends FeatureDecorator {
    public GPSDecorator(SimpleBicycle bicycle) {
        super(bicycle);
    }
    
    @Override
    public String getInfo() {
        return bicycle.getInfo() + " + GPS";
    }
}

// 잠금 기능 추가
class LockDecorator extends FeatureDecorator {
    public LockDecorator(SimpleBicycle bicycle) {
        super(bicycle);
    }
    
    @Override
    public String getInfo() {
        return bicycle.getInfo() + " + 스마트잠금";
    }
}