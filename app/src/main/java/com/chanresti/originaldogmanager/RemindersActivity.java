package com.chanresti.originaldogmanager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RemindersActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,NavigationView.OnNavigationItemSelectedListener {

private DrawerLayout mDraw;

    private ListView mListView;
    public String MainTag="MainTag";

   public int hh=0, mm=0, dd=0, MM=0, y=0;
    public TimePickerDialog timePickerDialog;
    public DatePickerDialog datePickerDialog;
    Dialog dialog ;
    private RemindersDbAdapter mDbAdapter;
    private RemindersSimpleCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

//configuring the side navigational drawer
        mDraw=findViewById(R.id.drawer_layout);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDraw, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDraw.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //adapting the listview to the data in the database
        mListView =  findViewById(R.id.reminders_list_view);
        mListView.setDivider(null);
        mDbAdapter = new RemindersDbAdapter(this);
        mDbAdapter.open();
        Cursor cursor = mDbAdapter.fetchAllReminders();
//from columns defined in the db
        String[] from = new String[]{
                RemindersDbAdapter.COL_CONTENT,RemindersDbAdapter.COL_DATESTRING
        };
//to the ids of views in the layout
        int[] to = new int[]{
                R.id.row_text,R.id.row_datestring
        };
        mCursorAdapter = new RemindersSimpleCursorAdapter(
//context
                RemindersActivity.this,
//the layout of the row
                R.layout.reminders_row,
//cursor
                cursor,
//from columns defined in the db
                from,
//to the ids of views in the layout
                to,
//flag - not used
                0);

        mListView.setAdapter(mCursorAdapter);

// on item click:
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int masterListPosition, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RemindersActivity.this);
                ListView modeListView = new ListView(RemindersActivity.this);
                String[] modes = new String[] { "Edit Reminder", "Delete Reminder" };
                ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(RemindersActivity.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, modes);
                modeListView.setAdapter(modeAdapter);
                builder.setView(modeListView);
                final Dialog dialog = builder.create();
                dialog.show();
                modeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//edit reminder
                        //edit reminder
                        if (position == 0) {
                            int nId = getIdFromPosition(masterListPosition);
                            Reminder reminder = mDbAdapter.fetchReminderById(nId);
                            fireCustomDialog(reminder);}
//delete reminder
                            else {
                            mDbAdapter.deleteReminderById(getIdFromPosition(masterListPosition));
                            mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                            Date nextala=NotificationScheduler.nextalarm(mDbAdapter.fetchDates());
                            NotificationScheduler.setReminder(RemindersActivity.this,AlarmReceiver.class,nextala);
                            Log.d(MainTag, "A reminder has been set to"+"\t"+nextala);
                        }

                        dialog.dismiss();
                    }
                });
            }
        });

        //when multiple list items are selected
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean
                    checked) { }
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.del_menu, menu);
                return true;
            }
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item_delete_reminder:
                        for (int nC = mCursorAdapter.getCount() - 1; nC >= 0; nC--) {
                            if (mListView.isItemChecked(nC)) {
                                mDbAdapter.deleteReminderById(getIdFromPosition(nC));

                            }
                        }
                        //set the next reminder
                        Date nextala=NotificationScheduler.nextalarm(mDbAdapter.fetchDates());
                        NotificationScheduler.setReminder(RemindersActivity.this,AlarmReceiver.class,nextala);
                        Log.d(MainTag, "A reminder has been set to"+"\t"+nextala);
                        mode.finish();
                        mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                        return true;
                }
                return false;
            }
            @Override
            public void onDestroyActionMode(ActionMode mode) { }
        });}
