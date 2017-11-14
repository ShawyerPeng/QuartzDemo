package basic.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

/**
 * 需要执行的任务
 */
public class MyJob implements Job {
    /**
     * 把要执行的操作，写在 execute 方法中
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("测试 Quartz " + new Date());
    }
}