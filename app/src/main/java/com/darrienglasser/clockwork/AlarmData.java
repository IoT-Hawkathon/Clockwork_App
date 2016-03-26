package com.darrienglasser.clockwork;

public class AlarmData {

    /**
     * Stored hour.
     */
    private int mHour;

    /**
     * Stored minute.
     */
    private int mMinute;

    /**
     * Whether or not alarm is toggled.
     */
    private boolean mToggled;

    public AlarmData(int hour, int minute, boolean toggled) {
        mHour = hour;
        mMinute = minute;
        mToggled = toggled;
    }
}
