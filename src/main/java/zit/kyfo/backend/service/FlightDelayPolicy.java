package zit.kyfo.backend.service;

public final class FlightDelayPolicy {

    public static final int COMPENSATION_THRESHOLD_MINUTES = 120;

    private FlightDelayPolicy() {
    }

    public static boolean isDelayed(int delayMinutes) {
        return delayMinutes >= COMPENSATION_THRESHOLD_MINUTES;
    }
}
