package org.ranking.core;

import org.ranking.model.RankingObj;
import org.ranking.model.Time;
import org.ranking.util.JedisUtil;
import org.ranking.util.ScheduledExecutorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;

import java.util.*;
import java.util.stream.Collectors;

/**
 * redis排行榜实现类
 * <p>适用分部署多台节点提供统计数据，在redis中使用滑动窗口方式实时统计，再由定时线程汇总结果</p>
 *
 * @author Gu
 * @since 2023-05-24
 */
public class RedisRanking extends AbstractRanking {
    private static final Logger LOGGER = LoggerFactory.getLogger(JedisUtil.class);
    private static final String METADATA = "_metadata";

    private static final String RANKING = "_ranking";

    public RedisRanking(String name, int maxRankingSize, Time rankingTime, int windowNum) {
        super(name, maxRankingSize, rankingTime, windowNum);
        init(name, windowNum);
    }

    public RedisRanking(String name, int maxRankingSize, Time rankingTime, int maxWindowSize, int windowNum, Time updatePeriod) {
        super(name, maxRankingSize, rankingTime, maxWindowSize, windowNum, updatePeriod);
        init(name, windowNum);
    }

    /**
     * 初始化设置元数据，如果是新建则启动定时线程汇总，已有则只上报和获取数据；
     */
    private void init(String name, int windowNum) {
        String script = "if (redis.call('exists',KEYS[1]) == 0) then\n" +
                "local currentSecond = redis.call('time')[1];\n" +
                "redis.call('hset', KEYS[1], 'startTime', currentSecond);\n" +
                "redis.call('hset', KEYS[1], 'maxRankingSize', ARGV[1]);\n" +
                "redis.call('hset', KEYS[1], 'rankingTime', ARGV[2]);\n" +
                "redis.call('hset', KEYS[1], 'maxWindowSize', ARGV[3]);\n" +
                "redis.call('hset', KEYS[1], 'intervalInSecond', ARGV[4]);\n" +
                "redis.call('hset', KEYS[1], 'windowNum', ARGV[5]);\n" +
                "return nil;\n" +
                "else return redis.call('hgetall', KEYS[1]);\n" +
                "end; ";

        try (Jedis jedis = JedisUtil.getJedis()) {
            Object result = jedis.eval(script, Arrays.asList(name + METADATA),
                    Arrays.asList(Integer.toString(maxRankingSize), Integer.toString(rankingTime),
                            Integer.toString(maxWindowSize), Integer.toString(intervalInSecond),
                            Integer.toString(windowNum)));
            if (result == null) {
                LOGGER.info("Create statistics ranking, info : {}", this.toString());
                ScheduledExecutorUtil.execute(this::updateRanking, updatePeriod);
                return;
            }
            LOGGER.info("Use existed statistics ranking.");
            if (result instanceof ArrayList) {
                ArrayList<String> list = (ArrayList<String>) result;
                Map<String, String> map = new HashMap<>();
                for (int i = 0; i < list.size(); i += 2) {
                    map.put(list.get(i), list.get(i + 1));
                }
                if (isSame(map)) {
                    LOGGER.info("Use existed statistics ranking, info : {}", this.toString());
                } else {
                    LOGGER.error("Create statistics ranking failed, cause existed a same name statistics ranking which params is not same to this! exist info : {}, current info : {}", map.toString(), this.toString());
                    throw new IllegalStateException("Existing a same name statistics ranking which params is not same to this");
                }
            }
        }
    }

    private boolean isSame(Map<String, String> map) {
        return Integer.toString(maxRankingSize).equals(map.get("maxRankingSize")) && Integer.toString(rankingTime).equals(map.get("rankingTime")) &&
                Integer.toString(maxWindowSize).equals(map.get("maxWindowSize")) && Integer.toString(windowNum).equals(map.get("windowNum"));
    }

    private void updateRanking() {
        StringBuilder reduce = new StringBuilder();
        reduce.append("redis.call('zunionstore', KEYS[1], ARGV[1], ");
        for (int i = 0; i < windowNum; i++) {
            reduce.append("'" + name + "_" + i + "'");
            if (i == windowNum - 1) {
                reduce.append(");\n");
            } else {
                reduce.append(", ");
            }
        }
        String shorten = "local size = redis.call('zcard', KEYS[1]);\n" +
                "if(size > tonumber(ARGV[2])) then\n" +
                "redis.call('zremrangebyrank', KEYS[1], 0, size - ARGV[2] - 1);\n" +
                "end ";

        reduce.append(shorten);
        try (Jedis jedis = JedisUtil.getJedis()) {
            jedis.eval(reduce.toString(), Arrays.asList(name + RANKING), Arrays.asList(Integer.toString(windowNum), Integer.toString(maxRankingSize)));
            LOGGER.info("Reduce statistic info into {}", name + RANKING);
        }
    }

    @Override
    public long increaseScore(String id, int score) {
        String script =
                "local key1 = KEYS[1];\n" +
                        "local key2 = KEYS[2];\n" +
                        "local arg1 = tonumber(ARGV[1]);\n" +
                        "local arg2 = tonumber(ARGV[2]);\n" +
                        "local arg3 = tonumber(ARGV[3]);\n" +
                        "local arg4 = ARGV[4];\n" +
                        "local arg5 = tonumber(ARGV[5]);\n" +
                        "local currentSecond = redis.call('time')[1];\n" +
                        "local timeId = math.floor(currentSecond / arg2);\n" +
                        "local idx = timeId % arg1;\n" +
                        "local windowName = key1..'_'..idx;\n" +
                        "local windowStartTime = tostring(currentSecond - currentSecond % arg2);\n" +
                        "if (redis.call('hexists', key2, idx) == 0) then\n" +
                        "redis.call('hset', key2, idx, windowStartTime);\n" +
                        "redis.call('zincrby', windowName, arg5, arg4);\n" +
                        "return 0; \n" +
                        "else\n" +
                        "   local lastTime = redis.call('hget', key2, idx);\n" +
                        "   if(windowStartTime == lastTime) then\n" +
                        "   redis.call('zincrby', windowName, arg5, arg4);\n" +
                        "   local size = redis.call('zcard', windowName);\n" +
                        "       if(size > arg3) then\n" +
                        "       redis.call('zremrangebyrank', windowName, 0, size - arg3 - 1);\n" +
                        "       return 3;\n" +
                        "       end;\n" +
                        "   return 2;\n" +
                        "   else\n" +
                        "   redis.call('hset', key2, idx, windowStartTime);\n" +
                        "   redis.call('del', windowName);\n" +
                        "   redis.call('zincrby', windowName, arg5, arg4);\n" +
                        "   end;\n" +
                        "   return 4;\n" +
                        "end;";
        try (Jedis jedis = JedisUtil.getJedis()) {
            return (long) jedis.eval(script, Arrays.asList(name, name + METADATA), Arrays.asList(Integer.toString(windowNum),
                    Integer.toString(intervalInSecond), Integer.toString(maxWindowSize), id, Integer.toString(score)));
        }
    }

    @Override
    public long decreaseScore(String id, int score) {
        return increaseScore(id, -score);
    }

    @Override
    public List<RankingObj> getRanking(int start, int end) {
        try (Jedis jedis = JedisUtil.getJedis()) {
            List<Tuple> ranking = jedis.zrevrangeWithScores(name + RANKING, start, end);
            if (ranking == null || ranking.size() == 0) {
                return Collections.EMPTY_LIST;
            }

            return ranking.stream().map(tuple -> new RankingObj(tuple.getElement(), Math.round(tuple.getScore()))).collect(Collectors.toList());
        }
    }
}
