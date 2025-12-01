// ChangeLocationCommand.java

package command;

import core.BicycleManager;

public class ChangeLocationCommand implements Command {
    private final BicycleManager bicycleManager;
    private final String id;
    private final String newLocation;

    public ChangeLocationCommand(BicycleManager bicycleManager,
                                 String id,
                                 String newLocation) {
        this.bicycleManager = bicycleManager;
        this.id = id;
        this.newLocation = newLocation;
    }

    @Override
    public void execute() {
        bicycleManager.changeBicycleLocation(id, newLocation);
    }
}
