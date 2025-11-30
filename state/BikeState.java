package state;

import core.BicycleStatus;
import java.util.List;
import observer.BreakdownReason;

public class BikeState {
    private final String bikeId;
    private BikeStateInterface state;

    public BikeState(String bikeId) {
        this.bikeId = bikeId;
        this.state = new AvailableState(); 
    }

    public void setState(BikeStateInterface state) {
        this.state = state;
    }

    public void reportBroken(List<BreakdownReason> reasons) {
        state.reportBroken(this, reasons);
    }

    public boolean canRent() { return state.canRent(); }
    public boolean canDelete() { return state.canDelete(); }
    public boolean canMove() { return state.canMove(); }
    public boolean canReport() { return state.canReport(); }

    public String getStatus() {
        return state.getStatus();
    }
    
    public BicycleStatus getBicycleStatus() {
        return state.getBicycleStatus();
    }
    
    public String getBikeId() { return bikeId; }
}