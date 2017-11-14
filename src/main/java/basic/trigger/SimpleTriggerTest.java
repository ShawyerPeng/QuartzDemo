package basic.trigger;

import basic.job.MyJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 调用任务的类
 * 允许设置开始时间，结束时间，重复间隔
 */
public class SimpleTriggerTest {
    private static Logger logger = LoggerFactory.getLogger(SimpleTriggerTest.class);

    public static void main(String[] args) {
        SchedulerFactory schedulerfactory = new StdSchedulerFactory();
        Scheduler scheduler = null;
        try {
            // 通过 SchedulerFactory 获取一个调度器实例
            scheduler = schedulerfactory.getScheduler();

            // 创建 JobDetail 实例，绑定 Job 实现类
            // 这里并不会马上创建一个HelloJob实例，实例创建是在scheduler安排任务触发执行时创建的
            // 这种机制也为后面使用Spring集成提供了便利
            String jobName = "JOB";
            String jobGroupName = "JOB_GROUP";
            JobDetail jobDetail = JobBuilder.newJob(MyJob.class).withIdentity(jobName, jobGroupName).build();

            // Trigger名称、Trigger Group名称
            String triggerName = "JOB_TRIGGER";
            String triggerGroupName = "TRIGGRT_GROUP";
            // 触发器Builder
            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
            // 触发器名,触发器组
            triggerBuilder.withIdentity(triggerName, triggerGroupName);
            // 触发器时间设定：立即执行且每40秒重复执行一次
            triggerBuilder.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(40).repeatForever());
            // 创建一个 SimpleTrigger 实例，指定该 Trigger 在 Scheduler 中所属组及名称。接着设置调度的时间规则为当前时间运行
            SimpleTrigger simpleTrigger = (SimpleTrigger) triggerBuilder.startNow().build();

            // 把作业和触发器注册到任务调度中/注册并进行调度
            scheduler.scheduleJob(jobDetail, simpleTrigger);

            // 启动调度器
            scheduler.start();

            try {
                // 当前线程等待 60 秒
                Thread.sleep(60L * 1000L);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 关闭任务调度程序，如果不关闭，调度程序schedule会一直运行着
            scheduler.shutdown(true);

            logger.error("结束运行...");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}