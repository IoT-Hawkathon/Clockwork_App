package com.darrienglasser.clockwork;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
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

    private MyDBHandler mDatabase;

    public AlarmAdapter(ArrayList<AlarmData> dataList, Context context, MyDBHandler database) {
        mDataList = dataList;
        mContext = context;
        mDatabase = database;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View alarmRow = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.alarm_row, parent, false);

        AlarmData data = mDataList.get(position);

        TextView hourDisplay = (TextView) alarmRow.findViewById(R.id.alarm_hour);
        TextView minuteDisplay = (TextView) alarmRow.findViewById(R.id.alarm_minute);

        Switch toggle = (Switch) alarmRow.findViewById(R.id.toggle_switch);

        hourDisplay.setText(
                String.format(
                mContext.getString(R.string.alarm_hour), data.getHour()));

        String cat;

        if (data.getMinute() < 10) {
            cat = "0" + data.getMinute();
        } else {
            cat = "" + data.getMinute();
        }

        minuteDisplay.setText(
                String.format(
                        mContext.getString(R.string.alarm_minute), cat));

        toggle.setChecked(data.isToggled());

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final AlarmData updatedData;
                updatedData = mDataList.get(position);
                updatedData.setToggled(isChecked);
                mDataList.remove(position);
                updateData(position, updatedData);
            }
        });

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

    /**
     * Helper method. Updates views.
     */
    public void updateData(AlarmData data) {
        mDataList.add(data);
        notifyDataSetChanged();
    }

    /**
     * Helper method. Updates views, destroys and recreates data table.
     */
    public void updateData(int index, AlarmData data) {
        mDataList.add(index, data);
        mDatabase.destroy();
        for (AlarmData alarmData : mDataList) {
            mDatabase.addData(alarmData);
        }

        notifyDataSetChanged();
    }
}
