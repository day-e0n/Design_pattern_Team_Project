package observer;

import java.util.List;

public class BreakdownReportSubject extends AbstractSubject {
    private final String bikeId;
    private final List<BreakdownReason> reasons;
    private final String station;
    private final boolean isElectric;

    public BreakdownReportSubject(String bikeId, List<BreakdownReason> reasons, String station, boolean isElectric) {
        this.bikeId = bikeId;
        this.reasons = reasons;
        this.station = station;
        this.isElectric = isElectric;
    }

    public String getBikeId() { return bikeId; }
    public List<BreakdownReason> getReasons() { return reasons; }
    public String getStation() { return station; }
    public boolean isElectric() { return isElectric; }

    public void report() {
        // 옵저버들에게 알림 전송
        notifyObservers();
    }
}