// ListByStatusCommand.java

package command;

import core.BicycleManager;
import core.BicycleStatus;

public class ListByStatusCommand implements Command {
    private final BicycleManager bicycleManager;
    private final BicycleStatus status;

    public ListByStatusCommand(BicycleManager bicycleManager,
                               BicycleStatus status) {
        this.bicycleManager = bicycleManager;
        this.status = status;
    }

    @Override
    public void execute() {
        bicycleManager.listBicyclesByStatus(status);
    }
}
