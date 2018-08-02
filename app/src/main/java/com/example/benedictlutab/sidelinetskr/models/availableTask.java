package com.example.benedictlutab.sidelinetskr.models;

/**
 * Created by Benedict Lutab on 7/30/2018.
 */

public class availableTask
{
    private String task_id, title, line_one, city, date_time_end, task_fee, status, profile_picture, first_name, last_name, category_name, image_one, image_two, description, date_time_posted;

    public availableTask(String task_id, String title, String line_one, String city, String date_time_end, String task_fee, String status, String profile_picture,
                         String first_name, String last_name, String category_name, String image_one, String image_two, String description, String date_time_posted)
    {
        this.task_id         = task_id;
        this.title           = title;
        this.line_one        = line_one;
        this.city            = city;
        this.date_time_end   = date_time_end;
        this.task_fee        = task_fee;
        this.status          = status;
        this.profile_picture = profile_picture;
        this.first_name      = first_name;
        this.last_name       = last_name;
        this.category_name   = category_name;
        this.image_one       = image_one;
        this.image_two       = image_two;
        this.description     = description;
        this.date_time_posted = date_time_posted;
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

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getImage_one() {
        return image_one;
    }

    public String getImage_two() {
        return image_two;
    }

    public String getDescription() {
        return description;
    }

    public String getDate_time_posted() {
        return date_time_posted;
    }
}
