package controller;

import model.ScheduleJob;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import service.ScheduleJobService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/quartz")
public class ScheduleJobController {
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private ScheduleJobService scheduleJobService;

    /**
     * 任务页面
     */
    @RequestMapping(value = "/input", method = RequestMethod.GET)
    public String inputScheduleJob(ScheduleJob scheduleJob, ModelMap modelMap) {
        if (scheduleJob.getJobId() != null) {
            ScheduleJob job = scheduleJobService.get(scheduleJob.getJobId());
            modelMap.put("scheduleJob", job);
        }
        return "input-schedule-job";
    }

    /**
     * 任务创建与更新(未存在的就创建，已存在的则更新)
     */
    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String updateJob(HttpServletRequest request, HttpServletResponse response,
                            @RequestBody ScheduleJob job, ModelMap model) {
        try {
            if (null != job) {
                // 获取触发器标识
                TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
                // 获取触发器trigger
                CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
                if (null == trigger) {  // 不存在任务
                    scheduleJobService.insert(job);
                } else {
                    scheduleJobService.update(job);
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return "ok";
    }

    /**
     * 删除任务
     */
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String deleteScheduleJob(Integer scheduleJobId) {
        scheduleJobService.delete(scheduleJobId);
        return "redirect:list-schedule-job.shtml";
    }

    /**
     * 运行一次任务
     */
    @RequestMapping(value = "/runOnce", method = RequestMethod.GET)
    public String runOnceScheduleJob(Integer scheduleJobId) {
        scheduleJobService.runOnce(scheduleJobId);
        return "redirect:list-schedule-job.shtml";
    }

    /**
     * 暂停任务
     */
    @RequestMapping(value = "/pause", method = RequestMethod.GET)
    public String pauseScheduleJob(Integer scheduleJobId) {
        scheduleJobService.pauseJob(scheduleJobId);
        return "redirect:list-schedule-job.shtml";
    }

    /**
     * 恢复任务
     */
    @RequestMapping(value = "/resume", method = RequestMethod.GET)
    public String resumeScheduleJob(Integer scheduleJobId) {
        scheduleJobService.resumeJob(scheduleJobId);
        return "redirect:list-schedule-job.shtml";
    }

    /**
     * 保存任务
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveScheduleJob(ScheduleJob scheduleJob) {
        // 测试用随便设个状态
        scheduleJob.setJobStatus("1");

        if (scheduleJob.getJobId() == null) {
            scheduleJobService.insert(scheduleJob);
        } else if (StringUtils.equalsIgnoreCase("delUpdate", "delUpdate")) {
            // 直接拿keywords存一下，就不另外重新弄了
            scheduleJobService.delUpdate(scheduleJob);
        } else {
            scheduleJobService.update(scheduleJob);
        }
        return "redirect:list-schedule-job.shtml";
    }

    /**
     * 任务列表页
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String listScheduleJob(ScheduleJob scheduleJob, ModelMap modelMap) {
        List<ScheduleJob> scheduleJobVoList = scheduleJobService.queryList(scheduleJob);
        modelMap.put("scheduleJobVoList", scheduleJobVoList);

        List<ScheduleJob> executingJobList = scheduleJobService.queryExecutingJobList();
        modelMap.put("executingJobList", executingJobList);

        return "list-schedule-job";
    }
}
