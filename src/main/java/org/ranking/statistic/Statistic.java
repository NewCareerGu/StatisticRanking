package org.ranking.statistic;

import org.ranking.model.RankingObj;

import java.util.List;

public interface Statistic {
    default long increaseScore(String id) {
        return increaseScore(id, 1);
    }

    default long decreaseScore(String id) {
        return decreaseScore(id, 1);
    }

    long increaseScore(String id, int score);

    long decreaseScore(String id, int score);

    default List<RankingObj> getRanking() {
        return getRanking(0, -1);
    }

    List<RankingObj> getRanking(int start, int end);
}
