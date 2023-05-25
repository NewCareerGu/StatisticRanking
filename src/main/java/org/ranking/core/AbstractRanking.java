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
    protected final int updatePeriod;

    // 每个小窗口时间=rankingTime/windowNum，单位秒
    protected final int intervalInSecond;

    public AbstractRanking(String name, int maxRankingSize, Time rankingTime, int windowNum) {
        this(name, maxRankingSize, rankingTime, maxRankingSize * 10, windowNum, new Time(10, TimeUnit.MINUTES));
    }

    /**
     * 排行榜构造
     * <p>基于滑动窗口算法, maxWindowSize为小窗口长度，windowNum为小窗口数量，两者越大越精确，消耗性能也会越多； maxRankingSize为排行榜最大长度</p>
     * <p>updatePeriod 为更新汇总排名信息时间，分布式场景下只有一个节点可以初始化并启动定时任务汇总统计信息，其他节点只是上报获取数据</p>
     *
     * @param name 排行榜名称
     * @param maxRankingSize 排行榜最大长度
     * @param rankingTime 排行榜统计时间
     * @param maxWindowSize 小窗口最大长度
     * @param windowNum 小窗口数量
     * @param updatePeriod 更新汇总排名信息时间
     */
    public AbstractRanking(String name, int maxRankingSize, Time rankingTime, int maxWindowSize, int windowNum, Time updatePeriod) {
        AssertionUtil.notEmpty(name, "Name can`t empty.");
        AssertionUtil.assertPositiveNumber(maxRankingSize,"MaxRankingSize must be a positive number.");
        AssertionUtil.assertNotNull(rankingTime,"RankingTime must be not null.");
        AssertionUtil.assertPositiveNumber(maxWindowSize,"MaxWindowSize must be a positive number.");
        AssertionUtil.assertPositiveNumber(windowNum,"WindowNum must be a positive number.");
        AssertionUtil.assertNotNull(updatePeriod,"UpdatePeriod must be not null.");

        this.name = name;
        this.maxRankingSize = maxRankingSize;
        this.rankingTime = rankingTime.toSecond();
        this.maxWindowSize = maxWindowSize;
        this.windowNum = windowNum;
        this.updatePeriod = updatePeriod.toSecond();
        this.intervalInSecond = this.rankingTime / windowNum;
    }

    @Override
    public String toString() {
        return "AbstractRanking{" +
                "name='" + name + '\'' +
                ", maxRankingSize=" + maxRankingSize +
                ", rankingTime=" + rankingTime +
                ", maxWindowSize=" + maxWindowSize +
                ", windowNum=" + windowNum +
                ", updatePeriod=" + updatePeriod +
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
