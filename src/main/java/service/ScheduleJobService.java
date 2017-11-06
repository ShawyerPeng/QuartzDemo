package service;

import model.ScheduleJob;

import java.util.List;

/**
 * 定时任务服务
 */
public interface ScheduleJobService {
    /**
     * 初始化定时任务
     */
    void initScheduleJob();

    /**
     * 新增
     */
    Integer insert(ScheduleJob scheduleJob);

    /**
     * 直接修改 只能修改运行的时间，参数、同异步等无法修改
     */
    Integer update(ScheduleJob scheduleJob);

    /**
     * 删除重新创建方式
     */
    Integer delUpdate(ScheduleJob scheduleJob);

    /**
     * 删除
     */
    Integer delete(Integer jobId);

    /**
     * 运行一次任务
     */
    void runOnce(Integer jobId);

    /**
     * 暂停任务
     */
    void pauseJob(Integer jobId);

    /**
     * 恢复任务
     */
    void resumeJob(Integer jobId);

    /**
     * 获取任务对象
     */
    ScheduleJob get(Integer jobId);

    /**
     * 查询任务列表
     */
     List<ScheduleJob> queryList(ScheduleJob scheduleJob);

    /**
     * 获取运行中的任务列表
     */
    List<ScheduleJob> queryExecutingJobList();
}
