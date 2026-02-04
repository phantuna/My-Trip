package org.example.demo_datn.quartz.jobs;

import org.example.demo_datn.quartz.timer.TimerInfo;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class HelloWorldJob implements Job {

    private static final Logger log = Logger.getLogger(HelloWorldJob.class.getName());



    @Override
    public void execute(JobExecutionContext context){
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        TimerInfo info = (TimerInfo) jobDataMap.get(HelloWorldJob.class.getSimpleName());
        log.info(info.getCallbackData());
    }
}
