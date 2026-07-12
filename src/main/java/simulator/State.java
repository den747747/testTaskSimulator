package simulator;

public enum State {
    // TODO: Waiting for start state potentially can be removed
    WAITING_FOR_START,
    WAITING_FOR_EMPTY,
    WORKING,
    WAITING_FOR_FULL
}
