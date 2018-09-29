package com.versusdeveloper.blogr.ReminderPosts;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.versusdeveloper.blogr.R;

public class ReminderViewHolder extends RecyclerView.ViewHolder{
    View mView;

    public ReminderViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setReminderTitle(String title){
        TextView reminder_title = mView.findViewById(R.id.reminderTitle);
        reminder_title.setText(title);
    }

    public void setReminderTime(String time){
        TextView reminder_time = mView.findViewById(R.id.reminderTime);
        reminder_time.setText(time);
    }

    public void setReminderNotes(String notes){
        TextView reminder_notes = mView.findViewById(R.id.reminderNotes);
        reminder_notes.setText(notes);
    }

}
