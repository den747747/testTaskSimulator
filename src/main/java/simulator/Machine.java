package simulator;

import java.util.logging.Logger;

public final class Machine {

    private static final Logger logger = Logger.getLogger(Machine.class.getName());

    private final int id;
    private final int startDelaySeconds;
    private final int processingSeconds;
    private final FakeApi api;
    private final Buffer resources;

    private State state = State.WAITING_FOR_START;
    private int currentTimerId = 0;

    public Machine(
            int id,
            int startDelaySeconds,
            int processingSeconds,
            FakeApi api,
            Buffer resources
    ) {
        this.id = id;
        this.startDelaySeconds = startDelaySeconds;
        this.processingSeconds = processingSeconds;
        this.api = api;
        this.resources = resources;
    }

    public State getState() {
        return state;
    }

    public void start() {
        if (startDelaySeconds == 0) {
            onStartDelayFinished();
            return;
        }

        currentTimerId = api.startTimer(startDelaySeconds, this::onStartDelayFinished);
        logger.info("Machine " + id + ": scheduled start after " + startDelaySeconds + " second(s)");
    }

    public void stop() {
        if (currentTimerId != 0) {
            api.killTimer(currentTimerId);
            currentTimerId = 0;
        }

        logger.info("Machine " + id + ": stopped");
    }

    public void onEmptyPlaceAvailable() {
        if (state == State.WAITING_FOR_EMPTY) {
            takeEmpty();
        }
    }

    public void onFullPlaceAvailable() {
        if (state == State.WAITING_FOR_FULL) {
            unloadFinished();
        }
    }

    private void onStartDelayFinished() {
        if (state != State.WAITING_FOR_START) {
            return;
        }

        currentTimerId = 0;
        state = State.WAITING_FOR_EMPTY;

        logger.info("Machine " + id + ": start delay finished");
        takeEmpty();
    }

    private void takeEmpty() {
        if (state != State.WAITING_FOR_EMPTY) {
            return;
        }

        if (!resources.tryTakeEmptyContainer(id)) {
            return;
        }

        state = State.WORKING;
        currentTimerId = api.startTimer(processingSeconds, this::finishingProcessing);

        logger.info("Machine " + id + ": started processing for " + processingSeconds + " second(s)");
    }

    private void finishingProcessing() {
        if (state != State.WORKING) {
            return;
        }

        currentTimerId = 0;
        state = State.WAITING_FOR_FULL;

        logger.info("Machine " + id + ": processing finished");
        unloadFinished();
    }

    private void unloadFinished() {
        if (state != State.WAITING_FOR_FULL) {
            return;
        }

        if (!resources.unloadFinishedProduct(id)) {
            return;
        }

        state = State.WAITING_FOR_EMPTY;

        logger.info("Machine " + id + ": delivered finished product");
        takeEmpty();
    }

}
