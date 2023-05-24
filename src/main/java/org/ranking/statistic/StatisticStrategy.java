package org.ranking.statistic;

import org.ranking.util.PropUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatisticStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticStrategy.class);

    private static Mode mode;


    static {
        String value = PropUtil.getProperty("mode", "alone");
        mode = Mode.valueOf(value);
    }

    public static Statistic getStatistic(String name) {
       switch (mode){
           case ALONE:
               return null;
           case REDIS:
               return null;
       }
       return null;
    }

    enum Mode {
        ALONE,
        REDIS;
    }
}
