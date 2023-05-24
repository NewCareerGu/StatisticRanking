package org.ranking.model;

/**
 * 排名信息
 *
 * @author Gu
 * @since 2023-05-24
 */
public class RankingObj {
    private String key;
    private long score;

    public RankingObj(String key, long score) {
        this.key = key;
        this.score = score;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public double getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "RankingObj{" +
                "key='" + key + '\'' +
                ", score=" + score +
                '}';
    }
}
