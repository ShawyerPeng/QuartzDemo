# 简介
Quartz 是一个由 James House 创立的开源项目，是一个功能强大的作业调度工具，可以计划的执行任务，定时、循环或在某一个时间来执行我们需要做的事，这可以给我们工作上带来很大的帮助。例如，你的程序中需要每个月的一号导出报表、定时发送邮件或程序需要每隔一段执行某一任务…… 等等，都可以用 Quartz 来解决。

Quartz 大致可分为三个主要的核心：
1. 调度器 Scheduler: 是一个计划调度器容器，容器里面可以盛放众多的 JobDetail 和 Trigger，当容器启动后，里面的每个 JobDetail 都会根据 Trigger 按部就班自动去执行.
2. 任务 Job：要执行的具体内容。JobDetail: 具体的可执行的调度程序，包含了这个任务调度的方案和策略。
3. 触发器 Trigger：调度参数的配置，什么时候去执行调度。

可以这么理解它的原理：调度器就相当于一个容器，装载着任务和触发器。任务和触发器又是绑定在一起的，然而一个任务可以对应多个触发器，但一个触发器却只能对应一个任务。当 JobDetail 和 Trigger 在 scheduler 容器上注册后，形成了装配好的任务作业（JobDetail 和 Trigger 所组成的一对儿），就可以伴随容器启动而调度执行了。

# 基本概念
* Job：具体需要执行的任务
* JobDetail：该任务的信息，包括具体执行的任务以及其他一些相关的信息
* Trigger：触发器
* Scheduler：任务调度器
* Misfire：错过的，指本来应该被执行但是实际没有被执行的任务调度

实现一个最简单的 Quartz 定时任务（不支持多机），有几个步骤：
1. 创建 Job
2. 创建 JobDetailFactoryBean。顾名思义，可以用于生成 JobDetail
3. 创建 Trigger。作用：配置定时时间
4. 创建 SchedulerFactoryBean。作用：启动定时任务

# 注意
updateByPrimaryKey 对你注入的字段全部更新（不判断是否为 Null）
updateByPrimaryKeySelective 会对字段进行判断再更新 (如果为 Null 就忽略更新)

insert 就把所有值插入, 但是要注意加入数据库字段有 default,default 是不会起作用的
insertSelective 不会忽略 default，只会插入不为null的字段

# 实现 Demo
创建 Job：具体的执行任务
```java
public class MyJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Do the job!");
        try {
            log.info("job scheduler = {}.",context.getScheduler().getSchedulerName());
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        log.info("job trigger = {}.",context.getTrigger());
    }
}
```
创建 JobDetailFactoryBean，CronTriggerFactoryBean，SchedulerFactoryBean。
```java
public class SchedulerJob {
    private final static String CRON_EXPRESSION = "*/3 * * * * ?";

    @Bean
    public JobDetailFactoryBean createJobDetail(){
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();jobDetailFactoryBean.setName("myJobName");
        jobDetailFactoryBean.setGroup("myJobGroup");

        jobDetailFactoryBean.setJobClass(MyJob.class);

        return jobDetailFactoryBean;
    }

    @Bean
    protected CronTriggerFactoryBean createTrigger(JobDetail jobDetail) {
        CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setName("myTriggerName");
        cronTriggerFactoryBean.setGroup("myTriggerGroup");

        cronTriggerFactoryBean.setCronExpression(CRON_EXPRESSION);
        cronTriggerFactoryBean.setJobDetail(jobDetail);
        cronTriggerFactoryBean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY);

        return cronTriggerFactoryBean;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(List<Trigger> triggers){
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setSchedulerName("myScheduleName");

        schedulerFactoryBean.setTriggers(triggers.toArray(new Trigger[0]));

        return schedulerFactoryBean;
    }
}
```
以上便可实现一个单机的定时任务。

# Cron 表达式
这些星号由左到右按顺序代表 ：`* * * * * * *`  
格式：[秒] [分] [小时] [日] [月] [周] [年]

序号 | 说明 | 是否必填 | 允许填写的值 | 允许的通配符
--- | --- | --- | --- | ---
1 | 秒 | 是 | 0-59 | `, - * /`
2 | 分 | 是 | 0-59 | `, - * /`
3 | 小时 | 是 | 0-23 | `, - * /`
4 | 日 | 是 | 1-31 | `, - * ? / L W`
5 | 月 | 是 | 1-12 or JAN-DEC | `, - * /`
6 | 周 | 是 | 1-7 or SUN-SAT | `, - * ? / L #`
7 | 年 | 否 | empty 或 1970-2099 | `, - * /`


