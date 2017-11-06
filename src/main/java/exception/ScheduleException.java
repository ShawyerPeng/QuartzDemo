package exception;

/**
 * 定时任务自定义异常
 */
public class ScheduleException extends Throwable {
    private static final long serialVersionUID = 1L;

    private String message;

    public ScheduleException() {
    }

    public ScheduleException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
