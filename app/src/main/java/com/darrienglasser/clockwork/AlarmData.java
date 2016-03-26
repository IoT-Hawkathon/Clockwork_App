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

    private long _id;

    /**
     * Basic constructor. Used to initialize object.
     *
     * @param hour Hour to be set.
     * @param minute Minute to be set.
     * @param toggled Whether or not alarm is toggled.
     */
    public AlarmData(int hour, int minute, boolean toggled) {
        mHour = hour;
        mMinute = minute;
        mToggled = toggled;
    }

    public int getHour() {
        return mHour;
    }

    public int getMinute() {
        return mMinute;
    }

    public boolean isToggled() {
        return mToggled;
    }

    public long getId() {
        return _id;
    }

    public void setHour(int mHour) {
        this.mHour = mHour;
    }

    public void setMinute(int mMinute) {
        this.mMinute = mMinute;
    }

    public void setToggled(boolean mToggled) {
        this.mToggled = mToggled;
    }

    public void setId(long _id) {
        this._id = _id;
    }
}
