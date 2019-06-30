package com.hasanin.hossam.remindeme;

public class ReminderModel {

    String time;
    Integer id;
    Integer hours;
    Integer minutes;
    long timeInMiliSecond;
    String description;
    Integer repeat;
    Integer active;

    public ReminderModel(){}

    public ReminderModel(String time, Integer id, Integer hours, Integer minutes, long timeInMiliSecond, String description, Integer repeat, Integer active) {
        this.time = time;
        this.id = id;
        this.hours = hours;
        this.minutes = minutes;
        this.timeInMiliSecond = timeInMiliSecond;
        this.description = description;
        this.repeat = repeat;
        this.active = active;
    }

    public long getTimeInMiliSecond() {
        return timeInMiliSecond;
    }

    public void setTimeInMiliSecond(long timeInMiliSecond) {
        this.timeInMiliSecond = timeInMiliSecond;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRepeat() {
        return repeat;
    }

    public void setRepeat(Integer repeat) {
        this.repeat = repeat;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }
}
