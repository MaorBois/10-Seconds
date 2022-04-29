package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ScoresAdapter extends ArrayAdapter<Player>
{
    Context context;
    ArrayList<Player> objects;
    public ScoresAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull ArrayList<Player> objects)
    {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.list_view_layout, parent, false);

        TextView placement_tv = view.findViewById(R.id.placement_tv);
        TextView score_tv = view.findViewById(R.id.score_tv);
        TextView name_tv = view.findViewById(R.id.et_name);

        Player temp = objects.get(position);
        score_tv.setText(String.valueOf(temp.getScore()));
        name_tv.setText(temp.getName());
        placement_tv.setText(""+(position + 1));
        return view;
    }
}
