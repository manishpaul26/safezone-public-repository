package com.track.safezone.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.track.safezone.R;
import com.track.safezone.database.SafeZoneDatabase;
import com.track.safezone.database.impl.FirebaseDB;
import com.track.safezone.services.AlarmReceiver;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StartQuarantineActivity extends AppCompatActivity {


    private static final String TAG = "StartQuarantineActivity";

    private SafeZoneDatabase database;

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private Button startTrackingButton;

    private List<CheckBox> allCheckboxes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_quarantine);


        this.database = FirebaseDB.getInstance();
        this.startTrackingButton = (Button) findViewById(R.id.button_start_tracking);

        TextView observationLegalMsg = (TextView) findViewById(R.id.textLegalDescription);


        this.findAllCheckboxes();
        this.applyListenerOnAllCheckboxes();




        startTrackingButton.setOnClickListener(view -> {

            for(CheckBox c: allCheckboxes) {
                c.setEnabled(false);
            }

            startTrackingButton.setEnabled(false);

            DateFormat dateFormat = new SimpleDateFormat(Constants.TIME_FORMAT);
            Date date = new Date();
            try {
                database.startQuarantineActivity(dateFormat.parse(dateFormat.format(date)));
            } catch (ParseException e) {
                Log.e(TAG, "onCreate: ", e);
            }


            alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            final BroadcastReceiver receiver = new AlarmReceiver();
            final IntentFilter intentFilter = new IntentFilter("ALARM_RECEIVER_INTENT_TRIGGER");
            getApplicationContext().registerReceiver(receiver, intentFilter);



            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
            intent.setClass(getApplicationContext(), AlarmReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);


            // TODO FIX ALARMS!
            // Set the alarm to start at approximately 2:00 p.m.
            Calendar calendar = Calendar.getInstance();
            //calendar.setTimeInMillis(System.currentTimeMillis());


            calendar.set(Calendar.AM_PM, Calendar.PM);
            //calendar.set(Calendar.HOUR_OF_DAY, calendar.get);
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 3);

            // With setInexactRepeating(), you have to use one of the AlarmManager interval
            // constants--in this case, AlarmManager.INTERVAL_DAY.
            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);


            Log.i(TAG, "onCreate: Alarm set for: " + new SimpleDateFormat(Constants.TIME_FORMAT).format(calendar.getTime()));


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
            }  else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
            } else {
                alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
            }


            Intent i = new Intent(getApplicationContext(), ConfirmationScreenActivity.class);
            startActivity(i);
        });
    }



    private void findAllCheckboxes() {
        LinearLayout rootLinearLayout = (LinearLayout) findViewById(R.id.layout_content_upper);
        int count = rootLinearLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = rootLinearLayout.getChildAt(i);
            if (v instanceof CheckBox) {
                CheckBox c = (CheckBox) v;
                allCheckboxes.add(c);
            }
        }
    }

    private View.OnClickListener checkboxOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            boolean allChecked = true;

            for(CheckBox c: allCheckboxes) {
                if (!c.isChecked()) {
                    allChecked = false;
                    break;
                }
            }
            
            if (allChecked) {
                startTrackingButton.setVisibility(View.VISIBLE);
                startTrackingButton.setEnabled(true);

            }
        }
    };

    private void applyListenerOnAllCheckboxes() {

        LinearLayout rootLinearLayout = (LinearLayout) findViewById(R.id.layout_content_upper);
        int count = rootLinearLayout.getChildCount();
        for (CheckBox c: allCheckboxes) {
            c.setOnClickListener(checkboxOnClickListener);
        }
    }
}
