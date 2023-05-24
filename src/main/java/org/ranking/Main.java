package org.ranking;

import org.ranking.model.RankingObj;
import org.ranking.model.Time;
import org.ranking.core.RedisRanking;
import org.ranking.core.Ranking;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        Ranking statistic = new RedisRanking("HelloWorld", 10, new Time(10, TimeUnit.SECONDS), 10, 10, new Time(1, TimeUnit.SECONDS));
        new Thread(() -> {
            while (true) {
                List<RankingObj> ranking = statistic.getRanking();
                System.out.println(ranking);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        }).start();
        for (int i = 0; i < 1000; i++) {
            long s = System.currentTimeMillis();
            statistic.increaseScore("user" + i % 20, i % 20);
            try {
                Thread.sleep(48);
            } catch (InterruptedException e) {
            }
            long e = System.currentTimeMillis();
            System.out.println("----------" + (e - s));
        }
    }
}