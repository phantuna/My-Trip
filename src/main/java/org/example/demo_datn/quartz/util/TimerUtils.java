package org.example.demo_datn.quartz.util;

import org.example.demo_datn.quartz.timer.TimerInfo;
import org.quartz.*;

import java.util.Date;

public class TimerUtils {
    private TimerUtils(){}

    public static JobDetail buildJobDetail(final Class clazz, final TimerInfo info){
        if (clazz == null) throw new IllegalArgumentException("Job class is required");
        if (info == null) throw new IllegalArgumentException("TimerInfo is required");
        final JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(clazz.getSimpleName(), info);

        return JobBuilder
                .newJob(clazz)
                .withIdentity(clazz.getSimpleName())
                .setJobData(jobDataMap)
                .build();
    }

    public static Trigger buildTrigger(final Class clazz, final TimerInfo info){
        SimpleScheduleBuilder builder = SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(info.getReqeatIntervalMs());

        if(info.isRunforever()){
            builder = builder.repeatForever();
        }else{
            builder = builder.withRepeatCount(info.getTotalFireCount() -1);
        }

        return TriggerBuilder.newTrigger()
                .withIdentity((clazz.getSimpleName()))
                .withSchedule(builder)
                .startAt(new Date(System.currentTimeMillis() + info.getInitialOffsetMs()))
                .build();
    }


}
