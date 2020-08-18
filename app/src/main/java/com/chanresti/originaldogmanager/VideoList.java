package com.chanresti.originaldogmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public  class VideoList extends AppCompatActivity {
    List<YVideo> videoList;
    ListView listView;
    public static Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videolist);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        intent=new Intent(VideoList.this,YTPlayer.class);
        videoList=new ArrayList<>();
        listView=findViewById(R.id.listView3);
       listView.setDivider(null);

        videoList.add(new YVideo(R.drawable.introp,"GrFSqOyp3nU","Introduction to Dog Training"));
        videoList.add(new YVideo(R.drawable.fetchp,"hpmrfOOMjyA","Teach Your Dog How to Fetch"));
        videoList.add(new YVideo(R.drawable.inp,"VmtI1y90HrI","Teach Your Dog to get into the Crate"));
        videoList.add(new YVideo(R.drawable.sitp,"5-MA-rGbt9k","Teach Your Dog  To Sit When Told"));
        videoList.add(new YVideo(R.drawable.stayp,"vYRlD8uB1yg","Teach Your Dog How to Stay"));

        MyListAdapter adapter=new MyListAdapter(VideoList.this,R.layout.custom_listlayout,videoList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                YVideo hero=videoList.get(i);

                intent.putExtra("Activeimage", hero.getVideo());
                startActivity(intent);
            }
        });


    }


}
