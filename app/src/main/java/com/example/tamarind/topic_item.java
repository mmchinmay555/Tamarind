package com.example.tamarind;

import java.time.DayOfWeek;
import java.util.Date;

public class topic_item {
    String topic_name;
    long time_recorded;//minutes
    String date_recorded;
    int day_recorded;

    public long getTime_recorded() {
        return time_recorded;
    }

    public topic_item(String topic_name, long time_recorded, String date_recorded, int day_recorded) {
        this.topic_name = topic_name;
        this.time_recorded = time_recorded;
        this.date_recorded = date_recorded;
        this.day_recorded = day_recorded;


    }
}
