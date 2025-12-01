// AddBicycleCommand.java

package command;

import core.BicycleManager;

public class AddBicycleCommand implements Command {
    private final BicycleManager bicycleManager;
    private final String id;
    private final String type;
    private final String station;

    public AddBicycleCommand(BicycleManager bicycleManager,
                             String id, String type, String station) {
        this.bicycleManager = bicycleManager;
        this.id = id;
        this.type = type;
        this.station = station;
    }

    @Override
    public void execute() {
        bicycleManager.addBicycle(id, type, station);
    }
}
