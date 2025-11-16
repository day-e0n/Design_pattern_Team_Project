package decorator;

public abstract class FeatureDecorator extends SimpleBicycle {
    protected final SimpleBicycle bicycle;

    public FeatureDecorator(SimpleBicycle bicycle) {
        super(bicycle.id);
        this.bicycle = bicycle;
    }

    @Override
    public String getInfo() {
        return bicycle.getInfo();
    }

    @Override
    public void lock() {
        bicycle.lock();
    }

    @Override
    public void unlock() {
        bicycle.unlock();
    }

    @Override
    public boolean isLocked() {
        return bicycle.isLocked();
    }
}
