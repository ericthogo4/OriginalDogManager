package com.chanresti.originaldogmanager;

/**
 * Created by Mwangi on 14/03/2018.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static com.chanresti.originaldogmanager.VideoList.intent;

//an adapter for the  VideoList In the dog-training activity
 public class MyListAdapter extends   ArrayAdapter<YVideo> {


    List<YVideo> videoList;


    //activity context
    Context context;

    //the layout resource file for the list items
    int resource;

    //constructor initializing the values
    public MyListAdapter(Context context, int resource, List<YVideo> videoList) {
        super(context, resource, videoList);
        this.context = context;
        this.resource = resource;
        this.videoList = videoList;
    }

    //this will return the ListView Item as a View
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_listlayout, null, false);
      ImageView imageView = view.findViewById(R.id.imageView);
      TextView content=view.findViewById(R.id.tag);
      Button play=view.findViewById(R.id.playbutton);
        final YVideo video = videoList.get(position);
        content.setText(video.getTag());
        imageView.setImageDrawable(context.getResources().getDrawable(video.getImage()));
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


              intent.putExtra("Activeimage", video.getVideo());
                context.startActivity(intent);
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                intent.putExtra("Activeimage", video.getVideo());
                context.startActivity(intent);
            }
        });
        return view;
    }


}
