package utils;

import model.ScheduleJob;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import quartz.AsyncJobFactory;
import quartz.SyncJobFactory;
import dto.ScheduleJobDto;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 定时任务操作辅助类
 */
public class ScheduleUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ScheduleUtils.class);

    //Trigger 各种状态：
    //None：Trigger 已经完成，且不会在执行，或者找不到该触发器，或者 Trigger 已经被删除
    //NORMAL: 正常状态
    //PAUSED：暂停状态
    //COMPLETE：触发器完成，但是任务可能还正在执行中
    //BLOCKED：线程阻塞状态
    //ERROR：出现错误

    /**
     * 获取触发器key
     */
    public static TriggerKey getTriggerKey(String jobName, String jobGroup) {
        return TriggerKey.triggerKey(jobName, jobGroup);
    }

    /**
     * 获取表达式触发器
     */
    public static CronTrigger getCronTrigger(Scheduler scheduler, String jobName, String jobGroup) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
            return (CronTrigger) scheduler.getTrigger(triggerKey);
        } catch (SchedulerException e) {
            LOG.error("获取定时任务CronTrigger出现异常", e);
        }
        return null;
    }

    /**
     * 创建任务
     */
    public static void createScheduleJob(Scheduler scheduler, ScheduleJob scheduleJob) {
        createScheduleJob(scheduler, scheduleJob.getJobName(), scheduleJob.getJobGroup(),
                scheduleJob.getCronExpression(), scheduleJob.getIsSync(), scheduleJob);
    }

    /**
     * 创建定时任务
     */
    private static void createScheduleJob(Scheduler scheduler, String jobName, String jobGroup,
                                          String cronExpression, Boolean isSync, Object param) {
        // 同步或异步
        Class<? extends Job> jobClass = isSync ? SyncJobFactory.class : AsyncJobFactory.class;

        // 构建job信息
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroup).build();

        // 表达式调度构建器
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

        // 按新的cronExpression表达式构建一个新的trigger
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup).withSchedule(scheduleBuilder).build();

        String jobTrigger = trigger.getKey().getName();

        ScheduleJob scheduleJob = (ScheduleJob) param;
        scheduleJob.setJobTrigger(jobTrigger);

        // 放入参数，运行时的方法可以获取
        jobDetail.getJobDataMap().put(ScheduleJobDto.JOB_PARAM_KEY, scheduleJob);

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            LOG.error("创建定时任务失败", e);
        }
    }

    /**
     * 运行一次任务
     */
    public static void runOnce(Scheduler scheduler, String jobName, String jobGroup) {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        try {
            scheduler.triggerJob(jobKey);
        } catch (SchedulerException e) {
            LOG.error("运行一次定时任务失败", e);
        }
    }

    /**
     * 暂停任务
     */
    public static void pauseJob(Scheduler scheduler, String jobName, String jobGroup) {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        try {
            scheduler.pauseJob(jobKey);
        } catch (SchedulerException e) {
            LOG.error("暂停定时任务失败", e);
        }
    }

    /**
     * 批量暂停任务计划
     */
    public static void pauseJob(Scheduler scheduler, GroupMatcher<JobKey> groupMatcher) throws SchedulerException {
        // GroupMatcher 创建示例，groupContains 即任务分组名称中包含指定字符的任务计划全部暂停
        // GroupMatcher<JobKey> groupMatcher = GroupMatcher.groupContains("xxxx");
        scheduler.pauseJobs(groupMatcher);
    }

    /**
     * 暂停所有任务
     */
    public static void pauseAllJob(Scheduler scheduler) throws SchedulerException {
        scheduler.pauseAll();
    }

    /**
     * 恢复任务
     */
    public static void resumeJob(Scheduler scheduler, String jobName, String jobGroup) {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        try {
            scheduler.resumeJob(jobKey);
        } catch (SchedulerException e) {
            LOG.error("暂停定时任务失败", e);
        }
    }

    /**
     * 恢复所有任务
     */
    public static void resumeAllJob(Scheduler scheduler) throws SchedulerException {
        scheduler.resumeAll();
    }

    /**
     * 获取jobKey
     */
    public static JobKey getJobKey(String jobName, String jobGroup) {
        return JobKey.jobKey(jobName, jobGroup);
    }

    /**
     * 更新定时任务
     */
    public static void updateScheduleJob(Scheduler scheduler, ScheduleJob scheduleJob) {
        updateScheduleJob(scheduler, scheduleJob.getJobName(), scheduleJob.getJobGroup(),
                scheduleJob.getCronExpression(), scheduleJob.getIsSync(), scheduleJob);
    }

    /**
     * 更新定时任务
     */
    private static void updateScheduleJob(Scheduler scheduler, String jobName, String jobGroup,
                                          String cronExpression, Boolean isSync, Object param) {
        // 同步或异步
        Class<? extends Job> jobClass = isSync ? SyncJobFactory.class : AsyncJobFactory.class;

        try {
//            JobDetail jobDetail = scheduler.getJobDetail(getJobKey(jobName, jobGroup));
//            jobDetail = jobDetail.getJobBuilder().ofType(jobClass).build();

            // 更新参数 实际测试中发现无法更新
//            JobDataMap jobDataMap = jobDetail.getJobDataMap();
//            jobDataMap.put(ScheduleJobVo.JOB_PARAM_KEY, param);
//            jobDetail.getJobBuilder().usingJobData(jobDataMap);

            TriggerKey triggerKey = ScheduleUtils.getTriggerKey(jobName, jobGroup);

            // 表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

            // 按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

            Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
            // 忽略状态为PAUSED的任务，解决集群环境中在其他机器设置定时任务为PAUSED状态后，集群环境启动另一台主机时定时任务全被唤醒的bug
            if (!triggerState.name().equalsIgnoreCase("PAUSED")) {
                // 按新的trigger重新设置job执行
                scheduler.rescheduleJob(triggerKey, trigger);
            }
            // 更新数据库中的任务
        } catch (SchedulerException e) {
            LOG.error("更新定时任务失败", e);
        }
    }

    /**
     * 删除定时任务
     */
    public static void deleteScheduleJob(Scheduler scheduler, String jobName, String jobGroup) {
        try {
            scheduler.deleteJob(getJobKey(jobName, jobGroup));
        } catch (SchedulerException e) {
            LOG.error("删除定时任务失败", e);
        }
    }

    /**
     * 批量删除定时任务
     */
    public static void deleteScheduleJobs(Scheduler scheduler, List<ScheduleJob> scheduleJobList) throws SchedulerException {
        if (scheduleJobList == null || scheduleJobList.size() <= 0) {
            return;
        }
        List<JobKey> jobKeyList = new ArrayList<>();
        for (ScheduleJob scheduleJob : scheduleJobList) {
            String jobName = scheduleJob.getJobName();
            String jobGroup = scheduleJob.getJobGroup();
            JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
            jobKeyList.add(jobKey);
        }
        scheduler.deleteJobs(jobKeyList);
    }

    /**
     * 取消任务
     */
    public static void cancleJob(Scheduler scheduler, String taskScheduleName, String taskGroupName) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(taskScheduleName, taskGroupName);
        scheduler.unscheduleJob(triggerKey);
    }

    /**
     * 批量取消任务
     */
    public static void cancleJobs(Scheduler scheduler, List<ScheduleJob> scheduleJobList) throws SchedulerException {
        if (scheduleJobList == null || scheduleJobList.size() <= 0) {
            return;
        }
        List<TriggerKey> triggerKeyList = new ArrayList<>();
        for (ScheduleJob scheduleJob : scheduleJobList) {
            String jobName = scheduleJob.getJobName();
            String jobGroup = scheduleJob.getJobGroup();
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
            triggerKeyList.add(triggerKey);
        }
        scheduler.unscheduleJobs(triggerKeyList);
    }

    /**
     * 检测任务计划是否存在
     */
    public static boolean checkJobExist(Scheduler scheduler, String taskScheduleName, String taskGroupName) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(taskScheduleName, taskGroupName);
        return scheduler.checkExists(jobKey);
    }

    /**
     * 返回任务计划的运行状态
     */
    public static int getJobState(Scheduler scheduler, String taskScheduleName, String taskGroupName) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(taskScheduleName, taskGroupName);
        return scheduler.getTriggerState(triggerKey).ordinal();
    }

    /**
     * 获取单个任务
     */
    public ScheduleJob getJob(Scheduler scheduler, String jobName, String jobGroup) throws SchedulerException {
        ScheduleJob scheduleJob = null;
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        if (null != trigger) {
            scheduleJob = new ScheduleJob();
            scheduleJob.setJobName(jobName);
            scheduleJob.setJobGroup(jobGroup);
            scheduleJob.setJobDesc("触发器:" + trigger.getKey());
            //scheduleJob.setNextTime(trigger.getNextFireTime()); //下次触发时间
            //scheduleJob.setPreviousTime(trigger.getPreviousFireTime());//上次触发时间
            Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
            scheduleJob.setJobStatus(triggerState.name());
            if (trigger instanceof CronTrigger) {
                CronTrigger cronTrigger = (CronTrigger) trigger;
                String cronExpression = cronTrigger.getCronExpression();
                scheduleJob.setCronExpression(cronExpression);
            }
        }
        return scheduleJob;
    }

    /**
     * 获取所有任务
     */
    public List<ScheduleJob> getAllJobs(Scheduler scheduler) throws SchedulerException {
        GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
        Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
        List<ScheduleJob> jobList = new ArrayList<>();
        for (JobKey jobKey : jobKeys) {
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
            for (Trigger trigger : triggers) {
                ScheduleJob job = new ScheduleJob();
                job.setJobName(jobKey.getName());
                job.setJobGroup(jobKey.getGroup());
                job.setJobDesc("触发器:" + trigger.getKey());
                //job.setNextTime(trigger.getNextFireTime()); //下次触发时间
                //job.setPreviousTime(trigger.getPreviousFireTime());//上次触发时间
                //触发器当前状态
                Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                job.setJobStatus(triggerState.name());
                if (trigger instanceof CronTrigger) {
                    CronTrigger cronTrigger = (CronTrigger) trigger;
                    String cronExpression = cronTrigger.getCronExpression();
                    job.setCronExpression(cronExpression);
                }
                jobList.add(job);
            }
        }
        return jobList;
    }

    /**
     * 所有正在运行的job
     */
    public List<ScheduleJob> getRunningJob(Scheduler scheduler) throws SchedulerException {
        List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
        List<ScheduleJob> jobList = new ArrayList<>(executingJobs.size());
        for (JobExecutionContext executingJob : executingJobs) {
            ScheduleJob job = new ScheduleJob();
            JobDetail jobDetail = executingJob.getJobDetail();
            JobKey jobKey = jobDetail.getKey();
            Trigger trigger = executingJob.getTrigger();
            job.setJobName(jobKey.getName());
            job.setJobGroup(jobKey.getGroup());
            job.setJobDesc("触发器:" + trigger.getKey());
            Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
            job.setJobStatus(triggerState.name());
            if (trigger instanceof CronTrigger) {
                CronTrigger cronTrigger = (CronTrigger) trigger;
                String cronExpression = cronTrigger.getCronExpression();
                job.setCronExpression(cronExpression);
            }
            jobList.add(job);
        }
        return jobList;
    }

    /**
     * 更新任务时间表达式
     */
    public void updateCronExpression(Scheduler scheduler, ScheduleJob scheduleJob) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
        // 获取trigger，即在spring配置文件中定义的 bean id="myTrigger"
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        // 表达式调度构建器
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression());
        // 按新的cronExpression表达式重新构建trigger
        trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
        // 按新的trigger重新设置job执行
        scheduler.rescheduleJob(triggerKey, trigger);
    }

    /**
     * 判断cron时间表达式正确性
     */
    public static boolean isValidExpression(final String cronExpression) throws ParseException {
        //CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
        CronTriggerImpl trigger = new CronTriggerImpl();
        try {
            trigger.setCronExpression(cronExpression);
            Date date = trigger.computeFirstFireTime(null);
            return date != null && date.after(new Date());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 立即执行一个任务
     */
    public void executeJob(Scheduler scheduler, ScheduleJob scheduleJob) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
        scheduler.triggerJob(jobKey);
    }

    public static void main(String[] args) throws ParseException {
        System.out.println(isValidExpression("0 0/1 * * * ?"));
    }

    /**
     * 任务运行状态
     */
    public enum TASK_STATE {
        NONE("NONE", "未知"),
        NORMAL("NORMAL", "正常运行"),
        PAUSED("PAUSED", "暂停状态"),
        COMPLETE("COMPLETE", ""),
        ERROR("ERROR", "错误状态"),
        BLOCKED("BLOCKED", "锁定状态");

        private String index;
        private String name;

        TASK_STATE(String index, String name) {
            this.name = name;
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public String getIndex() {
            return index;
        }
    }
}
