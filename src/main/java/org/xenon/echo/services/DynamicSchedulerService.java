package org.xenon.echo.services;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class DynamicSchedulerService {
    private final TaskScheduler taskScheduler;
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public DynamicSchedulerService(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public void scheduleOnce(String taskId, Runnable task, Instant runAt) {
        cancelTask(taskId);
        if (runAt.isAfter(Instant.now())) {
            ScheduledFuture<?> future = taskScheduler.schedule(task, runAt);
            scheduledTasks.put(taskId, future);
        }
    }

    public void scheduleCron(String taskId, Runnable task, String cronExpression) {
        cancelTask(taskId);
        ScheduledFuture<?> future = taskScheduler.schedule(task, new CronTrigger(cronExpression));
        scheduledTasks.put(taskId, future);
    }

    public void scheduleFixedRate(String taskId, Runnable task, long fixedRateMs) {
        cancelTask(taskId);
        ScheduledFuture<?> future = taskScheduler.scheduleAtFixedRate(task, fixedRateMs);
        scheduledTasks.put(taskId, future);
    }

    public void cancelTask(String taskId) {
        ScheduledFuture<?> future = scheduledTasks.remove(taskId);
        if (future != null) {
            future.cancel(false);
        }
    }
}
