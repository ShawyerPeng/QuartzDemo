package basic.trigger;

import basic.job.MyJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * 允许 UNIX cron 表达式来指定日期和时间来运行作业
 */
public class CronTriggerTest {
    public static void main(String[] args) {
        // 通过 schedulerFactory 获取一个调度器
        SchedulerFactory schedulerfactory = new StdSchedulerFactory();
        Scheduler scheduler = null;
        try {
            // 通过 schedulerFactory 获取一个调度器
            scheduler = schedulerfactory.getScheduler();

            // 创建 jobDetail 实例，绑定 Job 实现类
            String jobName = "";
            String jobGroupName = "";
            JobDetail jobDetail = JobBuilder.newJob(MyJob.class).withIdentity(jobName, jobGroupName).build();

            String triggerName = "";
            String triggerGroupName = "";
            String cron = "";
            // 触发器
            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
            // 触发器名,触发器组
            triggerBuilder.withIdentity(triggerName, triggerGroupName);
            triggerBuilder.startNow();
            // 触发器时间设定
            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
            // 创建Trigger对象
            CronTrigger cronTrigger = (CronTrigger) triggerBuilder.build();
            // 定义调度触发规则

            // 把作业和触发器注册到任务调度中
            scheduler.scheduleJob(jobDetail, cronTrigger);

            // 启动调度
            scheduler.start();

            scheduler.shutdown();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
