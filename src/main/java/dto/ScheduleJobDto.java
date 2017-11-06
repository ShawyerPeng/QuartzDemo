package dto;

import java.io.Serializable;
import java.util.Date;

public class ScheduleJobDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 任务调度的参数key
     */
    public static final String JOB_PARAM_KEY = "jobParam";

    /**
     * 任务id
     */
    private Long scheduleJobId;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务别名
     */
    private String aliasName;

    /**
     * 任务分组
     */
    private String jobGroup;

    /**
     * 触发器
     */
    private String jobTrigger;

    /**
     * 任务状态
     */
    private String status;

    /**
     * 任务运行时间表达式
     */
    private String cronExpression;

    /**
     * 是否异步
     */
    private Boolean isSync;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModify;

    /**
     * 任务执行url
     */
    private String url;
}
