package simulator;

import java.util.List;
import java.util.logging.Logger;

/**
 * Coordination of the simulation and passing events to the machines.
 */
public final class FactorySimulator implements ApiObserver {

    private static final Logger LOGGER = Logger.getLogger(FactorySimulator.class.getName());

    private final FakeApi api;
    private final List<Machine> machines;

    public FactorySimulator(FakeApi api, int processingSeconds) {
        this.api = api;
        api.setObserver(this);
        Buffer resources = new Buffer(api);
        int offset = processingSeconds / 3;
        this.machines = List.of(
                new Machine(1, 0, processingSeconds, api, resources),
                new Machine(2, offset, processingSeconds, api, resources),
                new Machine(3, offset * 2, processingSeconds, api, resources)
        );
    }

    public void start() {
        machines.forEach(Machine::start);
    }

    public void stop() {
        machines.forEach(Machine::stop);
    }

    //Callback per requirements, simulator will send to all machines
    @Override
    public void onEmptyPlaceSensorChanged() {
        boolean occupied = api.getEmptyPlaceSensor();

        LOGGER.info("Observer: empty place occupied = " + occupied);

        if (occupied) {
            //Can optimize later
            machines.forEach(Machine::onEmptyPlaceAvailable);
        }
    }

    //Same as above
    @Override
    public void onFullPlaceSensorChanged() {
        boolean occupied = api.getFullPlaceSensor();

        LOGGER.info("Observer: full place occupied = " + occupied);

        if (!occupied) {
            machines.forEach(Machine::onFullPlaceAvailable);
        }
    }

    public List<Machine> getMachines() {
        return machines;
    }

}
