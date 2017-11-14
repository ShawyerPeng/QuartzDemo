package basic;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * JobDataMap 是 JAVA Map 接口的一个实现，并添加了一些实现用于方便地存取基本类型数据。
 * 它能够存储任意规模的，你想要由 Job 在执行时使用的数据
 */
public class JobDataMapTest {
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
            // 在定义 JobDetail 时为 JobDataMap 添加数据
            JobDetail jobDetail = JobBuilder.newJob(DumbJob.class)
                    .withIdentity(jobName, jobGroupName)
                    .usingJobData("jobSays", "Hello World!")
                    .usingJobData("myFloatValue", 3.141f)
                    .build();

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
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public static class DumbJob implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            // 任务执行时从 JobDataMap 获取数据
            JobKey key = context.getJobDetail().getKey();
            JobDataMap dataMap = context.getJobDetail().getJobDataMap();
            String jobSays = dataMap.getString("jobSays");
            float myFloatValue = dataMap.getFloat("myFloatValue");
            System.err.println("Instance " + key + " of DumbJob says: " + jobSays + ", and val is: " + myFloatValue);
        }
    }
}