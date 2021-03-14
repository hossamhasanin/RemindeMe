package com.hasanin.hossam.remindeme;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.Random;

public class ReminderReceiver extends BroadcastReceiver {

    private static final String ADMIN_CHANNEL_ID = "channel_900";
    NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        RemindersDB remindersDB = new RemindersDB(context);
        MainActivity mainActivity = MainActivity.instance();

        //Setting up Notification channels for android O and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels();
        }

        int notificationId = new Random().nextInt(60000);

        Intent intent1 = new Intent(context , MainActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context , 0 , intent1 , PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone ringtoneManager = RingtoneManager.getRingtone(context , alarmUri);
        ringtoneManager.play();

        Integer id = intent.getIntExtra("reminderId" , 0);
        if (id != 0){
            if (remindersDB.getReminder(id).getRepeat() == 0){
                remindersDB.UpdateActivityStatus(id , 0);
            }
        }

        AlarmManager alarmManager = (AlarmManager) mainActivity.getSystemService(Context.ALARM_SERVICE);
        mainActivity.allReminders = remindersDB.getAllReminders();
        mainActivity.remindersRecAdapter = new RemindersRecAdapter(mainActivity , mainActivity.allReminders , remindersDB , alarmManager , mainActivity.close , mainActivity.delete , mainActivity.addReminder);
        mainActivity.remindersList.setAdapter(mainActivity.remindersRecAdapter);
        mainActivity.remindersList.setLayoutManager(new LinearLayoutManager(mainActivity));

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_logo_luncher)
                .setContentTitle("Hey you have a reminder") //the "title" value you sent in your notification
                .setContentText(intent.getStringExtra("description")) //ditto
                .setVibrate(new long[]{1000 , 1000 , 1000 , 1000 , 1000})
                .setContentIntent(pendingIntent);

        notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(){
        CharSequence adminChannelName = "Reminder_channel_1";
        String adminChannelDescription = "channel_reminder";

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.GREEN);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

}
