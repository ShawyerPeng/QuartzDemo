package basic.listener;

import basic.job.MyJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.KeyMatcher;

/**
 * JobListener实现，对 Job 建立一个监听器，分别对任务执行《之前，之后，取消》3 个阶段进行监听
 * 实现监听器需要实现 JobListener 接口，然后注册到 Scheduler 上就可以了
 * JobListener方法执行顺序：
 * 1. 如果TriigerListener的vetoJobExecution返回true
 * triggerFired -> vetoJobExecution ->jobExecutionVetoed
 * 2. 如果TiggerListener的vetoJobExecution返回false
 * triggerFired -> vetoJobExecution ->jobToBeExecuted -> [Job execute] -> jobWasExecuted ->[triggerMisfired|triggerComplete]
 */
public class MyJobListener implements JobListener {
    @Override
    public String getName() {
        return "MyJobListener";
    }

    /**
     * (1)任务执行之前执行
     */
    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        System.out.println("jobToBeExecuted:" + context.getJobDetail().getDescription());
    }

    /**
     * (2)这个方法正常情况下不执行，但是如果当TriggerListener中的vetoJobExecution方法返回true时，那么执行这个方法
     * 需要注意的是：如果方法(2)执行，那么(1)(3)这两个方法不会执行，因为任务被终止了
     */
    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        System.out.println("jobExecutionVetoed:" + context.getJobDetail().getDescription());
    }

    /**
     * (3)任务执行完成后执行，jobException如果它不为空则说明任务在执行过程中出现了异常
     */
    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException e) {
        System.out.println("jobWasExecuted:" + context.getJobDetail().getDescription());
    }

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

            // 将 Listener 注册到 Scheduler
            // 1. 全局注册,所有Job都会起作用
            scheduler.getListenerManager().addJobListener(new MyJobListener());
            // 2. 指定具体的任务
            Matcher<JobKey> matcher = KeyMatcher.keyEquals(new JobKey("job1", "group1"));
            scheduler.getListenerManager().addJobListener(new MyJobListener(), matcher);
            // 3. 指定一组任务
            GroupMatcher<JobKey> groupMatcher = GroupMatcher.jobGroupEquals("group1");
            scheduler.getListenerManager().addJobListener(new MyJobListener(), groupMatcher);
            // 4. 可以根据组的名字匹配开头和结尾或包含
            GroupMatcher<JobKey> startMatcher = GroupMatcher.groupStartsWith("g");
            GroupMatcher<JobKey> containsMatcher = GroupMatcher.groupContains("g");
            scheduler.getListenerManager().addJobListener(new MyJobListener(), startMatcher);
            scheduler.getListenerManager().addJobListener(new MyJobListener(), containsMatcher);

            // 启动调度器
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}