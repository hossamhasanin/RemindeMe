package com.hasanin.hossam.remindeme;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

public class RemindersRecAdapter extends RecyclerView.Adapter<RemindersRecAdapter.ReminderViewHolder> {

    Activity activity;
    ArrayList<ReminderModel> allReminders;
    RemindersDB remindersDB;
    AlarmManager alarmManager;

    Calendar rightNow;
    int currentHour;
    int currentMinute;
    Calendar calendar;

    public RemindersRecAdapter(Activity activity , ArrayList<ReminderModel> allReminders , RemindersDB remindersDB , AlarmManager alarmManager){
        this.activity = activity;
        this.allReminders = allReminders;
        this.remindersDB = remindersDB;
        this.alarmManager = alarmManager;

        rightNow = Calendar.getInstance();
    }



    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.reminder_card , parent , false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {

        holder.description.setText(allReminders.get(position).getDescription());
        holder.time.setText(allReminders.get(position).getTime());
        if (allReminders.get(position).getActive() == 1){
            holder.active.setChecked(true);
        } else {
            holder.active.setChecked(false);
        }

        //calendar.set(Calendar.HOUR_OF_DAY , allReminders.get(position).getHours());
        //calendar.set(Calendar.MINUTE , allReminders.get(position).getMinutes());
        holder.active.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                remindersDB.UpdateActivityStatus(allReminders.get(position).getId(), 1);
                //allReminders.set(position, new ReminderModel(allReminders.get(position).getTime(), allReminders.get(position).getId(), allReminders.get(position).getHours(), allReminders.get(position).getMinutes() , allReminders.get(position).getTimeInMiliSecond() , allReminders.get(position).getDescription(), allReminders.get(position).getRepeat(), 1));

                long time = allReminders.get(position).getTimeInMiliSecond();
                if (calendar.before(rightNow)){
                    time += 86400000L;
                }

                Intent reminderReceiver = new Intent(activity , ReminderReceiver.class);
                reminderReceiver.putExtra("description" , allReminders.get(position).getDescription());
                reminderReceiver.putExtra("reminderId" , allReminders.get(position).getId());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(activity , allReminders.get(position).getId() , reminderReceiver , 0);
                if (allReminders.get(position).getRepeat() == 0){
                    alarmManager.set(AlarmManager.RTC_WAKEUP , time , pendingIntent);
                } else {
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP , time  , AlarmManager.INTERVAL_DAY , pendingIntent);
                }

                Toast.makeText(activity , "The reminder has been Activated !" , Toast.LENGTH_LONG).show();

            } else {
                remindersDB.UpdateActivityStatus(allReminders.get(position).getId(), 0);
                //allReminders.set(position, new ReminderModel(allReminders.get(position).getTime(), allReminders.get(position).getId(), allReminders.get(position).getHours(), allReminders.get(position).getMinutes() , allReminders.get(position).getTimeInMiliSecond() , allReminders.get(position).getDescription(), allReminders.get(position).getRepeat(), 0));

                Intent reminderReceiver = new Intent(activity , ReminderReceiver.class);
                reminderReceiver.putExtra("description" , allReminders.get(position).getDescription());
                reminderReceiver.putExtra("reminderId" , allReminders.get(position).getId());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(activity , allReminders.get(position).getId() , reminderReceiver , 0);
                alarmManager.cancel(pendingIntent);
                Toast.makeText(activity , "The reminder has been canceled !" , Toast.LENGTH_LONG).show();
            }
        });

    }


    @Override
    public int getItemCount() {
        return allReminders.size();
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout container;
        Switch active;
        TextView time;
        TextView description;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container_card);
            active = itemView.findViewById(R.id.active);
            time = itemView.findViewById(R.id.time);
            description = itemView.findViewById(R.id.description);
        }
    }

}
