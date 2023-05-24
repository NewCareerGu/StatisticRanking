package org.ranking.core;

import org.ranking.model.RankingObj;

import java.util.List;

/**
 * 排行榜类接口
 *
 * @author Gu
 * @since 2023-05-24
 */
public interface Ranking {
    /**
     * 为某个id增加1分
     *
     * @param id id
     * @return
     */
    default long increaseScore(String id) {
        return increaseScore(id, 1);
    }

    /**
     * 为某个id减少1分
     *
     * @param id id
     * @return
     */
    default long decreaseScore(String id) {
        return decreaseScore(id, 1);
    }

    /**
     * 为某个id增加自定义分数分
     *
     * @param id id
     * @param score 自定义分数
     * @return
     */
    long increaseScore(String id, int score);

    /**
     * 为某个id减少自定义分数分
     *
     * @param id id
     * @param score 自定义分数
     * @return
     */
    long decreaseScore(String id, int score);

    /**
     * 获取整个排行榜排名，按分数倒序
     *
     * @return 排行榜信息
     */
    default List<RankingObj> getRanking() {
        return getRanking(0, -1);
    }

    /**
     * 获取指定位置排行榜排名，按分数倒序
     *
     * @param start 开始位置
     * @param end 结束位置
     * @return
     */
    List<RankingObj> getRanking(int start, int end);
}
