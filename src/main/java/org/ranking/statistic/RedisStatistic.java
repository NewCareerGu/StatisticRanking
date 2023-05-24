package org.ranking.statistic;

import org.ranking.model.RankingObj;
import org.ranking.model.TimeUnit;
import org.ranking.util.JedisUtil;
import org.ranking.util.ScheduledExecutorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;

public class RedisStatistic extends AbstractStatistic {
    private static final Logger LOGGER = LoggerFactory.getLogger(JedisUtil.class);
    private static final String METADATA = "_metadata";

    private static final String RANKING = "_ranking";

    public RedisStatistic(String name, int maxRankingSize, TimeUnit rankingTime, int windowNum) {
        super(name, maxRankingSize, rankingTime, windowNum);
        init(name, windowNum);
    }

    public RedisStatistic(String name, int maxRankingSize, TimeUnit rankingTime, int maxWindowSize, int windowNum) {
        super(name, maxRankingSize, rankingTime, maxWindowSize, windowNum);
        init(name, windowNum);
    }

    private void init(String name, int windowNum) {
        String script = "if (redis.call('exists',KEYS[1]) == 0) then " +
                "local currentSecond = redis.call('time')[1]; " +
                "redis.call('hset', KEYS[1], 'startTime', currentSecond); " +
                "redis.call('hset', KEYS[1], 'maxRankingSize', ARGV[1]); " +
                "redis.call('hset', KEYS[1], 'rankingTime', ARGV[2]); " +
                "redis.call('hset', KEYS[1], 'maxWindowSize', ARGV[3]); " +
                "redis.call('hset', KEYS[1], 'intervalInSecond', ARGV[4]); " +
                "redis.call('hset', KEYS[1], 'windowNum', ARGV[5]); " +
                "return nil; " +
                "else return redis.call('hgetall', KEYS[1]);" +
                "end; ";

        try (Jedis jedis = JedisUtil.getJedis()) {
            Object result = jedis.eval(script, Arrays.asList(name + METADATA),
                    Arrays.asList(Integer.toString(maxRankingSize), Integer.toString(rankingTime),
                            Integer.toString(maxWindowSize), Integer.toString(intervalInSecond),
                            Integer.toString(windowNum)));
            if (result == null) {
                LOGGER.info("Create statistics ranking, info : {}", this.toString());
                ScheduledExecutorUtil.execute(this::reduceRanking, 10);
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

    private Runnable reduceRanking() {
        return () -> {
            StringBuilder reduce = new StringBuilder();
            reduce.append("redis.call('zunionstore', KEYS[1], ARGV[1], ");
            for (int i = 0; i < windowNum; i++) {
                reduce.append("\\'" + name + "_" + i + "\\'");
                if (i == windowNum - 1) {
                    reduce.append(");");
                } else {
                    reduce.append(", ");
                }
            }
            String shorten = "local size = redis.call('zcard', KEYS[1]); " +
                    "if(size > ARGV[2]) then " +
                    "redis.call('zremrangebyrank', KEYS[1], 0, size - ARGV[2] - 1); " +
                    "end";

            reduce.append(shorten);
            System.out.printf(reduce.toString());
            try (Jedis jedis = JedisUtil.getJedis()) {
                jedis.eval(reduce.toString(), Arrays.asList(name + RANKING), Arrays.asList(Integer.toString(windowNum), Integer.toString(maxRankingSize)));
            }
        };
    }

    public void reduceRanking2() {
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
        String shorten = "local size = redis.call('zcard', KEYS[1]);\n " +
                "if(size > tonumber(ARGV[2])) then\n " +
                "redis.call('zremrangebyrank', KEYS[1], 0, size - ARGV[2] - 1);\n " +
                "end";

        reduce.append(shorten);
        System.out.printf(reduce.toString());
        try (Jedis jedis = JedisUtil.getJedis()) {
            Object res = jedis.eval(reduce.toString(), Arrays.asList(name + RANKING), Arrays.asList(Integer.toString(windowNum), Integer.toString(maxRankingSize)));
            System.out.printf("");
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
            List<Tuple> ranking = jedis.zrevrangeByScoreWithScores(RANKING, start, end);
            System.out.printf("");
        }
        return null;
    }
}
