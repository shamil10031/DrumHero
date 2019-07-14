package com.ShomazzApp.drumhero;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ShomazzApp.drumhero.game.Song;
import com.ShomazzApp.drumhero.R;
import com.ShomazzApp.drumhero.utils.DBManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class WebActivity extends Activity {

    WebView webView;
    static String path;
    RelativeLayout rlay;
    EditText editNameOfSong;
    EditText editNameOfArtist;
    ProgressDialog pDialog;
    View saveSongView;
    Song s;
    File file;
    private String root;
    private static Intent songsActvityIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_web);
        webView = (WebView) findViewById(R.id.webView);
        editNameOfSong = (EditText) findViewById(R.id.editNameOfSong);
        editNameOfArtist = (EditText) findViewById(R.id.editNameOfArtist);
        rlay = (RelativeLayout) findViewById(R.id.webActivityView);
        songsActvityIntent = new Intent(WebActivity.this, SongsActivity.class);
        if (path == null) {
            webView.loadUrl("http://www.midi.ru/base/403/");
        } else {
            webView.loadUrl(path);
        }
        root = Environment.getExternalStorageDirectory().toString();
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        saveSongView = findViewById(R.id.saveSongView);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.endsWith(".mid")) {
                    webView.loadUrl(url);
                } else {
                   // new DownloadFileFromURL().execute(url);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                path = url;
            }
        });
    }

    public void onPushNames(View v) {
        if (editNameOfSong.getText().toString().equals(null)
                || editNameOfArtist.getText().toString().equals(null)
                || editNameOfSong.getText().toString().equals("")
                || editNameOfArtist.getText().toString().equals("")
                || editNameOfSong.getText().toString().equals(" ")
                || editNameOfArtist.getText().toString().equals(" ")) {
            Toast t = Toast.makeText(this, "Fill In The Gaps", Toast.LENGTH_LONG);
            t.show();
        } else {
            s = new Song(editNameOfSong.getText().toString(), editNameOfArtist
                    .getText().toString(), Song.makeTableName(editNameOfSong
                    .getText().toString()
                    + " - "
                    + editNameOfArtist.getText().toString()), file.getName(),
                    SongsActivity.songArrayList.size() + 1);
           // AddToDB toDBTask = new AddToDB(s, file);
            //toDBTask.execute();
            SongsActivity.adapter.notifyDataSetChanged();
            editNameOfSong.setText("");
            editNameOfArtist.setText("");
            System.out.println("From Web newSong.getFileName == " + s.getFileName());
            saveSongView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (saveSongView.getVisibility() == View.VISIBLE) {
            saveSongView.setVisibility(View.GONE);
            webView.setOnTouchListener(null);
        } else {
            super.onBackPressed();
        }
    }

    public static class AddToDB extends AsyncTask<String, Void, Void> {

        ProgressDialog pDialog;
        Song song;
        File file;
        Context context;

        public AddToDB(Song song, File file, Context context) {
            this.song = song;
            this.file = file;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("From Web  Start Adding to db ");
            pDialog = new ProgressDialog(context);
            pDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progress));
            pDialog.setMessage("Your song is adding ... It may take some time...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... arg0) {
            DBManager.getInstance(context).addSongToDB(
                    song,
                    ConstructorActivity.getLinesToTable(ConstructorActivity
                            .getArrayFromMidi(Environment.getExternalStorageDirectory().toString()
                                    + ConstructorActivity.MUSICFOLDER + "/" + song.getFileName())));
            System.out.println("From Web  but get this path " + file.getAbsolutePath());
            ConstructorActivity.makeAllMidiFilesForGame(file.getAbsolutePath());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            System.out.println("From Web added file.getName == " + file.getName());
            System.out.println("From Web added s.getFileName == " + song.getFileName());
            pDialog.dismiss();
            //saveSongView.setVisibility(View.GONE);
            System.out.println("From Web added to db");
            //WebActivity.this.startActivity(songsActvityIntent);
            Toast t = Toast.makeText(context, "Song added", 2000);
            t.show();
        }

    }
}
