package com.darrienglasser.clockwork;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class AlarmAdapter extends BaseAdapter {

    /**
     * List containing data.
     */
    private ArrayList<AlarmData> mDataList;

    /**
     * Android context.
     */
    private Context mContext;

    public AlarmAdapter(ArrayList<AlarmData> dataList, Context context) {
        mDataList = dataList;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View alarmRow = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.alarm_row, parent, false);

        AlarmData data = mDataList.get(position);

        TextView display = (TextView) alarmRow.findViewById(R.id.alarm_number);
        Switch toggle = (Switch) alarmRow.findViewById(R.id.toggle_switch);

        display.setText(
                String.format(
                mContext.getString(R.string.alarm_number), data.getHour(), data.getMinute()));

        toggle.setChecked(data.isToggled());

        return alarmRow;
    }

    @Override
    public int getCount() {
        if (mDataList == null) return 0;
        return mDataList.size();
    }

    @Override
    public long getItemId(int position) {
        // Useless method
        return position;
    }

    @Override
    public AlarmData getItem(int position) {
        return mDataList.get(position);
    }

}