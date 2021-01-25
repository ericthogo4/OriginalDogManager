package com.chanresti.originaldogmanager;

import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

public class AlarmPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmpage);
//hide action bar:
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //set the reminder text received from the intent to the textbox
        TextView textView=findViewById(R.id.intentstring);
      String   intentsting=getIntent().getExtras().getString("intentstring");
        textView.setText("\n"+"Please Remember To:"+"\n"+intentsting);
        //play the sound of a dog howling
        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.dogsound);
        mediaPlayer.start();
    }

}
