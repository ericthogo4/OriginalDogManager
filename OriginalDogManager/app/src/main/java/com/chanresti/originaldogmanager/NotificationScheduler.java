package com.chanresti.originaldogmanager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.app.TaskStackBuilder;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Mwangi on 21/03/2018.
 */

public class NotificationScheduler  {
   static DateFormat df=new SimpleDateFormat("HH:mm  dd/MM/yyyy");
    public static final int DAILY_REMINDER_REQUEST_CODE=100;
    public static  int UNIQUE_NOTIFICATION_REQUEST_CODE=100;
    public static final String TAG="NotifiScheduler";

//method for setting a reminder to a certain date(notice that the date has time within it)
    public static void setReminder(Context context, Class<?> cls,Date datestring)
    {
        Log.d("MainTag"+TAG, " setReminder has been called"+"\t"+"with this date:"+"\t"+datestring.toString());
        //getting the current time
        Calendar calendar = Calendar.getInstance();
        Calendar setcalendar = Calendar.getInstance();
        //getting the time of the reminder
       setcalendar.setTime(datestring);
       if(setcalendar.after(calendar)){
           //setting the public static variable currentdate in alarmreceiver so that it can be used to identify the reminder
       AlarmReceiver.currentdate=df.format(datestring);
           Log.d("MainTag"+TAG, " setReminder I have set AlarmReceiver.currentdate to"+"\t"+df.format(datestring)+"\t"+"see?:  "+AlarmReceiver.currentdate);
        // cancel already scheduled reminders
        cancelReminder(context,cls);
           Log.d("MainTag"+TAG, " setReminder I have canceled the previous alarm"+"\t");


        // Enable a receiver

        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);


        Intent intent1 = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, DAILY_REMINDER_REQUEST_CODE, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(), pendingIntent);}

    }
//a method to cancel already set reminders
    public static void cancelReminder(Context context,Class<?> cls)
    {
        // Disable a receiver
        Log.d("MainTag"+TAG, " cancelReminder has been called"+"\t");
        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent1 = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, DAILY_REMINDER_REQUEST_CODE, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(pendingIntent);
        pendingIntent.cancel();
    }
//a method to show notifications
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void showNotification(Context context, Class<?> cls, String title, String content)
    {
        Log.d("MainTag"+TAG, " showNotification has been called"+"\t"+"to show"+"\t"+title+"\t"+content);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    ++UNIQUE_NOTIFICATION_REQUEST_CODE;
        Intent notificationIntent = new Intent(context, cls);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(cls);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(UNIQUE_NOTIFICATION_REQUEST_CODE, PendingIntent.FLAG_ONE_SHOT);



        Notification notification = new Notification.Builder(context).setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(UNIQUE_NOTIFICATION_REQUEST_CODE, notification);

    }// a method to show the alarmpage activity
    public static  void showalarmpage(String Doggiewords,Context context){
        Intent intent67=new Intent(context,AlarmPage.class);
        intent67.putExtra("intentstring",Doggiewords);
      intent67.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
     context.startActivity(intent67);

    }
    //a method to get the next date to be scheduled
    public static Date nextalarm(ArrayList<Date> a){
        Log.d("MainTag"+TAG, " nextAlarm has been called"+"\t"+"with this arraylist"+"\t"+a.toString());

        DateCompare compare = new DateCompare();

        Collections.sort(a, compare);
        Log.d("MainTag"+TAG, "nextAlarm  here is the sorted arraylist I am using:"+"\t"+a.toString());
        Date date=new Date();
        if(a.size()>0){
        date=a.get(0);}
        else {
            Log.d("MainTag"+TAG, " nextAlarm the size of arraylist is less or equal to zero"+"\t");
        }
        Log.d("MainTag"+TAG, " nextAlarm here is the date I will return"+"\t"+date.toString());
        return date;
    }
//a comparator class for sorting the arraylist
    static class DateCompare implements Comparator<Date> {

        public int compare(Date one, Date two){

            return one.compareTo(two);

        }
    }

}

