package com.hasanin.hossam.remindeme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class RemindersDB extends SQLiteOpenHelper {


    private static String DBNAME = "remindersList";
    private static int version = 1;



    public RemindersDB(@Nullable Context context) {
        super(context, DBNAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS `reminder` ( `id` INTEGER, `time` TEXT , `hours` INTEGER , `minutes` INTEGER , `timeInMiliSecond` LONG , `description` TEXT DEFAULT NULL , `repeat` INTEGER DEFAULT 0 , `active` INTEGER DEFAULT 1 ,  PRIMARY KEY(`id`) ); ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table IF EXISTS reminder");
        onCreate(db);
    }

    public boolean InsertNewReminder (String time , String desc , Integer hours , Integer minutes , Integer repeat , long timeInMiliSecond){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("time",time);
        contentValues.put("hours",hours);
        contentValues.put("minutes",minutes);
        contentValues.put("timeInMiliSecond" , timeInMiliSecond);
        contentValues.put("description" , desc);
        contentValues.put("repeat" , repeat);
        Long result = db.insert("reminder" , null , contentValues);
        if (result == 1){
            return true;
        }else {
            return false;
        }
    }

    public int GetLastRecordId(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM reminder order by id desc limit 1", null);
        data.moveToFirst();
        return data.getInt(data.getColumnIndex("id"));
    }

    public boolean TableEmptinessCheck(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery("SELECT count(*) FROM reminder" , null);
        data.moveToFirst();
        if (data.getInt(0) > 0){
            return true;
        } else {
            return false;
        }
    }


    public ArrayList<ReminderModel> getAllReminders (){
        ArrayList<ReminderModel> allReminders = new ArrayList<ReminderModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        // type = 0 means folder
        Cursor data = db.rawQuery("SELECT * FROM reminder", null);
        data.moveToFirst();
        while (data.isAfterLast() == false){
            allReminders.add(new ReminderModel(data.getString(data.getColumnIndex("time")) , data.getInt(data.getColumnIndex("id")) , data.getInt(data.getColumnIndex("hours")), data.getInt(data.getColumnIndex("minutes")) , data.getLong(data.getColumnIndex("timeInMiliSecond")) , data.getString(data.getColumnIndex("description")) , data.getInt(data.getColumnIndex("repeat")) , data.getInt(data.getColumnIndex("active"))));
            data.moveToNext();
        }
        return allReminders;
    }

    public ReminderModel getReminder(Integer id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM reminder where id = " + id, null);
        data.moveToFirst();
        ReminderModel reminderModel = new ReminderModel();
        reminderModel.setId(id);
        reminderModel.setTime(data.getString(data.getColumnIndex("time")));
        reminderModel.setDescription(data.getString(data.getColumnIndex("description")));
        reminderModel.setHours(data.getInt(data.getColumnIndex("hours")));
        reminderModel.setMinutes(data.getInt(data.getColumnIndex("minutes")));
        reminderModel.setActive(data.getInt(data.getColumnIndex("active")));
        reminderModel.setRepeat(data.getInt(data.getColumnIndex("repeat")));
        reminderModel.setTimeInMiliSecond(data.getLong(data.getColumnIndex("timeInMiliSecond")));
        return reminderModel;
    }

    public void UpdateActivityStatus(Integer id , int status){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("update reminder set active = "+ status +" where id = " + id);
    }

    public boolean deleteReminder(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM reminder WHERE id = " + id);
            return true;
        }catch (Exception e){
            System.out.println(e);
            return false;
        }
    }


}
