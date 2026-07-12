package simulator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Local simulation for external API (real API is not accessible).
 * Added implementation to be able to test properly
 * Time is simulated. Expired timers are executed when tick(...) is called
 */
public final class FakeApi {

    private static final Logger LOGGER = Logger.getLogger(FakeApi.class.getName());

    private boolean emptyPlaceSensorOccupied = true;
    private boolean fullPlaceSensorOccupied = false;

    private int nextTimerId = 1;
    private int currentTime = 0;

    private record ScheduledTask(int id, int expiryTime, Runnable callback) {}

    private final List<ScheduledTask> timers = new ArrayList<>();

    private ApiObserver observer;

    public void setObserver(ApiObserver observer) {
        this.observer = observer;
    }

    public void setEmptyPlaceSensor(boolean value) {
        emptyPlaceSensorOccupied = value;
        LOGGER.info("setEmptyPlaceSensor(" + value + ")");
        if (observer != null) observer.onEmptyPlaceSensorChanged();
    }

    public boolean getEmptyPlaceSensor() {
        return emptyPlaceSensorOccupied;
    }

    public void setFullPlaceSensor(boolean value) {
        fullPlaceSensorOccupied = value;
        LOGGER.info("setFullPlaceSensor(" + value + ")");
        if (observer != null) observer.onFullPlaceSensorChanged();
    }

    public boolean getFullPlaceSensor() {
        return fullPlaceSensorOccupied;
    }

    public int startTimer(int seconds, Runnable callback) {
        int id = nextTimerId++;
        timers.add(new ScheduledTask(id, currentTime + seconds, callback));
        LOGGER.info("startTimer(seconds=" + seconds + ") -> id=" + id);
        return id;
    }

    public void killTimer(int id) {
        boolean removed = timers.removeIf(t -> t.id == id);
        if (removed) {
            LOGGER.info("killTimer(id=" + id + ")");
        }
    }

    public void tick(int seconds) throws IllegalArgumentException {
        if (seconds <= 0) throw new IllegalArgumentException("seconds must be more than 0");
        currentTime += seconds;
        List<ScheduledTask> expired = new ArrayList<>();
        timers.removeIf(t -> {
            if (t.expiryTime <= currentTime) {
                expired.add(t);
                return true;
            }
            return false;
        });
        expired.sort(Comparator.comparingInt(t -> t.expiryTime));
        expired.forEach(t -> {
            LOGGER.info("timer expired: id=" + t.id);
            t.callback.run();
        });
    }

    public void close() {
        timers.clear();
    }
}