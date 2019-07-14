package com.ShomazzApp.drumhero;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ShomazzApp.drumhero.game.Game;
import com.ShomazzApp.drumhero.utils.MySurfaceView;

public class GameActivity extends Activity {

    private static final String log = "ADDDA";
    private static final String sharedKey = "count";
    public static boolean noTappersMode;
    public static View pauseView;
    public static String titleByName;
    public static Intent intentGameEnd = new Intent();
    private static RelativeLayout layoutGame;
    private static int difficulty;
    private static int mode;
    private static String songPath;
    private static String tableName;
    private static Intent intentSongs = new Intent();
    private static Intent intentMainMenu = new Intent();
    private SharedPreferences sharedPreferences;
    private int tappedButtonId;
    private SharedPreferences mSettings;
    private Button btnResume, btnNewSong, btnMainMenu, btnEndGame;
    private Game game;
    private RelativeLayout pause;
    private Toast waitForBeginToast;
    private MySurfaceView mySV;
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    switch (v.getId()) {
                        case R.id.btnResume:
                            btnResume.setTextColor(0xFF0000FF);
                            break;
                        case R.id.btnGameEnded:
                            btnEndGame.setTextColor(0xFF0000FF);
                            break;
                        case R.id.btnMainMenu:
                            btnMainMenu.setTextColor(0xFF0000FF);
                            break;
                        case R.id.btnNewSong:
                            btnNewSong.setTextColor(0xFF0000FF);
                            break;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    tappedButtonId = v.getId();
                    if (game == null) game = mySV.getGame();
                    switch (v.getId()) {
                        case R.id.btnResume:
                            btnResume.setTextColor(0xFFFFFFFF);
                            pauseView.setVisibility(View.GONE);
                            game.onResume();
                            break;
                        case R.id.btnMainMenu:
                            Log.d(log, "onMainMenu");
                            game.setGameEnded();
                            btnMainMenu.setTextColor(0xFFFFFFFF);
                            startNeededActivity();
                            break;
                        case R.id.btnGameEnded:
                            game.setGameEnded();
                            btnEndGame.setTextColor(0xFFFFFFFF);
                            startNeededActivity();
                            break;
                        case R.id.btnNewSong:
                            game.setGameEnded();
                            btnNewSong.setTextColor(0xFFFFFFFF);
                            startNeededActivity();
                            break;
                    }
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game);
        sharedPreferences = getSharedPreferences(
                getString(R.string.shar), Context.MODE_PRIVATE);
        mSettings = getSharedPreferences(SettingsActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(SettingsActivity.APP_PREFERENCES_GAMEPLAY)) {
            noTappersMode = mSettings.getBoolean(SettingsActivity.APP_PREFERENCES_GAMEPLAY, false);
        } else noTappersMode = false;
        mySV = (MySurfaceView) findViewById(R.id.mySurfaceView);
        mySV.destroyDrawingCache();
        if (!getIntent().getStringExtra(getString(R.string.GameIntentSongPath)).equals(GameEndedActivity.nothing)) {
            songPath = getIntent().getStringExtra(getString(R.string.GameIntentSongPath));
            tableName = getIntent().getStringExtra(getString(R.string.GameIntentTableName));
            titleByName = getIntent().getStringExtra(getString(R.string.GameIntentTitleByName));
            difficulty = getIntent().getIntExtra(getString(R.string.GameIntentDifficulty), Game.EASY);
            mode = getIntent().getIntExtra(getString(R.string.GameIntentMode), Game.GAME);
        }
        pause = (RelativeLayout) findViewById(R.id.pauseView);
        game = new Game(this, songPath, tableName, titleByName, difficulty, mode, noTappersMode);
        intentSongs = new Intent(GameActivity.this, SongsActivity.class);
        intentMainMenu = new Intent(GameActivity.this, MainMenuActivity.class);
        intentGameEnd = new Intent(GameActivity.this, GameEndedActivity.class);
        mySV.getHolder().setFormat(PixelFormat.RGBA_8888);
        mySV.setGame(game);
        btnEndGame = (Button) findViewById(R.id.btnGameEnded);
        btnNewSong = (Button) findViewById(R.id.btnNewSong);
        btnResume = (Button) findViewById(R.id.btnResume);
        btnMainMenu = (Button) findViewById(R.id.btnMainMenu);
        layoutGame = (RelativeLayout) findViewById(R.id.layout_game);
        pauseView = findViewById(R.id.pauseView);
        waitForBeginToast = Toast.makeText(this, "Wait for game beginning", Toast.LENGTH_LONG);
        btnNewSong.setOnTouchListener(onTouchListener);
        btnEndGame.setOnTouchListener(onTouchListener);
        btnMainMenu.setOnTouchListener(onTouchListener);
        btnResume.setOnTouchListener(onTouchListener);
    }

    public int getCount() {
        int count = -1;
        if (sharedPreferences.contains(sharedKey))
            count = sharedPreferences.getInt(sharedKey, count);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putInt(sharedKey, ++count);
        ed.commit();
        Log.d(log, "Count == " + count);
        return count;
    }

    public void showAddOrStartActivity() {
        /*if (Appodeal.isLoaded(Appodeal.NON_SKIPPABLE_VIDEO))
            if (getCount() % 3 == 0)
                Appodeal.show(GameActivity.this, Appodeal.NON_SKIPPABLE_VIDEO);
            else startNeededActivity();
        else*/
        startNeededActivity();
    }

    public void startNeededActivity() {
        pauseView.setVisibility(View.GONE);
        switch (tappedButtonId) {
            case R.id.btnGameEnded:
                game.onGameEnded(false);
                break;
            case R.id.btnMainMenu:
                startActivity(intentMainMenu);
                break;
            case R.id.btnNewSong:
                startActivity(intentSongs);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (game.gameStarted) {
            if (pause.getVisibility() == View.GONE) {
                game.onPause();
                pause.setVisibility(View.VISIBLE);
            }
        } else {
            waitForBeginToast.show();
        }
    }

    @Override
    protected void onStop() {
        Log.d("GameActivity", "onStop() called");
        if (mySV != null) {
            Log.d("GameActivity", "set gone visibility called");
            mySV.setVisibility(View.GONE);
        }
        if (!game.gameEnded) {
            intentGameEnd.putExtra(getString(R.string.GameEndedIntentWin), false);
            startActivity(intentGameEnd);
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GameActivity", "onResume() called");
        if (mySV != null) {
            Log.d("GameActivity", "set gone visibility called");
            mySV.setVisibility(View.VISIBLE);
        }
    }
}