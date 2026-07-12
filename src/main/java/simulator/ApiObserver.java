package simulator;
/**
 * Receives notification from the external API when a sensor state changes.
 */
public interface ApiObserver {
    void onEmptyPlaceSensorChanged();
    void onFullPlaceSensorChanged();
}