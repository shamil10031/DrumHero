package com.ShomazzApp.drumhero.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.ShomazzApp.drumhero.ConstructorActivity;
import com.ShomazzApp.drumhero.R;
import com.ShomazzApp.drumhero.WebActivity;
import com.ShomazzApp.drumhero.game.Song;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class AdapterCustomCloud extends BaseAdapter {

    public static int position;
    public static String activeSongName;
    private static float sizeCoff;
    public MediaPlayer mediaPlayer;
    public int activeSongNumber = -1;
    private ArrayList<Song> songs;
    private LayoutInflater inflater;
    private Context context;
    private PlayFileFromURLTask playFileFromURLTask;

    public AdapterCustomCloud(ArrayList<Song> songs, Context context) {
        this.songs = songs;
        this.inflater = (LayoutInflater) context.
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

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        System.out.println("From AdapterCloud getView(" + position + ");");
        final Holder holder = new Holder();
        final View view;
        view = inflater.inflate(R.layout.list_item_cloud, null);
        holder.songTV = (TextView) view.findViewById(R.id.listViewCloudSong);
        holder.btnPlay = (Button) view.findViewById(R.id.btnPlayFromCloud);
        holder.btnDownload = (Button) view.findViewById(R.id.btnDownloadFromCloud);
        File f = new File(songs.get(position).getFileName());
        if (mediaPlayer == null) {
            System.out.println("GET VIEW : MEDIAPLAYER NULL!!!");
            holder.musicStopped();
        } else {
            System.out.println("GET VIEW : MEDIAPLAYER NOT NULL!!!");
            System.out.println("GET VIEW : activeSongName == " + activeSongName + "; song.filename == "
                    + f.getName());
            System.out.println(activeSongName == f.getName() + "");
            if (activeSongName.equals(f.getName())) {
                try {
//                System.out.println(mediaPlayer.isPlaying());
                    if (mediaPlayer.isPlaying()) {
                        holder.musicPlaying();
                    } else {
                        holder.musicStopped();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                holder.musicStopped();
            }
        }
        holder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdapterCustomCloud.position = position;
                System.out.println("From AdapterCloud active song number == " + activeSongNumber + " position == " + position);
                /*if (activeSongNumber == -1) {
                    activeSongNumber = position;
                    playFileFromURLTask = new PlayFileFromURLTask(AdapterCustomCloud.this);
                    playFileFromURLTask.execute(songs.get(position).getFileName());
                    holder.btnPlay.setBackgroundResource(R.drawable.ic_pause_dark);
                }
                if (position != activeSongNumber) {
                    playFileFromURLTask = new PlayFileFromURLTask(AdapterCustomCloud.this);
                    playFileFromURLTask.execute(songs.get(position).getFileName());
                    holder.btnPlay.setBackgroundResource(R.drawable.ic_pause_dark);
                } else {
                    if (mediaPlayer != null) {
                        System.out.println("MP != null");
                        if (mediaPlayer.isPlaying()) {
                            System.out.println("MP playing");
                            mediaPlayer.pause();
                            holder.btnPlay.setBackgroundResource(R.drawable.ic_play_dark);
                        } else {
                            System.out.println("MP paused");
                            mediaPlayer.start();
                            holder.btnPlay.setBackgroundResource(R.drawable.ic_pause_dark);
                        }
                    }
                }*/
                if (mediaPlayer == null) {
                    playFileFromURLTask = new PlayFileFromURLTask(AdapterCustomCloud.this);
                    playFileFromURLTask.execute(songs.get(position).getFileName());
                } else {
                    if (activeSongNumber == position) {
                        try {
                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.pause();
                            } else {
                                mediaPlayer.start();
                            }
                            notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        playFileFromURLTask = new PlayFileFromURLTask(AdapterCustomCloud.this);
                        playFileFromURLTask.execute(songs.get(position).getFileName());
                    }
                }
            }
        });
        holder.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Song song = songs.get(position);
                AddSongFromURL addTask = new AddSongFromURL(song);
                addTask.execute(song.getFileName());
            }
        });
        sizeCoff = MySurfaceView.myDeviceWidth / context.getResources().getDisplayMetrics().widthPixels;
        System.out.println("From SongsActivity sizeCoff == " + MySurfaceView.myDeviceWidth
                + " / " + context.getResources().getDisplayMetrics().widthPixels + " == " + sizeCoff);
        if (sizeCoff >= 3.0f) {
            sizeCoff /= 1.9f;
        }
        holder.songTV.setTextSize(30f / sizeCoff);
        if (songs != null && songs.size() != 0) {
            holder.songTV.setText(songs.get(position).getTitleByName());
        }
        return view;
    }

    public void onStop() {
        try {
            mediaPlayer.stop();
            mediaPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        File cashFolder = new File(Environment.getExternalStorageDirectory().toString()
                + ConstructorActivity.MUSICFOLDER
                + ConstructorActivity.CACHEFOLDER);
        deleteRecursive(cashFolder);
        cashFolder.mkdir();
    }

    public void onPause() {
        try {
            mediaPlayer.stop();
            mediaPlayer.release();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    public class Holder {
        public Button btnPlay;
        TextView songTV;
        Button btnDownload;

        public void musicStopped() {
            System.out.println("musicStopped");
            btnPlay.setBackgroundResource(R.drawable.ic_play_dark);
        }

        public void musicPlaying() {
            System.out.println("Music playibng");
            btnPlay.setBackgroundResource(R.drawable.ic_pause_dark);
        }

    }

    public class PlayFileFromURLTask extends AsyncTask<String, Void, Void> {

        ProgressDialog pDialog;
        File song;
        String songPath;
        AdapterCustomCloud adapter;

        public PlayFileFromURLTask(AdapterCustomCloud adapter) {
            this.adapter = adapter;
        }

        public String getSongPath() {
            return song.getAbsolutePath();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("From Web Starting download");
            pDialog = new ProgressDialog(context);
            pDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progress));
            pDialog.setMessage("Loading... Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... f_url) {
            int count;
            File cacheFolder = new File(Environment.getExternalStorageDirectory().toString()
                    + ConstructorActivity.MUSICFOLDER
                    + ConstructorActivity.CACHEFOLDER);
            if (!cacheFolder.exists()) {
                cacheFolder.mkdir();
            }
            File f = new File(f_url[0]);
            File f3 = new File(cacheFolder + "/" + f.getName());
            System.out.println(f3.getAbsolutePath());
            if (f3.exists()) {
                System.out.println("EXIST!");
                songPath = f3.getAbsolutePath();
            } else {
                try {
                    System.out.println(f_url + "     saff a  " + f_url[0]);
                    URL url = new URL(f_url[0]);
                    String fileName = java.net.URLDecoder.decode(url.getFile(),
                            "UTF-8").replaceAll("[^A-Za-z0-9_/\\.]", "");
                    song = new File(fileName);
                    System.out.println("filename == " + fileName);
                    URLConnection conection = url.openConnection();
                    conection.connect();
                    int lenghtOfFile = 500000;
                    InputStream input = new BufferedInputStream(url.openStream(),
                            lenghtOfFile);
                    OutputStream output = new FileOutputStream(cacheFolder + "/" + song.getName().replace(".mid", ""));
                    song = new File(cacheFolder + "/" + song.getName().replace(".mid", ""));
                    System.out.println("From Web outputFile.getAbsolutePath == " + song.getAbsolutePath());
                    byte data[] = new byte[lenghtOfFile];
                    while ((count = input.read(data)) != -1) {
                        output.write(data, 0, count);
                    }
                    songPath = song.getAbsolutePath();
                    output.flush();
                    output.close();
                    input.close();
                    System.out.println(song.getName() + "    +    " + song.getAbsolutePath());
                } catch (Exception e) {
                    Log.e("Error: ", e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (mediaPlayer != null) {
                try {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            mediaPlayer = MediaPlayer.create(context, Uri.parse(songPath));
            mediaPlayer.start();
            File f = new File(songPath);
            AdapterCustomCloud.activeSongName = f.getName();
            adapter.notifyDataSetChanged();
            adapter.activeSongNumber = position;
            pDialog.dismiss();
        }
    }

    public class AddSongFromURL extends AsyncTask<String, Void, Void> {

        public File file;
        ProgressDialog pDialog;
        Song song;

        public AddSongFromURL(Song song) {
            this.song = song;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("From Web Starting download");
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            pDialog = new ProgressDialog(context);
            pDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progress));
            pDialog.setMessage("Loading... Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                System.out.println("From Web Downloading");
                String fileName = java.net.URLDecoder.decode(url.getFile(),
                        "UTF-8").replaceAll("[^A-Za-z0-9_/\\.]", "");
                file = new File(fileName);
                System.out.println("From Web filename == " + fileName);
                URLConnection conection = url.openConnection();
                conection.connect();
                int lenghtOfFile = 500000;
                InputStream input = new BufferedInputStream(url.openStream(),
                        lenghtOfFile);
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().toString()
                        + ConstructorActivity.MUSICFOLDER
                        + "/" + file.getName().replace(".mid", ""));
                file = new File(Environment.getExternalStorageDirectory().toString()
                        + ConstructorActivity.MUSICFOLDER
                        + "/" + file.getName().replace(".mid", ""));
                System.out.println("From Web outputFile.getAbsolutePath == " + file.getAbsolutePath());
                byte data[] = new byte[lenghtOfFile];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            pDialog.dismiss();
            song.setFileName(file.getName());
            song.setTableName(Song.makeTableName(song.getTitleByName()));
            WebActivity.AddToDB addToDBTask = new WebActivity.AddToDB(song, file, context);
            addToDBTask.execute();
        }
    }
}