//        Button button=findViewById(R.id.textid);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Date nextala=NotificationScheduler.nextalarm(mDbAdapter.fetchDates());
//        NotificationScheduler.setReminder(RemindersActivity.this,AlarmReceiver.class,nextala);
//        Log.d(MainTag, "A reminder has been set to"+"\t"+nextala);
//            }
//        });
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_new) {
        fireCustomDialog(null);
        } else if (id == R.id.nav_training) {
            Intent intent4=new Intent(RemindersActivity.this,VideoList.class);
            startActivity(intent4);
        } else if (id == R.id.nav_files) {
            Intent intent=new Intent(RemindersActivity.this,Profile.class);
            startActivity(intent);
        } else if (id == R.id.nav_exit) {
            finish();
        }



       mDraw.closeDrawer(GravityCompat.START);
        return true;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_reminders,menu);
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_new:
//                fireCustomDialog(null);
//                Log.d(getLocalClassName(),"create new Reminder");
//                return true;
//            case R.id.action_dogtraining:
//                Intent intent4=new Intent(RemindersActivity.this,VideoList.class);
//                startActivity(intent4);
//
//                return true;
//            case R.id.action_dogfiles:
//                Intent intent=new Intent(RemindersActivity.this,Profile.class);
//                startActivity(intent);
//                return true;
//            case R.id.action_exit:
//                finish();
//                return true;
//            default:
//                return false;
//        }
//    }

    private int getIdFromPosition(int nC) {
        return (int)mCursorAdapter.getItemId(nC);
    }



  private void fireCustomDialog(final Reminder reminder){
        dialog = new Dialog(this);
        Date date5=new Date();
        Calendar calendar2=getCalendarFromDate(date5);
        int y5=calendar2.get(Calendar.YEAR);
        int m5= calendar2.get(Calendar.MONTH);
        int d5=calendar2.get(Calendar.DAY_OF_MONTH);
        timePickerDialog=new TimePickerDialog(RemindersActivity.this, this, calendar2.get(Calendar.HOUR_OF_DAY),  calendar2.get(Calendar.MINUTE), true);
        datePickerDialog=new DatePickerDialog(RemindersActivity.this, this, y5, m5, d5);
        timePickerDialog.setCanceledOnTouchOutside(true);
        datePickerDialog.setCanceledOnTouchOutside(true);
        timePickerDialog.setTitle("Time");

        datePickerDialog.setTitle("Date");


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom);
        TextView titleView = (TextView) dialog.findViewById(R.id.custom_title);
        final EditText editCustom = (EditText) dialog.findViewById(R.id.custom_edit_reminder);

        Button commitButton = (Button) dialog.findViewById(R.id.custom_button_commit);
        final CheckBox checkBox = (CheckBox) dialog.findViewById(R.id.custom_check_box);
        LinearLayout rootLayout = (LinearLayout) dialog.findViewById(R.id.custom_root_layout);
        final boolean isEditOperation = (reminder != null);
//this is for an edit
        if (isEditOperation){
            //setting the views to match the reminder selected
            Calendar calendar=getCalendarFromDateString(reminder.getDatestring());
            titleView.setText("Edit Reminder");
            checkBox.setChecked(reminder.getImportant() == 1);
            editCustom.setText(reminder.getContent());

            rootLayout.setBackgroundColor(getResources().getColor(R.color.dark_blue));
            timePickerDialog=new TimePickerDialog(RemindersActivity.this, this, calendar.get(Calendar.HOUR_OF_DAY),  calendar.get(Calendar.MINUTE), true);
            datePickerDialog=new DatePickerDialog(RemindersActivity.this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));



        }
        commitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String dates=getDateStringFromInts(hh,mm,dd,MM,y);
                String reminderText = editCustom.getText().toString();
                if (isEditOperation) {
                    Reminder reminderEdited = new Reminder(reminder.getId(),
                            reminderText, dates, checkBox.isChecked() ? 1 : 0);
                    mDbAdapter.updateReminder(reminderEdited);
                    Toast.makeText(getApplicationContext(),"A reminder has Been Set to"+"\t"+dates,Toast.LENGTH_LONG).show();
                    //set the next reminder
                    Date nextala=NotificationScheduler.nextalarm(mDbAdapter.fetchDates());
                    NotificationScheduler.setReminder(RemindersActivity.this,AlarmReceiver.class,nextala);
                    Log.d(MainTag, "A reminder has been set to"+"\t"+nextala);


//this is for new reminder
                } else {
                    mDbAdapter.createReminder(reminderText, dates, checkBox.isChecked());
                    Toast.makeText(getApplicationContext(),"A reminder has Been Set to"+"\t"+dates,Toast.LENGTH_LONG).show();
                   // set the next reminder
                    Date nextala=NotificationScheduler.nextalarm(mDbAdapter.fetchDates());
                    NotificationScheduler.setReminder(RemindersActivity.this,AlarmReceiver.class,nextala);
                    Log.d(MainTag, "A reminder has been set to"+"\t"+nextala);

                }
                mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());

                dialog.dismiss();
            }
        });Button buttonCancel = (Button) dialog.findViewById(R.id.custom_button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        datePickerDialog.show();

    }


//a method to get a date in string format from integers from the date and time pickers
    public String getDateStringFromInts(int hh,int mm,int dd,int MM,int y){
        String h;
        String m;
        String d;
        String M;

        if( hh<10){
            h="0"+hh;
        }
        else {
            h= String.valueOf(hh);
        }
        if( mm<10){
            m="0"+mm;
        }
        else {
            m= String.valueOf(mm);
        }
        if( dd<10){
            d="0"+dd;
        }
        else {
            d= String.valueOf(dd);
        }
        if( MM<10){
            M="0"+MM;
        }
        else {
            M= String.valueOf(MM);
        }

     String datestring=h+":"+m+"  "+d+"/"+M+"/"+y;
        return datestring;
    }
    // a method for getting a corresponding calendar from a date in string form
    public Calendar getCalendarFromDateString(String datestring){
        DateFormat df=new SimpleDateFormat("HH:mm  dd/MM/yyyy");
        Date date = null;
        try {
           date=df.parse(datestring);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        return calendar;

    }
    // a method for getting a corresponding calendar from a date in Date form
    public Calendar getCalendarFromDate(Date date){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        return calendar;

    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        y=i;
        MM= i1+1;
        dd=i2;
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        hh=i;
        mm=i1;
        dialog.show();
    }



}