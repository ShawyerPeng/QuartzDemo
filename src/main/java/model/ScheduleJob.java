package model;

import java.io.Serializable;
import java.util.Date;

public class ScheduleJob implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 任务调度的参数key
     */
    public static final String JOB_PARAM_KEY = "jobParam";

    /**
     * 任务ID
     */
    private Integer jobId;
    /**
     * 任务名称
     */
    private String jobName;
    /**
     * 任务分组
     */
    private String jobGroup;
    /**
     * 任务别名
     */
    private String jobAlias;
    /**
     * 任务描述
     */
    private String jobDesc;
    /**
     * 任务触发器
     */
    private String jobTrigger;
    /**
     * 任务状态（0:禁用 1:启用 2:删除）
     */
    private String jobStatus;
    /**
     * 任务运行时间表达式
     */
    private String cronExpression;
    /**
     * 任务执行URL
     */
    private String url;
    /**
     * 是否同步（0:异步，1:同步）
     */
    private Boolean isSync;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 上一次运行时间
     */
    private Date prevTime;
    /**
     * 下一次运行时间
     */
    private Date nextTime;

    public ScheduleJob() {
    }

    public ScheduleJob(String jobName, String jobGroup) {
        this.jobName = jobName;
        this.jobGroup = jobGroup;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName == null ? null : jobName.trim();
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup == null ? null : jobGroup.trim();
    }

    public String getJobAlias() {
        return jobAlias;
    }

    public void setJobAlias(String jobAlias) {
        this.jobAlias = jobAlias == null ? null : jobAlias.trim();
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc == null ? null : jobDesc.trim();
    }

    public String getJobTrigger() {
        return jobTrigger;
    }

    public void setJobTrigger(String jobTrigger) {
        this.jobTrigger = jobTrigger == null ? null : jobTrigger.trim();
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus == null ? null : jobStatus.trim();
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression == null ? null : cronExpression.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public Boolean getIsSync() {
        return isSync;
    }

    public void setIsSync(Boolean isSync) {
        this.isSync = isSync;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getPrevTime() {
        return prevTime;
    }

    public void setPrevTime(Date prevTime) {
        this.prevTime = prevTime;
    }

    public Date getNextTime() {
        return nextTime;
    }

    public void setNextTime(Date nextTime) {
        this.nextTime = nextTime;
    }

    @Override
    public String toString() {
        return "ScheduleJob{" +
                "jobId=" + jobId +
                ", jobName='" + jobName + '\'' +
                ", jobGroup='" + jobGroup + '\'' +
                ", jobAlias='" + jobAlias + '\'' +
                ", jobDesc='" + jobDesc + '\'' +
                ", jobTrigger='" + jobTrigger + '\'' +
                ", jobStatus='" + jobStatus + '\'' +
                ", cronExpression='" + cronExpression + '\'' +
                ", url='" + url + '\'' +
                ", isSync=" + isSync +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}