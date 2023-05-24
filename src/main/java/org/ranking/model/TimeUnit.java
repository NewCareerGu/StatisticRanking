package org.ranking.model;

public class TimeUnit {
    private int time;
    private Unit unit;

    public enum Unit {
        SECOND,
        MINUTE,
        HOUR,
        DAY,
    }

    public TimeUnit(int time, Unit unit) {
        this.time = time;
        this.unit = unit;
    }

    public int toSecond() {
        int seconds = 1;
        switch (unit) {
            case SECOND:
                break;
            case MINUTE:
                seconds = 60;
                break;
            case HOUR:
                seconds = 3600;
                break;
            case DAY:
                seconds = 86400;
                break;
        }

        return time * seconds;
    }

}
