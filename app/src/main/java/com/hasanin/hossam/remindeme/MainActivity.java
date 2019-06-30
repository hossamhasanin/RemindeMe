package com.hasanin.hossam.remindeme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RemindersDB remindersDB;
    RemindersRecAdapter remindersRecAdapter;
    RecyclerView remindersList;
    ArrayList<ReminderModel> allReminders;
    AlarmManager alarmManager;
    public static MainActivity inst;

    public static MainActivity instance(){
        return inst;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        remindersDB = new RemindersDB(this);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        FloatingActionButton addReminder = findViewById(R.id.addReminder);
        remindersList = findViewById(R.id.remindersList);
        TextView emptyMess = findViewById(R.id.empty_mess);

        addReminder.setOnClickListener(c -> {
            Intent intent = new Intent(MainActivity.this , AddReminderActivity.class);
            MainActivity.this.startActivity(intent);
        });

        if (remindersDB.TableEmptinessCheck()){
            if (emptyMess.getVisibility() == View.VISIBLE)
                emptyMess.setVisibility(View.GONE);
            if (remindersList.getVisibility() == View.GONE)
                remindersList.setVisibility(View.VISIBLE);
            allReminders = remindersDB.getAllReminders();
            remindersRecAdapter = new RemindersRecAdapter(this , allReminders , remindersDB , alarmManager);
            remindersList.setAdapter(remindersRecAdapter);
            remindersList.setLayoutManager(new LinearLayoutManager(this));
        } else {
            if (emptyMess.getVisibility() == View.GONE)
                emptyMess.setVisibility(View.VISIBLE);
            if (remindersList.getVisibility() == View.VISIBLE)
                remindersList.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        inst = this;
    }
}
