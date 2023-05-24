package org.ranking;

import org.ranking.model.TimeUnit;
import org.ranking.statistic.RedisStatistic;
import org.ranking.statistic.Statistic;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        RedisStatistic statistic = new RedisStatistic("Zaazsss", 10, new TimeUnit(10, TimeUnit.Unit.SECOND), 10, 10);
        for (int i = 0; i < 1000; i++) {
            statistic.increaseScore("zzz" + i % 100, i);
            Thread.sleep(3);
            System.out.println(i);
        }
        statistic.reduceRanking2();
        System.out.println();
    }
}