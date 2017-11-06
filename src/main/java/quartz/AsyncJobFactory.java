package quartz;

import dto.ScheduleJobDto;
import model.ScheduleJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 异步任务工厂
 */
public class AsyncJobFactory extends QuartzJobBean {
    private static final Logger LOG = LoggerFactory.getLogger(AsyncJobFactory.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LOG.info("AsyncJobFactory execute");
        ScheduleJob scheduleJob = (ScheduleJob) context.getMergedJobDataMap().get(ScheduleJobDto.JOB_PARAM_KEY);
        System.out.println("jobName:" + scheduleJob.getJobName() + " " + scheduleJob);
    }
}
