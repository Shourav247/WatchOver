package com.example.shourav.watchover.Fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.shourav.watchover.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BatteryFragment extends Fragment {

    private Context mContext;

    private TextView mTextViewInfo;
    private TextView mTextViewPercentage;
    private ProgressBar mProgressBar;

    private int mProgressStatus = 0;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /*
                BatteryManager
                    The BatteryManager class contains strings and constants used for values in the
                    ACTION_BATTERY_CHANGED Intent, and provides a method for querying battery
                    and charging properties.
            */
            /*
                public static final String EXTRA_SCALE
                    Extra for ACTION_BATTERY_CHANGED: integer containing the maximum battery level.
                    Constant Value: "scale"
            */
            // Get the battery scale
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
            // Display the battery scale in TextView
            mTextViewInfo.setText("Battery Scale : " + scale);

            /*
                public static final String EXTRA_LEVEL
                    Extra for ACTION_BATTERY_CHANGED: integer field containing the current battery
                    level, from 0 to EXTRA_SCALE.

                    Constant Value: "level"
            */
            // get the battery level
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
            // Display the battery level in TextView
            mTextViewInfo.setText(mTextViewInfo.getText() + "\nBattery Level : " + level);

            // Calculate the battery charged percentage
            float percentage = level/ (float) scale;
            // Update the progress bar to display current battery charged percentage
            mProgressStatus = (int)((percentage)*100);

            // Show the battery charged percentage text inside progress bar
            mTextViewPercentage.setText("" + mProgressStatus + "%");

            // Show the battery charged percentage in TextView
            mTextViewInfo.setText(mTextViewInfo.getText() +
                    "\nPercentage : "+ mProgressStatus + "%");

            // Display the battery charged percentage in progress bar
            mProgressBar.setProgress(mProgressStatus);
        }
    };



    public BatteryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_battery, container, false);

        mContext = getContext();

        //Initialize a new IntentFilter instance
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        // Register the broadcast receiver
        mContext.registerReceiver(mBroadcastReceiver,iFilter);

        // Get the widgets reference from XML layout
        mTextViewInfo = (TextView) v.findViewById(R.id.tv_info);
        mTextViewPercentage = (TextView) v.findViewById(R.id.tv_percentage);
        mProgressBar = (ProgressBar) v.findViewById(R.id.pb);

        return v;
    }

}
