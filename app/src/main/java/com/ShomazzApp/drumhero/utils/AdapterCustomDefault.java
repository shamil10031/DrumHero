package com.ShomazzApp.drumhero.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ShomazzApp.drumhero.R;

import java.util.ArrayList;

public class AdapterCustomDefault extends BaseAdapter {

    private static float sizeCoff;
    private ArrayList<String> songs;
    private ArrayList<String> bestScores;
    private LayoutInflater inflater;
    private Context context;

    public AdapterCustomDefault(ArrayList<String> songs, ArrayList<String> bestScores, Context context){
        this.songs = songs;
        this.bestScores = bestScores;
        this.inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView songTV;
        TextView bestScoreTV;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder ();
        View view;
        view = inflater.inflate(R.layout.list_item_default, null);
        holder.songTV=(TextView) view.findViewById(R.id.listViewDefaultSong);
        holder.bestScoreTV=(TextView) view.findViewById(R.id.listViewScore);
        sizeCoff = MySurfaceView.myDeviceWidth / context.getResources().getDisplayMetrics().widthPixels;
        /*System.out.println("From AdapterCustomDef sizeCoff == " + MySurfaceView.myDeviceWidth
                + " / " + context.getResources().getDisplayMetrics().widthPixels + " == " + sizeCoff);*/
        if (sizeCoff >= 3.0f){
            sizeCoff /= 1.9f;
        }
        holder.bestScoreTV.setTextSize(30f / sizeCoff);
        holder.songTV.setTextSize(30f / sizeCoff);
        if (songs != null && songs.size() != 0) {
            holder.songTV.setText(songs.get(position));
            holder.bestScoreTV.setText(bestScores.get(position));
        }
        return view;
    }
}
