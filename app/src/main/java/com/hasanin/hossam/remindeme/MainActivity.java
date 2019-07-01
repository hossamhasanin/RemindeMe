package com.hasanin.hossam.remindeme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RemindersDB remindersDB;
    RemindersRecAdapter remindersRecAdapter;
    RecyclerView remindersList;
    FloatingActionButton addReminder;
    FloatingActionButton close;
    FloatingActionButton delete;
    TextView emptyMess;
    ArrayList<ReminderModel> allReminders;
    AlarmManager alarmManager;
    public static MainActivity inst;

    public static MainActivity instance(){
        return inst;
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        remindersDB = new RemindersDB(this);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        addReminder = findViewById(R.id.addReminder);
        delete = findViewById(R.id.delete);
        close = findViewById(R.id.close);
        remindersList = findViewById(R.id.remindersList);
        emptyMess = findViewById(R.id.empty_mess);

        addReminder.setOnClickListener(c -> {
            Intent intent = new Intent(MainActivity.this , AddReminderActivity.class);
            MainActivity.this.startActivity(intent);
        });

        delete.setOnClickListener(c -> {
            Dialog dialog = showProgressDailog();
            ArrayList<ReminderModel> choosen = remindersRecAdapter.getChoosen();

            for (ReminderModel reminder : choosen){
                boolean deleted = remindersDB.deleteReminder(reminder.getId());
                if (!deleted){
                    Toast.makeText(getApplicationContext() , "Error while deleting !" , Toast.LENGTH_LONG).show();
                    break;
                }

                Intent reminderReceiver = new Intent(getApplicationContext() , ReminderReceiver.class);
                reminderReceiver.putExtra("description" , reminder.getDescription());
                reminderReceiver.putExtra("reminderId" , reminder.getId());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext() , reminder.getId() , reminderReceiver , 0);
                alarmManager.cancel(pendingIntent);

                remindersRecAdapter.notifyItemRemoved(remindersRecAdapter.allReminders.indexOf(reminder));
                remindersRecAdapter.allReminders.remove(reminder);
            }

            if (remindersRecAdapter.allReminders.isEmpty()){
                if (emptyMess.getVisibility() == View.GONE)
                    emptyMess.setVisibility(View.VISIBLE);
                if (remindersList.getVisibility() == View.VISIBLE)
                    remindersList.setVisibility(View.GONE);
            }

            delete.setVisibility(View.GONE);
            addReminder.setVisibility(View.VISIBLE);
            close.setVisibility(View.GONE);

            dialog.dismiss();
        });

        close.setOnClickListener(c -> {
            remindersRecAdapter = new RemindersRecAdapter(this , allReminders , remindersDB , alarmManager , close , delete , addReminder);
            remindersList.setAdapter(remindersRecAdapter);
            remindersList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            delete.setVisibility(View.GONE);
            addReminder.setVisibility(View.VISIBLE);
            close.setVisibility(View.GONE);
        });

        if (remindersDB.TableEmptinessCheck()){
            if (emptyMess.getVisibility() == View.VISIBLE)
                emptyMess.setVisibility(View.GONE);
            if (remindersList.getVisibility() == View.GONE)
                remindersList.setVisibility(View.VISIBLE);
            allReminders = remindersDB.getAllReminders();
            remindersRecAdapter = new RemindersRecAdapter(this , allReminders , remindersDB , alarmManager , close , delete , addReminder);
            remindersList.setAdapter(remindersRecAdapter);
            remindersList.setLayoutManager(new LinearLayoutManager(this));
        } else {
            if (emptyMess.getVisibility() == View.GONE)
                emptyMess.setVisibility(View.VISIBLE);
            if (remindersList.getVisibility() == View.VISIBLE)
                remindersList.setVisibility(View.GONE);
        }
    }

    public Dialog showProgressDailog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = this.getLayoutInflater().inflate(R.layout.progress_dialog , null);
        TextView waitingMess = view.findViewById(R.id.waiting_mess);
        waitingMess.setText("Deleting the reminders ! ....");
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    @Override
    protected void onStart() {
        super.onStart();
        inst = this;
    }
}
