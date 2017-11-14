package basic.listener;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;

/**
 * 调度器监听器TriggerListener实现，
 */
public class MyTriggerListener implements TriggerListener {
    @Override
    public String getName() {
        return "MyTriggerListener";
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext jobExecutionContext) {
        System.out.println("triggerFired:" + trigger.getDescription());
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext jobExecutionContext) {
        System.out.println("vetoJobExecution:" + trigger.getDescription());
        return false;

        /**
         return:false
         triggerFired:MyTrigger
         vetoJobExecution:MyTrigger
         jobToBeExecuted:MyJobDetail
         MyJob secondriver.quartz.TestJobListener.MyJob
         jobWasExecuted:MyJobDetail
         triggerComplete:MyTrigger
         */
        /**
         return:true
         triggerFired:MyTrigger
         vetoJobExecution:MyTrigger
         jobExecutionVetoed:MyJobDetail
         */
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
        System.out.println("triggerMisfired:" + trigger.getDescription());
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext jobExecutionContext, Trigger.CompletedExecutionInstruction completedExecutionInstruction) {
        System.out.println("triggerComplete:" + trigger.getDescription());
    }
}