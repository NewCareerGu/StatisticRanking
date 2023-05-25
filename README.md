# StatisticRanking
欢迎来到我们的实时统计排行榜！基于最新数据的排行榜，让您时刻了解最热门和最具活力的内容。我们的排行榜基于实时统计，使用滑动窗口算法，可以支持分布式上报数据，确保您获得准确、实时的排名信息。

## 特点
* 实时统计：
跟据当前时间，你可以获取最近一小时，一天或者自定义时间内的实时排行统。

* 使用滑动窗口算法：
通过设置窗口大小和滑动步长，我们能够灵活地调整统计的时间范围，从而捕捉到最近的数据变化。这意味着您可以轻松追踪过去的一小时、一天甚至一周内的热门内容，及时发现趋势和变化。窗口分片越小统计越准确，同时性能消耗也越大。

* 分布式统计：
可以分布式多台节点上报统计数据，汇总redis排行榜信息。

## 分布式模式
基于redis作为排行榜中心，多个节都可以上报排行信息
```
创建一个名为 `HelloWorld` 的排行榜,排行榜长度最大10，统计窗口10s，滑动窗口数量10，滑动窗口长度10，更新汇总排行信息间隔1s
Ranking statistic = new RedisRanking("HelloWorld", 10, new Time(10, TimeUnit.SECONDS), 10, 10, new Time(1, TimeUnit.SECONDS));
为news1增加一次点击事件
statistic.increaseScore("news1", 1);
获取排行榜信息
List<RankingObj> ranking = statistic.getRanking();

redis生成数据解释：会生成存放排行榜元素数据信息及小窗口们，根据当前时间取模将统计信息放到对于的小窗口队列中，通过定时任务将小窗口们的数据汇总到排行榜。
127.0.0.1:6379> keys *
1."HelloWorld_metadata" // 存放元数据及窗口对应起始时间
2."HelloWorld_5"        // 每个小窗口（zset)
3."HelloWorld_1"
4."HelloWorld_0"
5."HelloWorld_3"
6."HelloWorld_8"
7."HelloWorld_ranking"  // 排行榜信息（zset)，由小窗口统计汇总到排行榜
8."HelloWorld_9"
9."HelloWorld_6"
10."HelloWorld_2"
11."HelloWorld_7"
12."HelloWorld_4"
```
## 单机模式（TODO）