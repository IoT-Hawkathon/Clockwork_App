package com.darrienglasser.clockwork;

/**
 * POJO containing calendar info for an event.
 */
public class CalendarInfo {
    private String mEventName;

    private String mStartTime;

    private String mEndTime;

    /**
     * Basic constructor. Used to initialize the object.
     *
     */
    public CalendarInfo(String eventName, String startTime, String endTime) {
        mEventName = eventName;
        mStartTime = startTime;
        mEndTime = endTime;
    }

    public String getEventName() {
        return mEventName;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public String getEndTime() {
        return mEndTime;
    }
}
