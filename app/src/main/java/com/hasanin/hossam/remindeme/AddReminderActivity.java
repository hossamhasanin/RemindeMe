package com.hasanin.hossam.remindeme;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class AddReminderActivity extends AppCompatActivity {

    int isRepeat = 0;
    RemindersDB remindersDB;
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        TimePicker timePicker = findViewById(R.id.timePicker);
        Switch repeat = findViewById(R.id.repeat);
        EditText desciption = findViewById(R.id.description);
        Button save = findViewById(R.id.save);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        remindersDB = new RemindersDB(this);

        save.setOnClickListener(c -> {
           if (repeat.isChecked())
               isRepeat = 1;

           int lastId = 0;
           if (remindersDB.TableEmptinessCheck())
                lastId = remindersDB.GetLastRecordId();


            Calendar calendar = Calendar.getInstance();
            Calendar now = Calendar.getInstance();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                calendar.set(Calendar.HOUR_OF_DAY , timePicker.getHour());
                calendar.set(Calendar.MINUTE , timePicker.getMinute());

                remindersDB.InsertNewReminder(timePicker.getHour() + " : " + timePicker.getMinute() , desciption.getText().toString() , timePicker.getHour() , timePicker.getMinute() , isRepeat , calendar.getTimeInMillis());
            } else {
                calendar.set(Calendar.MINUTE , timePicker.getCurrentHour());
                calendar.set(Calendar.MINUTE , timePicker.getCurrentMinute());
                remindersDB.InsertNewReminder(timePicker.getCurrentHour() + " : " + timePicker.getCurrentMinute() , desciption.getText().toString() , timePicker.getCurrentHour() , timePicker.getCurrentMinute() , isRepeat , calendar.getTimeInMillis());
            }
            int reminderId = lastId+1;
            Intent reminderReceiver = new Intent(getApplicationContext() , ReminderReceiver.class);
            reminderReceiver.putExtra("description" , desciption.getText().toString());
            reminderReceiver.putExtra("reminderId" , reminderId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(AddReminderActivity.this , reminderId , reminderReceiver , 0);

            long time = calendar.getTimeInMillis();
            if (calendar.before(now)){
                time += 86400000L;
            }

            if (repeat.isChecked()){
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP , time , AlarmManager.INTERVAL_DAY , pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP , time , pendingIntent);
            }


            Toast.makeText(getApplicationContext() , "Added the reminder successfully :)" , Toast.LENGTH_LONG).show();
            Intent mainActivity = new Intent(AddReminderActivity.this , MainActivity.class);
            startActivity(mainActivity);
        });

    }
}
