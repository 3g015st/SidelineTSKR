package com.example.benedictlutab.sidelinetskr.models;

/**
 * Created by Benedict Lutab on 7/30/2018.
 */

public class availableTask
{
    private String task_id, title, line_one, city, date_time_end, task_fee, status, profile_picture;

    public availableTask(String task_id, String title, String line_one, String city, String date_time_end, String task_fee, String status, String profile_picture)
    {
        this.task_id = task_id;
        this.title = title;
        this.line_one = line_one;
        this.city = city;
        this.date_time_end = date_time_end;
        this.status = status;
        this.task_fee = task_fee;
        this.profile_picture = profile_picture;
    }

    public String getTask_id() {
        return task_id;
    }

    public String getTitle() {
        return title;
    }

    public String getLine_one() {
        return line_one;
    }

    public String getCity() {
        return city;
    }

    public String getDate_time_end() {
        return date_time_end;
    }

    public String getTask_fee() {
        return task_fee;
    }

    public String getStatus() {
        return status;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

}
