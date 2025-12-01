// ChangeStatusCommand.java

package command;

import core.BicycleManager;
import core.BicycleStatus;

public class ChangeStatusCommand implements Command {
    private final BicycleManager bicycleManager;
    private final String id;
    private final BicycleStatus newStatus;

    public ChangeStatusCommand(BicycleManager bicycleManager,
                               String id,
                               BicycleStatus newStatus) {
        this.bicycleManager = bicycleManager;
        this.id = id;
        this.newStatus = newStatus;
    }

    @Override
    public void execute() {
        bicycleManager.changeBicycleStatus(id, newStatus);
    }
}
