package com.ShomazzApp.drumhero;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ShomazzApp.drumhero.fileexplore.FileExplore;
import com.ShomazzApp.drumhero.game.Game;
import com.ShomazzApp.drumhero.game.Song;
import com.ShomazzApp.drumhero.utils.AdapterCustomCloud;
import com.ShomazzApp.drumhero.utils.AdapterCustomDefault;
import com.ShomazzApp.drumhero.utils.ResourcesHelper;
import com.ShomazzApp.drumhero.utils.DBManager;
import com.ShomazzApp.drumhero.utils.MySurfaceView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SongsActivity extends Activity {

    private View footerView;
    private static final String site = "http://www.midiworld.com/search/?q=rock";
    public static float sizeCoff;
    public static boolean beginnerMode = true;
    public static ArrayList<Song> songArrayList;
    public AdapterCustomDefault adapter;
    public AdapterCustomCloud adapterWeb;
    private SharedPreferences mSettings;
    private Button btnCloudDownload, btnStorage, btnYes, btnNo, btnBack, btnViewMore;
    private static Intent intentGameActivity = new Intent();
    private static Intent intentAddSong = new Intent();
    private static Intent intentMainMenu = new Intent();
    private static Intent intentSettingsActivity = new Intent();
    private boolean deleteSongsMode = false;
    private RadioGroup radioGroupDifficulty;
    private ImageView songsImageView;
    private int difficulty;
    private int mode = Game.GAME;
    private ArrayList<String> songsString;
    private Song activeSong;
    private CheckBox checkB;
    private RelativeLayout rlayAdd;
    private RelativeLayout rlayStartSong;
    private RelativeLayout rlayDeleteSongs;
    private RelativeLayout rlayDeleteSongsAccept;
    private RelativeLayout rlayBackButton;
    private ListView listView;
    private String root;
    private ProgressDialog pDialog;
    private ArrayList<String> scores;
    private Toast songDeletedToast;
    private static String currentSite;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        footerView = null;
        btnCloudDownload = null;
        btnStorage = null;
        btnYes = null;
        btnNo = null;
        btnBack = null;
        btnViewMore = null;
        songArrayList = null;
        radioGroupDifficulty = null;
        songsImageView = null;
        checkB = null;
        rlayAdd = null;
        rlayStartSong = null;
        rlayDeleteSongsAccept = null;
        rlayBackButton = null;
        listView = null;
        pDialog = null;
        songDeletedToast = null;
    }

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                int position, long id) {
            if (rlayAdd.getVisibility() == View.GONE
                    && rlayStartSong.getVisibility() == View.GONE) {
                if (!deleteSongsMode) {
                    DBManager.getInstance(getApplicationContext());
                    activeSong = songArrayList.get(position);
                    intentGameActivity.putExtra(getString(R.string.GameIntentSongPath),
                            root + ConstructorActivity.MUSICFOLDER + "/" +
                                    activeSong.getFileName());
                    intentGameActivity.putExtra(getString(R.string.GameIntentTableName),
                            activeSong.getTableName());
                    intentGameActivity.putExtra(getString(R.string.GameIntentTitleByName),
                            activeSong.getTitleByName());
                    rlayStartSong.setVisibility(View.VISIBLE);
                    System.out.println("FromOnItemClick root+musicfolder+fileName == " + root +
                            ConstructorActivity.MUSICFOLDER + "/"
                            + activeSong.getFileName());
                } else {
                    activeSong = songArrayList.get(position);
                    rlayDeleteSongsAccept.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    switch (v.getId()) {
                        case R.id.btn_storage_download:
                            if (rlayStartSong.getVisibility() == View.GONE) {
                                btnStorage.setBackgroundResource(R.drawable.ic_folder_holded);
                            }
                            break;
                        case R.id.btn_cloud_download:
                            if (rlayStartSong.getVisibility() == View.GONE) {
                                btnCloudDownload.setBackgroundResource(
                                        R.drawable.ic_cloud_download_holded);
                            }
                            break;
                        case R.id.btnAgreeDeleteSong:
                            btnYes.setBackgroundResource(R.drawable.yes_button_holded);
                            break;
                        case R.id.btnDisAgreeDeleteSong:
                            btnNo.setBackgroundResource(R.drawable.no_button_holded);
                            break;
                        case R.id.btn_back:
                            btnBack.setBackgroundResource(R.drawable.ic_arrow_forwar_holded);
                            break;
                        case R.id.btn_view_more:
                            btnViewMore.setTextColor(0xFF0000FF);
                            break;
                        /*case R.id.btnStartFromSongs:
                            btnStart.setBackgroundResource(R.drawable.start_button_holded);
                            break;*/
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    switch (v.getId()) {
                        case R.id.btn_storage_download:
                            if (rlayStartSong.getVisibility() == View.GONE) {
                                btnStorage.setBackgroundResource(R.drawable.ic_folder_white_24dp);
                                intentAddSong.setClass(SongsActivity.this, FileExplore.class);
                                startActivity(intentAddSong);
                            }
                            break;
                        case R.id.btn_cloud_download:
                            if (rlayStartSong.getVisibility() == View.GONE) {
                                btnCloudDownload.setBackgroundResource(
                                        R.drawable.ic_cloud_download_white_24dp);
                                new LoadSongsFromWebThread().execute();
                                btnCloudDownload.setVisibility(View.GONE);
                                btnStorage.setVisibility(View.GONE);
                                songsImageView.setVisibility(View.GONE);
                                rlayBackButton.setVisibility(View.VISIBLE);
                            }
                            break;
                        case R.id.btn_back:
                            btnBack.setBackgroundResource(R.drawable.ic_arrow_forward_white_36dp);
                            btnCloudDownload.setVisibility(View.VISIBLE);
                            btnStorage.setVisibility(View.VISIBLE);
                            songsImageView.setVisibility(View.VISIBLE);
                            rlayBackButton.setVisibility(View.GONE);
                            if (adapterWeb != null) {
                                adapterWeb.onPause();
                            }
                            currentSite = site;
                            updateListView();
                            break;
                        case R.id.btn_view_more:
                            btnViewMore.setTextColor(0xFFFFFFFF);
                            AddMoreSongFromWeb a = new AddMoreSongFromWeb();
                            a.execute();
                            break;
                        case R.id.btnAgreeDeleteSong:
                            btnYes.setBackgroundResource(R.drawable.yes_button);
                            DBManager.getInstance(getApplicationContext()).deleteSong(activeSong);
                            updateListView();
                            songDeletedToast.show();
                            rlayDeleteSongsAccept.setVisibility(View.GONE);
                            break;
                        case R.id.btnDisAgreeDeleteSong:
                            btnNo.setBackgroundResource(R.drawable.no_button);
                            rlayDeleteSongsAccept.setVisibility(View.GONE);
                            break;
                        /*case R.id.btnStartFromSongs:

                            break;*/
                    }
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_songs);
        footerView = getLayoutInflater().inflate(R.layout.list_item_view_more_song, null);
        btnViewMore = (Button) footerView.findViewById(R.id.btn_view_more);
        currentSite = site;
        checkB = (CheckBox) findViewById(R.id.checkBbox);
        mSettings = getSharedPreferences(SettingsActivity.APP_PREFERENCES, MODE_PRIVATE);
        if (mSettings.contains(SettingsActivity.APP_PREFERENCES_BEGINNERMODE)) {
            beginnerMode = mSettings.getBoolean(SettingsActivity.APP_PREFERENCES_BEGINNERMODE,
                    false);
        } else {
            beginnerMode = false;
        }
        if ((!new File("/data/data/" + getPackageName() +
                "/databases/data.db").exists()) || DBManager.getInstance(getApplicationContext())
                .getAllSongsFromDB() == null) {
            System.out.println("From SongsActivity copyDB() 1");
            copyDB(this);
        }
        checkB.setChecked(beginnerMode);
        mode = Game.GAME;
        difficulty = 10;
        deleteSongsMode = getIntent().getBooleanExtra(getString(R.string.SongsIntentDeleteSongs),
                false);
        System.out.println(
                "From SongsActivity getExtra('deleteSongs', false) == " + deleteSongsMode);
        songDeletedToast = Toast.makeText(this, "Song deleted", Toast.LENGTH_SHORT);
        songsString = new ArrayList<>();
        sizeCoff = MySurfaceView.myDeviceWidth / getResources().getDisplayMetrics().widthPixels;
        System.out.println("From SongsActivity sizeCoff == " + MySurfaceView.myDeviceWidth
                + " / " + getResources().getDisplayMetrics().widthPixels + " == " + sizeCoff);
        rlayAdd = (RelativeLayout) findViewById(R.id.addSongLayOut);
        rlayStartSong = (RelativeLayout) findViewById(R.id.startSongLayout);
        rlayDeleteSongs = (RelativeLayout) findViewById(R.id.deleteSongsLayout);
        rlayDeleteSongsAccept = (RelativeLayout) findViewById(R.id.deleteSongsAcceptLayout);
        rlayBackButton = (RelativeLayout) findViewById(R.id.songsLayoutBackBtn);
        listView = (ListView) findViewById(R.id.songsListView);
        btnStorage = (Button) findViewById(R.id.btn_storage_download);
        btnCloudDownload = (Button) findViewById(R.id.btn_cloud_download);
        btnNo = (Button) findViewById(R.id.btnDisAgreeDeleteSong);
        btnYes = (Button) findViewById(R.id.btnAgreeDeleteSong);
        btnBack = (Button) findViewById(R.id.btn_back);
        songsImageView = (ImageView) findViewById(R.id.songsImage);
        radioGroupDifficulty = (RadioGroup) findViewById(R.id.radioGroupDifficulty);
        intentGameActivity = new Intent(SongsActivity.this, GameActivity.class);
        intentMainMenu = new Intent(this, MainMenuActivity.class);
        intentSettingsActivity = new Intent(this, SettingsActivity.class);
        //btnCloudDownload.startAnimation(anim);
        //btnStorage.startAnimation(anim);
        root = Environment.getExternalStorageDirectory().toString();
        songArrayList = DBManager.getInstance(getApplicationContext())
                .getAllSongsFromDB();
        for (int i = 0; i < songArrayList.size(); i++) {
            songsString.add(songArrayList.get(i).getTitleByName());
        }
        scores = DBManager.getInstance(getApplicationContext())
                .getAllScores(songsString);
        adapter = new AdapterCustomDefault(songsString, scores, this);
        listView.setAdapter(adapter);
        if (deleteSongsMode) {
            rlayDeleteSongs.setVisibility(View.VISIBLE);
        }
        listView.setOnItemClickListener(onItemClickListener);
        new CopySongsAsyncTask(this).execute();
        ConstructorActivity.isConstructorActivity = false;
        radioGroupDifficulty.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case -1:
                    break;
                case R.id.radioButtonEasy:
                    difficulty = Game.EASY;
                    break;
                case R.id.radioButtonMedium:
                    difficulty = Game.MEDIUM;
                    break;
                case R.id.radioButtonHard:
                    difficulty = Game.HARD;
                    break;
            }
            //btnStart.setBackgroundResource(R.drawable.start_button);
                /*if (mode == 10 || difficulty == 10) {
                    notPickedToast.show();
                } else {*/
            if (checkB.isChecked()) {
                beginnerMode = true;
            } else {
                beginnerMode = false;
            }
            intentGameActivity.putExtra(getString(R.string.GameIntentDifficulty), difficulty);
            intentGameActivity.putExtra(getString(R.string.GameIntentMode), mode);
            startActivity(intentGameActivity);
            rlayStartSong.setVisibility(View.GONE);

        });
        /*radioGroupMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case -1:
                        break;
                    case R.id.radioButtonOnlyDrumsMode:
                        mode = Game.ONLYDRUMS;
                        break;
                    case R.id.radioButtonDefaultMode:
                        mode = Game.GAME;
                        break;
                    case R.id.radioButtonOnlyMusicMode:
                        mode = Game.ONLYMUSIC;
                        break;
                }
            }
        });*/
        btnViewMore.setOnTouchListener(onTouchListener);
        btnYes.setOnTouchListener(onTouchListener);
        btnNo.setOnTouchListener(onTouchListener);
        btnStorage.setOnTouchListener(onTouchListener);
        btnCloudDownload.setOnTouchListener(onTouchListener);
        btnBack.setOnTouchListener(onTouchListener);
        //btnStart.setOnTouchListener(onTouchListener);
        //updateListView();
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(SettingsActivity.APP_PREFERENCES_BEGINNERMODE, beginnerMode);
        editor.apply();
        if (adapterWeb != null) {
            adapterWeb.onPause();
        }
    }

    private void updateListView() {
        listView.removeFooterView(footerView);
        songArrayList = DBManager.getInstance(getApplicationContext())
                .getAllSongsFromDB();
        songsString = new ArrayList<>();
        for (int i = 0; i < songArrayList.size(); i++) {
            songsString.add(songArrayList.get(i).getTitleByName());
        }
        scores = DBManager.getInstance(getApplicationContext())
                .getAllScores(songsString);
        adapter = new AdapterCustomDefault(songsString, scores, this);
        listView.setAdapter(adapter);
    }

    public String getArtistFromParsedLi(String liText) {
        String artist = "null";
        Pattern p = Pattern.compile("\\(.+\\)");
        Matcher m = p.matcher(liText);
        if (m.find()) {
            artist = m.group();
        }
        artist = artist.substring(1, artist.length() - 1);
        return artist;
    }

    public String getTitleFromParsedLi(String liText) {
        String title = "null";
        Pattern p = Pattern.compile(".+\\(");
        Matcher m = p.matcher(liText);
        if (m.find()) {
            title = m.group();
        }
        title = title.substring(0, title.length() - 1);
        return title;
    }

    public Song createSongFromParsedLi(String liText) {
        Song song;
        String artist = getArtistFromParsedLi(liText);
        String title = getTitleFromParsedLi(liText);
        song = new Song(title, artist, null, null, 0);
        return song;
    }

    @Override
    public void onStop() {
        System.out.println("From Songs onStop!");
        if (adapterWeb != null) {
            adapterWeb.onStop();
        }
        super.onStop();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        btnStorage.setBackgroundResource(R.drawable.ic_folder_white_24dp);
        btnCloudDownload.setBackgroundResource(R.drawable.ic_cloud_download_white_24dp);
        try {
            songArrayList = DBManager.getInstance(getApplicationContext())
                    .getAllSongsFromDB();
        } catch (SQLiteException e) {
            copyDB(this);
            songArrayList = DBManager.getInstance(getApplicationContext())
                    .getAllSongsFromDB();
            System.out.println("From SongsActivity onRestart SQLiteException e");
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (!deleteSongsMode) {
            if (rlayAdd.getVisibility() == View.VISIBLE ||
                    rlayStartSong.getVisibility() == View.VISIBLE) {
                rlayAdd.setVisibility(View.GONE);
                rlayStartSong.setVisibility(View.GONE);
                btnStorage.setBackgroundResource(R.drawable.ic_folder_white_24dp);
                btnCloudDownload.setBackgroundResource(R.drawable.ic_cloud_download_white_24dp);
            } else {
                this.startActivity(intentMainMenu);
            }
        } else {
            if (rlayDeleteSongsAccept.getVisibility() == View.VISIBLE) {
                rlayDeleteSongsAccept.setVisibility(View.GONE);
            } else {
                startActivity(intentSettingsActivity);
            }
        }
    }

    public void onRlayAdd(View v) {
        btnStorage.setBackgroundResource(R.drawable.ic_folder_white_24dp);
        btnCloudDownload.setBackgroundResource(R.drawable.ic_cloud_download_white_24dp);
        rlayAdd.setVisibility(View.GONE);
    }

    public static void copyDB(Context context) {
        try {
            String destPath = "/data/data/" + context.getPackageName()
                    + "/databases/data.db";
            File data = new File(destPath);
            InputStream in = context.getAssets().open("data.db");
            OutputStream out = new FileOutputStream(data);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.v("TAG", "ioexeption");
            e.printStackTrace();
        }
    }

    public class LoadSongsFromWebThread extends AsyncTask<String, Void, String> {

        Document doc;
        Elements parsedLiList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SongsActivity.this);
            pDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
            pDialog.setMessage("Loading songs from cloud... Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                doc = Jsoup.connect(site).get();
                parsedLiList = doc.select("#page ul:first-of-type li");
                songArrayList.clear();
                for (Element li : parsedLiList) {
                    System.out.println("From Songs liText == " + li.text());
                    Song s = createSongFromParsedLi(li.text());
                    Elements a = li.getElementsByTag("a");
                    s.setFileName(a.attr("href"));
                    songArrayList.add(s);
                }
                adapterWeb = new AdapterCustomCloud(songArrayList, SongsActivity.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            listView.setAdapter(adapterWeb);
            listView.addFooterView(footerView);
        }
    }

    public class AddMoreSongFromWeb extends AsyncTask<String, Void, String> {

        Document doc;
        Element aNext;
        Elements parsedLiList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SongsActivity.this);
            pDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
            pDialog.setMessage("Loading songs from cloud... Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                doc = Jsoup.connect(currentSite).get();
                aNext = doc.select("#page a[href].nomark").last();
                doc = Jsoup.connect(aNext.attr("href")).get();
                currentSite = aNext.attr("href");
                parsedLiList = doc.select("#page ul:first-of-type li");
                for (Element li : parsedLiList) {
                    System.out.println("From Songs liText == " + li.text());
                    Song s = createSongFromParsedLi(li.text());
                    Elements a = li.getElementsByTag("a");
                    s.setFileName(a.attr("href"));
                    songArrayList.add(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            adapterWeb.notifyDataSetChanged();
        }
    }

    static class CopySongsAsyncTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog pDialog;
        private ResourcesHelper resourcesHelper;
        private Drawable progressDrawable;

        CopySongsAsyncTask(Context context) {
            pDialog = new ProgressDialog(context);
            resourcesHelper = new ResourcesHelper(context);
            progressDrawable = context.getResources().getDrawable(R.drawable.progress);
        }

        @Override
        protected void onPreExecute() {
            pDialog.setIndeterminateDrawable(progressDrawable);
            pDialog.setMessage("Loading all songs... It will never repeat");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            resourcesHelper.copySongs();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            resourcesHelper.destroy();
            pDialog.dismiss();
            pDialog = null;
            resourcesHelper = null;
            progressDrawable = null;
            super.onPostExecute(result);
        }
    }
}