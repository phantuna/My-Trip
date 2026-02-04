package org.example.demo_datn.quartz.timer;


import lombok.Data;

@Data
public class TimerInfo {
    private int totalFireCount;
    private boolean runforever;
    private long reqeatIntervalMs;
    private long initialOffsetMs;
    private String callbackData;

}

