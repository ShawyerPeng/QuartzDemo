package service.impl;

import mapper.ScheduleJobMapper;
import model.ScheduleJob;
import org.springframework.util.CollectionUtils;
import utils.ScheduleUtils;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.ScheduleJobService;

import java.util.ArrayList;
import java.util.List;

/**
 * 定时任务服务实现
 */
@Service
public class ScheduleJobServiceImpl implements ScheduleJobService {
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private ScheduleJobMapper scheduleJobMapper;

    public void initScheduleJob() {
        List<ScheduleJob> scheduleJobList = scheduleJobMapper.selectAll();
        if (CollectionUtils.isEmpty(scheduleJobList)) {
            return;
        }
        for (ScheduleJob scheduleJob : scheduleJobList) {
            CronTrigger cronTrigger = ScheduleUtils.getCronTrigger(scheduler, scheduleJob.getJobName(), scheduleJob.getJobGroup());
            // 不存在，创建一个
            if (cronTrigger == null) {
                ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
            } else {
                // 已存在，那么更新相应的定时设置
                ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
            }
        }
    }

    public Integer insert(ScheduleJob scheduleJob) {
        ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
        return scheduleJobMapper.insertSelective(scheduleJob);
    }

    public Integer update(ScheduleJob scheduleJob) {
        ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
        return scheduleJobMapper.updateByPrimaryKeySelective(scheduleJob);
    }

    public Integer delUpdate(ScheduleJob scheduleJob) {
        // 先删除
        ScheduleUtils.deleteScheduleJob(scheduler, scheduleJob.getJobName(), scheduleJob.getJobGroup());
        // 再创建
        ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
        // 数据库直接更新即可
        return scheduleJobMapper.updateByPrimaryKeySelective(scheduleJob);
    }

    public Integer delete(Integer jobId) {
        ScheduleJob scheduleJob = scheduleJobMapper.selectByPrimaryKey(jobId);
        // 删除运行的任务
        ScheduleUtils.deleteScheduleJob(scheduler, scheduleJob.getJobName(), scheduleJob.getJobGroup());
        // 删除数据
        return scheduleJobMapper.deleteByPrimaryKey(jobId);
    }

    public void runOnce(Integer jobId) {
        ScheduleJob scheduleJob = scheduleJobMapper.selectByPrimaryKey(jobId);
        ScheduleUtils.runOnce(scheduler, scheduleJob.getJobName(), scheduleJob.getJobGroup());
    }

    public void pauseJob(Integer jobId) {
        ScheduleJob scheduleJob = scheduleJobMapper.selectByPrimaryKey(jobId);
        ScheduleUtils.pauseJob(scheduler, scheduleJob.getJobName(), scheduleJob.getJobGroup());
        // 演示数据库就不更新了
    }

    public void resumeJob(Integer jobId) {
        ScheduleJob scheduleJob = scheduleJobMapper.selectByPrimaryKey(jobId);
        ScheduleUtils.resumeJob(scheduler, scheduleJob.getJobName(), scheduleJob.getJobGroup());
        // 演示数据库就不更新了
    }

    public ScheduleJob get(Integer jobId) {
        return scheduleJobMapper.selectByPrimaryKey(jobId);
    }

    public List<ScheduleJob> queryList(ScheduleJob scheduleJob) {
        List<ScheduleJob> scheduleJobs = scheduleJobMapper.selectAll();
        try {
            for (ScheduleJob job : scheduleJobs) {
                JobKey jobKey = ScheduleUtils.getJobKey(job.getJobName(), job.getJobGroup());
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                if (CollectionUtils.isEmpty(triggers)) {
                    continue;
                }

                //这里一个任务可以有多个触发器， 但是我们一个任务对应一个触发器，所以只取第一个即可，清晰明了
                Trigger trigger = triggers.iterator().next();
                job.setJobTrigger(trigger.getKey().getName());

                Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                job.setJobStatus(triggerState.name());

                if (trigger instanceof CronTrigger) {
                    CronTrigger cronTrigger = (CronTrigger) trigger;
                    String cronExpression = cronTrigger.getCronExpression();
                    job.setCronExpression(cronExpression);
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return scheduleJobs;
    }

    /**
     * 获取运行中的job列表
     */
    public List<ScheduleJob> queryExecutingJobList() {
        try {
            // 存放结果集
            List<ScheduleJob> jobList = new ArrayList<>();

            // 获取scheduler中的JobGroupName
            for (String group : scheduler.getJobGroupNames()) {
                // 获取JobKey 循环遍历JobKey
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.<JobKey>groupEquals(group))) {
                    JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                    JobDataMap jobDataMap = jobDetail.getJobDataMap();
                    ScheduleJob scheduleJob = (ScheduleJob) jobDataMap.get(ScheduleJob.JOB_PARAM_KEY);

                    List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                    Trigger trigger = triggers.iterator().next();
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());

                    scheduleJob.setJobTrigger(trigger.getKey().getName());
                    scheduleJob.setJobStatus(triggerState.name());
                    if (trigger instanceof CronTrigger) {
                        CronTrigger cronTrigger = (CronTrigger) trigger;
                        String cronExpression = cronTrigger.getCronExpression();
                        scheduleJob.setCronExpression(cronExpression);
                    }
                    // 获取正常运行的任务列表
                    if (triggerState.name().equals("NORMAL")) {
                        jobList.add(scheduleJob);
                    }
                }
            }

            /** 非集群环境获取正在执行的任务列表 */
            /**
             List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
             List<ScheduleJobVo> jobList = new ArrayList<ScheduleJobVo>(executingJobs.size());
             for (JobExecutionContext executingJob : executingJobs) {
             ScheduleJobVo job = new ScheduleJobVo();
             JobDetail jobDetail = executingJob.getJobDetail();
             JobKey jobKey = jobDetail.getKey();
             Trigger trigger = executingJob.getTrigger();
             job.setJobName(jobKey.getName());
             job.setJobGroup(jobKey.getGroup());
             job.setJobTrigger(trigger.getKey().getName());
             Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
             job.setStatus(triggerState.name());
             if (trigger instanceof CronTrigger) {
             CronTrigger cronTrigger = (CronTrigger) trigger;
             String cronExpression = cronTrigger.getCronExpression();
             job.setCronExpression(cronExpression);
             }
             jobList.add(job);
             }*/

            return jobList;
        } catch (SchedulerException e) {
            return null;
        }
    }
}
