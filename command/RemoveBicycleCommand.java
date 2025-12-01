// RemoveBicycleCommand.java

package command;

import core.BicycleManager;

public class RemoveBicycleCommand implements Command {
    private final BicycleManager bicycleManager;
    private final String id;

    public RemoveBicycleCommand(BicycleManager bicycleManager, String id) {
        this.bicycleManager = bicycleManager;
        this.id = id;
    }

    @Override
    public void execute() {
        bicycleManager.removeBicycle(id);
    }
}
