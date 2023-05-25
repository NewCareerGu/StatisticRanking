package org.ranking.model;

import org.ranking.util.AssertionUtil;

import java.util.concurrent.TimeUnit;

/**
 * 时间窗
 *
 * @author Gu
 * @since 2023-05-24
 */
public class Time {
    private int time;
    private TimeUnit unit;

    public Time(int time, TimeUnit unit) {
        AssertionUtil.assertPositiveNumber(time,"Time must be a positive number.");
        AssertionUtil.assertNotNull(unit,"TimeUnit must be not null.");
        this.time = time;
        this.unit = unit;
    }

    public int toSecond() {
        int seconds = 1;
        switch (unit) {
            case SECONDS:
                break;
            case MINUTES:
                seconds = 60;
                break;
            case HOURS:
                seconds = 3600;
                break;
            case DAYS:
                seconds = 86400;
                break;
        }

        return time * seconds;
    }

}
