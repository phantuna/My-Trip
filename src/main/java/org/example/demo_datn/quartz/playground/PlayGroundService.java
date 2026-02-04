package org.example.demo_datn.quartz.playground;

import org.example.demo_datn.quartz.jobs.HelloWorldJob;
import org.example.demo_datn.quartz.jobs.ScheduleService;
import org.example.demo_datn.quartz.timer.TimerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.awt.SystemColor.info;

@Service
public class PlayGroundService {

    private final ScheduleService  scheduler;

    @Autowired
    public PlayGroundService (final ScheduleService scheduler){
        this.scheduler = scheduler;
    }

    public void runHelloWorldJob(){
        final TimerInfo info = new TimerInfo();
        info.setTotalFireCount(5);
        info.setReqeatIntervalMs(2000);
        info.setInitialOffsetMs(1000);
        info.setRunforever(false);
        info.setCallbackData("My callback data");
        scheduler.schedule(HelloWorldJob.class, info);

    }
}
