package com.versusdeveloper.blogr.ReminderPosts;

public class Reminder {

    private String reminderTitle, reminderTime, reminderNotes;

    public Reminder(){

    }

    public Reminder (String reminderTitle, String reminderTime, String reminderNotes){
        this.reminderTitle = reminderTitle;
        this.reminderTime = reminderTime;
        this.reminderNotes = reminderNotes;
    }

    public String getReminderTitle() {
        return reminderTitle;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public String getReminderNotes() {
        return reminderNotes;
    }

    public void setReminderTitle(String reminderTitle) {
        this.reminderTitle = reminderTitle;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public void setReminderNotes(String reminderNotes) {
        this.reminderNotes = reminderNotes;
    }
}

