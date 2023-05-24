package org.ranking.core;

import org.ranking.model.Time;
import org.ranking.util.AssertionUtil;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 排行榜抽象类
 *
 * @author Gu
 * @since 2023-05-24
 */
public abstract class AbstractRanking implements Ranking {
    // 统计排行榜名称
    protected final String name;

    // 统计排行榜最大容量
    protected final int maxRankingSize;

    // 排行榜时间，单位秒
    protected final int rankingTime;

    // 滑动窗口队列最大容量
    protected final int maxWindowSize;

    // 总共多少窗口，rankingTime/windowNum 为每个窗口时间
    protected final int windowNum;

    // 每次归并统计数据到排行榜的间隔时间，单位秒
    protected final int reduceTime;

    // 每个小窗口时间=rankingTime/windowNum，单位秒
    protected final int intervalInSecond;

    public AbstractRanking(String name, int maxRankingSize, Time rankingTime, int windowNum) {
        this(name, maxRankingSize, rankingTime, maxRankingSize * 10, windowNum, new Time(10, TimeUnit.MINUTES));
    }

    public AbstractRanking(String name, int maxRankingSize, Time rankingTime, int maxWindowSize, int windowNum, Time reduceTime) {
        AssertionUtil.notEmpty(name, "Name can`t empty.");
        AssertionUtil.assertPositiveNumber(maxRankingSize,"MaxRankingSize must be a positive number.");
        AssertionUtil.assertNotNull(rankingTime,"RankingTime must be not null.");
        AssertionUtil.assertPositiveNumber(maxWindowSize,"MaxWindowSize must be a positive number.");
        AssertionUtil.assertPositiveNumber(windowNum,"WindowNum must be a positive number.");
        AssertionUtil.assertNotNull(reduceTime,"ReduceTime must be not null.");

        this.name = name;
        this.maxRankingSize = maxRankingSize;
        this.rankingTime = rankingTime.toSecond();
        this.maxWindowSize = maxWindowSize;
        this.windowNum = windowNum;
        this.reduceTime = reduceTime.toSecond();
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
        AbstractRanking that = (AbstractRanking) o;
        return maxRankingSize == that.maxRankingSize && rankingTime == that.rankingTime && maxWindowSize == that.maxWindowSize && windowNum == that.windowNum && intervalInSecond == that.intervalInSecond && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
