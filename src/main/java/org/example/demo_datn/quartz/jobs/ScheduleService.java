package org.example.demo_datn.quartz.jobs;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.example.demo_datn.quartz.timer.TimerInfo;
import org.example.demo_datn.quartz.util.TimerUtils;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {
    private static Logger log = LoggerFactory.getLogger(ScheduleService.class);

    private final Scheduler scheduler;

    @Autowired
    public ScheduleService(Scheduler scheduler){
        this.scheduler = scheduler;
    }

    public void schedule( final Class clazz , final TimerInfo info){
        final JobDetail jobDetail = TimerUtils.buildJobDetail(clazz,info);
        final Trigger trigger = TimerUtils.buildTrigger(clazz,info);
        try {
            scheduler.scheduleJob(jobDetail,trigger);
        }catch(SchedulerException e){
            log.error(e.getMessage(), e);
        }

    }
    @PostConstruct
    public void init(){
        try{
            scheduler.start();
        }catch(SchedulerException e){
            log.error(e.getMessage(), e);
        }
    }

    @PreDestroy
    public void preDestroy(){
        try {
            scheduler.shutdown();
        }catch(SchedulerException e){
            log.error(e.getMessage(), e);
        }
    }
}


