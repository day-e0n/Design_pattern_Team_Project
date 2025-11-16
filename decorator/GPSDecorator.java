package decorator;

public class GPSDecorator extends FeatureDecorator {
    public GPSDecorator(SimpleBicycle bicycle) {
        super(bicycle);
    }

    @Override
    public String getInfo() {
        return bicycle.getInfo() + " + GPS";
    }

    // 필요하면 위치 추적/업데이트 메서드 확장 가능
}
