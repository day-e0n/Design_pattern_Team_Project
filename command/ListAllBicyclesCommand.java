// ListAllBicyclesCommand.java

package command;

import core.BicycleManager;

public class ListAllBicyclesCommand implements Command {
    private final BicycleManager bicycleManager;

    public ListAllBicyclesCommand(BicycleManager bicycleManager) {
        this.bicycleManager = bicycleManager;
    }

    @Override
    public void execute() {
        bicycleManager.listAllBicycles();
    }
}
