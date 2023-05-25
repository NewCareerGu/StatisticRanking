package org.ranking.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务工具类
 *
 * @author Gu
 * @since 2023-05-24
 */
public class ScheduledExecutorUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledExecutorUtil.class);

    private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(Integer.parseInt(PropUtil.getProperty("scheduled.thread.num", "2")));

    private ScheduledExecutorUtil() {
    }

    public static void execute(Runnable runnable, int period) {
        executor.scheduleAtFixedRate(() -> {
                    LOGGER.info("Start reduce task.");
                    runnable.run();
                    LOGGER.info("End reduce task.");
                }
                , 1, period, TimeUnit.SECONDS);
    }
}
