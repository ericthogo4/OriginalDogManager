package com.chanresti.originaldogmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {
public static String currentdate;
    String TAG = "AlarmReceiver";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
                RemindersDbAdapter adapter=new RemindersDbAdapter(context);
                adapter.open();


        Log.d("MainTag"+TAG, " onReceive has been called"+"\t");


        if (intent.getAction() != null && context != null) {


            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            adapter.open();
                //On boot completed,the next alarm is scheduled
                Log.d("MainTag"+TAG, "onReceive: BOOT_COMPLETED");
                //first get the current time
                Date date3=new Date();
                Log.d("MainTag"+TAG, "onReceive here is the tome I will use to see if the next reminder is eligible"+"\t"+date3.toString());
                //get the next date to be scheduled
                Date date=NotificationScheduler.nextalarm(adapter.fetchDates());
                Log.d("MainTag"+TAG, "onReceive here is latest date"+"\t"+date.toString());
                //check whether the next date is after the current date
                if (date.after(date3)){
                    //if it is set an alarm to fire at that time
                    NotificationScheduler.setReminder(context, AlarmReceiver.class, date);
                    Log.d("MainTag"+TAG, " onReceive i have set next alarm to"+"\t"+date.toString());
                }
                else {
                    Log.d("MainTag"+TAG, "onReceive I didn't set the alarm this date was before this time"+"\t"+date3.toString());
                }
                adapter.close();
        }}



//on receive:
        //whenever a reminder is set in notificationscheduler, the public static variable :datestring is updated to the corresponding reminder's date as it is in the database
        Log.d("MainTag"+TAG, "onReceive i am about to fetch reminders by this datestring:"+"\t"+currentdate);
        //this datestring is used to identify the corresponding reminder in the database so that the reminder text for this particular time is acquired
        ArrayList<Reminder> reminder=adapter.fetchReminderByDateString(currentdate);
        int x=0;
        //the sting that will be passed to the alarmpage class through the intent
        String intentstring;
            StringBuilder stringBuilder=new StringBuilder();
        while(x>=0 && x<=reminder.size()-1){
            Log.d("MainTag"+TAG, " onReceive I am about to call NotificationScheduler.showNotification "+"\t");
        //show notification with the reminder text
            NotificationScheduler.showNotification(context, RemindersActivity.class,
              " DogManager", reminder.get(x).getContent());
        if(x>=1){
            stringBuilder.append(" and ");
        }
        stringBuilder.append(reminder.get(x).getContent());


            Log.d("MainTag"+TAG, " onReceive in while"+"\t"+"Reminder"+x+"\t"+reminder.get(x).getDatestring());
            Log.d("MainTag"+TAG, " onReceive in while"+"\t"+"This is the text to show:"+x+"\t"+reminder.get(x).getContent());

            Toast.makeText(context,"Remember To:"+"\t"+reminder.get(x).getContent(),Toast.LENGTH_LONG).show();
        x=x+1;
        }
        intentstring=stringBuilder.toString();
        //start the alarmpage activity
       NotificationScheduler.showalarmpage(intentstring,context);
//get the next date to be scheduled and set a reminder for that date:
        Date date3=new Date();
        Log.d("MainTag"+TAG, "onReceive here is the tome I will use to see if the next reminder is eligible"+"\t"+date3.toString());
        Date date=NotificationScheduler.nextalarm(adapter.fetchDates());
        Log.d("MainTag"+TAG, "onReceive here is latest date"+"\t"+date.toString());
        if (date.after(date3)){
            NotificationScheduler.setReminder(context, AlarmReceiver.class, date);
            Log.d("MainTag"+TAG, " onReceive i have set next alarm to"+"\t"+date.toString());
        }
        else {
            Log.d("MainTag"+TAG, "onReceive I didn't set the alarm this date was before this time"+"\t"+date3.toString());
        }
        adapter.close();
    }
}

