package com.hasanin.hossam.remindeme;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

public class RemindersRecAdapter extends RecyclerView.Adapter<RemindersRecAdapter.ReminderViewHolder> {

    Activity activity;
    ArrayList<ReminderModel> allReminders;
    RemindersDB remindersDB;
    AlarmManager alarmManager;
    FloatingActionButton close;
    FloatingActionButton delete;
    FloatingActionButton add;

    Calendar rightNow;
    int currentHour;
    int currentMinute;
    Calendar calendar;
    private ArrayList<ReminderModel> choosen;

    public RemindersRecAdapter(Activity activity , ArrayList<ReminderModel> allReminders , RemindersDB remindersDB , AlarmManager alarmManager , FloatingActionButton close , FloatingActionButton delete , FloatingActionButton add){
        this.activity = activity;
        this.allReminders = allReminders;
        this.remindersDB = remindersDB;
        this.alarmManager = alarmManager;
        this.close = close;
        this.delete = delete;
        this.add = add;

        rightNow = Calendar.getInstance();
        choosen = new ArrayList<>();
    }



    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.reminder_card , parent , false);
        return new ReminderViewHolder(view);
    }

    @SuppressLint("RestrictedApi")
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

        holder.container.setOnLongClickListener(c -> {
            if (!choosen.contains(allReminders.get(position))){
                add.setVisibility(View.GONE);
                close.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);

                choosen.add(allReminders.get(position));
                holder.container.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
                holder.description.setTextColor(Color.WHITE);
                holder.time.setTextColor(Color.DKGRAY);
            }
            return true;
        });

        holder.container.setOnClickListener(l -> {
            if (!choosen.isEmpty() && choosen.contains(allReminders.get(position))){
                choosen.remove(allReminders.get(position));
                holder.container.setBackgroundColor(Color.WHITE);
                holder.description.setTextColor(Color.BLACK);
                holder.time.setTextColor(Color.GRAY);
                if (choosen.isEmpty()){
                    add.setVisibility(View.VISIBLE);
                    close.setVisibility(View.GONE);
                    delete.setVisibility(View.GONE);
                }
            } else if (!choosen.isEmpty() && !choosen.contains(allReminders.get(position))){
                choosen.add(allReminders.get(position));
                holder.container.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
                holder.description.setTextColor(Color.WHITE);
                holder.time.setTextColor(Color.DKGRAY);
            } else {
                showReminderCard(position);
            }

        });

    }

    public ArrayList<ReminderModel> getChoosen(){
        return choosen;
    }

    private void showReminderCard(Integer position){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View layout = activity.getLayoutInflater().inflate(R.layout.show_reminder , null);
        TextView description = layout.findViewById(R.id.show_desc);
        TextView time = layout.findViewById(R.id.show_time);
        Button edit = layout.findViewById(R.id.edit);
        Button delete = layout.findViewById(R.id.delete_item);
        description.setText(allReminders.get(position).getDescription());
        time.setText(allReminders.get(position).getTime());
        builder.setView(layout);
        Dialog dialog = builder.create();
        edit.setOnClickListener(e -> {
            Intent intent = new Intent(activity , AddReminderActivity.class);
            intent.putExtra("hours" , allReminders.get(position).getHours());
            intent.putExtra("minutes" , allReminders.get(position).getMinutes());
            intent.putExtra("description" , allReminders.get(position).getDescription());
            intent.putExtra("repeat" , allReminders.get(position).getRepeat());
            activity.startActivity(intent);
        });
        delete.setOnClickListener(d -> {
            boolean t = remindersDB.deleteReminder(allReminders.get(position).getId());

            Intent reminderReceiver = new Intent(activity , ReminderReceiver.class);
            reminderReceiver.putExtra("description" , allReminders.get(position).getDescription());
            reminderReceiver.putExtra("reminderId" , allReminders.get(position).getId());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(activity , allReminders.get(position).getId() , reminderReceiver , 0);
            alarmManager.cancel(pendingIntent);

            notifyItemRemoved(position);
            allReminders.remove(allReminders.get(position));

            if (allReminders.isEmpty()){
                if (MainActivity.instance().emptyMess.getVisibility() == View.GONE)
                    MainActivity.instance().emptyMess.setVisibility(View.VISIBLE);
                if (MainActivity.instance().remindersList.getVisibility() == View.VISIBLE)
                    MainActivity.instance().remindersList.setVisibility(View.GONE);
            }

            dialog.dismiss();
        });
        dialog.show();
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