常用示例：
```
0 0 12 * * ? 每天 12 点触发 
0 15 10 ? * * 每天 10 点 15 分触发 
0 15 10 * * ? 每天 10 点 15 分触发 
0 15 10 * * ? * 每天 10 点 15 分触发 
0 15 10 * * ? 2005 2005 年每天 10 点 15 分触发 
0 * 14 * * ? 每天下午的 2 点到 2 点 59 分每分触发 
0 0/5 14 * * ? 每天下午的 2 点到 2 点 59 分 (整点开始，每隔 5 分触发) 
0 0/5 14,18 * * ? 每天下午的 2 点到 2 点 59 分 (整点开始，每隔 5 分触发) 
每天下午的 18 点到 18 点 59 分 (整点开始，每隔 5 分触发) 
0 0-5 14 * * ? 每天下午的 2 点到 2 点 05 分每分触发 
0 10,44 14 ? 3 WED     3 月分每周三下午的 2 点 10 分和 2 点 44 分触发 （特殊情况，在一个时间设置里，执行两次或                                                             两次以上的情况） 
0 59 2 ? * FRI    每周 5 凌晨 2 点 59 分触发； 
0 15 10 ? * MON-FRI 从周一到周五每天上午的 10 点 15 分触发 
0 15 10 15 * ? 每月 15 号上午 10 点 15 分触发 
0 15 10 L * ? 每月最后一天的 10 点 15 分触发 
0 15 10 ? * 6L 每月最后一周的星期五的 10 点 15 分触发 
0 15 10 ? * 6L 2002-2005 从 2002 年到 2005 年每月最后一周的星期五的 10 点 15 分触发 
0 15 10 ? * 6#3 每月的第三周的星期五开始触发 
0 0 12 1/5 * ? 每月的第一个中午开始每隔 5 天触发一次 
0 11 11 11 11 ? 每年的 11 月 11 号 11 点 11 分触发 (光棍节)
```

## 通配符说明
* `*`：表示所有值. 例如: 在分的字段上设置 "*", 表示每一分钟都会触发。 
* `?`：表示不指定值。使用的场景为不需要关心当前设置这个字段的值。例如: 要在每月的 10 号触发一个操作，但不关心是周几，所以需要周位置的那个字段设置为 "?" 具体设置为 0 0 0 10 * ? 
* `-`：表示区间。例如 在小时上设置 "10-12", 表示 10,11,12 点都会触发。 
* `,`：表示指定多个值，例如在周字段上设置 "MON,WED,FRI" 表示周一，周三和周五触发 
* `/`：用于递增触发。如在秒上面设置 "5/15" 表示从 5 秒开始，每增 15 秒触发 (5,20,35,50)。在月字段上设置'1/3'所示每月 1 号开始，每隔三天触发一次。 
* `L`：表示最后的意思。在日字段设置上，表示当月的最后一天 (依据当前月份，如果是二月还会依据是否是润年 [leap]), 在周字段上表示星期六，相当于 "7" 或 "SAT"。如果在 "L" 前加上数字，则表示该数据的最后一个。例如在周字段上设置 "6L" 这样的格式, 则表示 “本月最后一个星期五 " 
* `W`：表示离指定日期的最近那个工作日 (周一至周五). 例如在日字段上设置 "15W"，表示离每月 15 号最近的那个工作日触发。如果 15 号正好是周六，则找最近的周五 (14 号) 触发, 如果 15 号是周未，则找最近的下周一 (16 号) 触发. 如果 15 号正好在工作日 (周一至周五)，则就在该天触发。如果指定格式为 "1W", 它则表示每月 1 号往后最近的工作日触发。如果 1 号正是周六，则将在 3 号下周一触发。(注，"W" 前只能设置具体的数字, 不允许区间 "-"). 

# Trigger 的类型
* SimpleTrigger：用于实现每隔一定时间执行任务，以及重复多少次，如每 2 小时执行一次，重复执行 5 次。
* CronTirgger：类似于 LINUX 上的任务调度命令 crontab，即利用一个包含 7 个字段的表达式来表示时间调度方式
* DateIntervalTrigger：最适合调度类似每 N（1, 2, 3...）小时，每 N 天，每 N 周等的任务。
* NthIncludedDayTrigger：用于每隔一个周期的第几天调度任务，例如，每个月的第 3 天执行指定的任务。

除了上面提到的 4 种 Trigger，Quartz 中还定义了一个 Calendar 类（注意，是 org.quartz.Calendar）。这个 Calendar 与 Trigger 一起使用，但是它们的作用相反，它是用于排除任务不被执行的情况。例如，按照 Trigger 的规则在 10 月 1 号需要执行任务，但是 Calendar 指定了 10 月 1 号是节日（国庆），所以任务在这一天将不会被执行。通常来说，Calendar 用于排除节假日的任务调度，从而使任务只在工作日执行。

# 使用有状态（StatefulJob）还是无状态的任务（Job）
任务分为有状态和无状态两种。实现 Job 接口的任务缺省为无状态的。Quartz 中还有另外一个接口 StatefulJob。实现 StatefulJob 接口的任务为有状态的。

StatefulJob 接口已经不推荐使用了，换成了注解的方式，只需要给你实现的 Job 类加上注解 @DisallowConcurrentExecution 即可实现有状态

无状态任务一般指可以并发的任务，即任务之间是独立的，不会互相干扰。例如我们定义一个 trigger，每 2 分钟执行一次，但是某些情况下一个任务可能需要 3 分钟才能执行完，这样，在上一个任务还处在执行状态时，下一次触发时间已经到了。对于无状态任务，只要触发时间到了就会被执行，因为几个相同任务可以并发执行。但是对有状态任务来说，是不能并发执行的，同一时间只能有一个任务在执行。

# 与 Spring 整合
在 Spring 中使用 Quartz 有两种方式实现：第一种是任务类继承 QuartzJobBean，第二种则是在配置文件里定义任务类和要执行的方法，类和方法可以是普通类。
## 方式一

## 方式二



# 参考资料
[基于 Quartz 开发企业级任务调度应用](https://www.ibm.com/developerworks/cn/opensource/os-cn-quartz/)  
