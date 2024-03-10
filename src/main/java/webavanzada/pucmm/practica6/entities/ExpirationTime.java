package webavanzada.pucmm.practica6.entities;

public enum ExpirationTime {
    ONE_YEAR(365 * 24 * 3600),
    ONE_MONTH(30 * 24 * 3600),
    ONE_WEEK(7 * 24 * 3600),
    ONE_DAY(24 * 3600),
    ONE_HOUR(3600),
    ONE_MINUTE(60);
    private final long seconds;

    ExpirationTime(long seconds) {
        this.seconds = seconds;
    }

    public long getSeconds() {
        return seconds;
    }
}
