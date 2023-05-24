package org.ranking.statistic;

import org.ranking.model.TimeUnit;

import java.util.Objects;

public abstract class AbstractStatistic implements Statistic {
    // 统计排行榜名称
    protected final String name;

    // 统计排行榜最大容量
    protected final int maxRankingSize;

    // 排行榜时间
    protected final int rankingTime;

    // 滑动窗口队列最大容量
    protected final int maxWindowSize;


    protected final int windowNum;

    // 小窗口的时长，单位秒
    protected final int intervalInSecond;


    public AbstractStatistic(String name, int maxRankingSize, TimeUnit rankingTime, int windowNum) {
        this(name, maxRankingSize, rankingTime, maxRankingSize * 10, windowNum);
    }

    public AbstractStatistic(String name, int maxRankingSize, TimeUnit rankingTime, int maxWindowSize, int windowNum) {
        this.name = name;
        this.maxRankingSize = maxRankingSize;
        this.rankingTime = rankingTime.toSecond();
        this.maxWindowSize = maxWindowSize;
        this.windowNum = windowNum;
        this.intervalInSecond = this.rankingTime / windowNum;
    }

    @Override
    public String toString() {
        return "Statistic{" +
                "name='" + name + '\'' +
                ", maxRankingSize=" + maxRankingSize +
                ", rankingTime=" + rankingTime +
                ", maxWindowSize=" + maxWindowSize +
                ", windowNum=" + windowNum +
                ", intervalInSecond=" + intervalInSecond +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractStatistic that = (AbstractStatistic) o;
        return maxRankingSize == that.maxRankingSize && rankingTime == that.rankingTime && maxWindowSize == that.maxWindowSize && windowNum == that.windowNum && intervalInSecond == that.intervalInSecond && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
