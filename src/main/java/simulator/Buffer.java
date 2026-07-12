package simulator;

import java.util.logging.Logger;

public final class Buffer {

    private static final Logger LOGGER = Logger.getLogger(Buffer.class.getName());

    private final FakeApi fakeApi;

    public Buffer(FakeApi api) {
        this.fakeApi = api;
    }

    public boolean tryTakeEmptyContainer(int machineId) {
        if (!fakeApi.getEmptyPlaceSensor()) {
            return false;
        }
        fakeApi.setEmptyPlaceSensor(false);
        LOGGER.info("Machine " + machineId + " took empty container");
        return true;
    }

    public boolean unloadFinishedProduct(int machineId) {
        if (fakeApi.getFullPlaceSensor()) {
            return false;
        }
        fakeApi.setFullPlaceSensor(true);
        LOGGER.info("Machine " + machineId + " placed finished product");
        return true;
    }
}
