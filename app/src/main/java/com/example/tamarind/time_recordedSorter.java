package com.example.tamarind;

import java.util.Comparator;

public class time_recordedSorter implements Comparator<topic_item>{
    @Override
    public int compare(topic_item o1, topic_item o2) {
        if(o1.getTime_recorded() < o2.getTime_recorded()){
            return 1;
        } else {
            return -1;
        }
    }
}
