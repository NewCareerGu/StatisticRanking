package org.ranking.statistic;

import java.util.HashMap;
import java.util.Map;

public class StatisticFactory {
    private static StatisticFactory instance = new StatisticFactory();

    private static Map<String,Statistic> map = new HashMap<>();

    private static final Object LOCK = new Object();

    private StatisticFactory(){
    }

    public static StatisticFactory getInstance(){
        return instance;
    }


   /* public Statistic getStatistic(String name){
        if(!map.containsKey(name)){
            synchronized (LOCK){
                if(!map.containsKey(name)){

                }
            }
        }
    }*/
}
