package com.ShomazzApp.drumhero;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ShomazzApp.drumhero.game.Game;
import com.ShomazzApp.drumhero.game.GameController;
import com.ShomazzApp.drumhero.utils.MySurfaceView;
import com.ShomazzApp.drumhero.utils.ResourcesHelper;

public class GameActivity extends Activity implements GameController {

    public boolean isNoTappersMode;
    private static int difficulty;
    private static int mode;
    public static String titleByName;
    private static String songPath;
    private static String tableName;
    private int tappedButtonId;
    private Button btnResume;
    private Button btnNewSong;
    private Button btnMainMenu;
    private Button btnEndGame;
    private Game game;
    private RelativeLayout pauseView;
    private MySurfaceView surfaceView;

    @Override
    public void onDestroy() {
        super.onDestroy();
        game = null;
        surfaceView = null;
        btnResume = null;
        btnNewSong = null;
        btnMainMenu = null;
        btnEndGame = null;
        pauseView = null;
    }

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
                    if (game == null) {
                        game = surfaceView.getGame();
                    }
                    switch (v.getId()) {
                        case R.id.btnResume:
                            btnResume.setTextColor(0xFFFFFFFF);
                            pauseView.setVisibility(View.GONE);
                            game.onResume();
                            break;
                        case R.id.btnMainMenu:
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
        initViews();
        setOnTouchListeners();
        isNoTappersMode = getIsNoTappersMode();
        if (!getIntent().getStringExtra(getString(R.string.GameIntentSongPath)).equals(
                GameEndedActivity.nothing)) {
            songPath = getIntent().getStringExtra(getString(R.string.GameIntentSongPath));
            tableName = getIntent().getStringExtra(getString(R.string.GameIntentTableName));
            titleByName = getIntent().getStringExtra(getString(R.string.GameIntentTitleByName));
            difficulty = getIntent().getIntExtra(getString(R.string.GameIntentDifficulty),
                    Game.EASY);
            mode = getIntent().getIntExtra(getString(R.string.GameIntentMode), Game.GAME);
        }
        game = new Game(this, songPath, tableName, titleByName, difficulty, mode, isNoTappersMode);
        setupSurface(game);
        new LoadResourcesAsyncTask(this, surfaceView).execute();
    }

    private boolean getIsNoTappersMode() {
        boolean isNoTappersMode = false;
        SharedPreferences settingsSharedPref = getSharedPreferences(
                SettingsActivity.APP_PREFERENCES,
                Context.MODE_PRIVATE
        );
        if (settingsSharedPref.contains(SettingsActivity.APP_PREFERENCES_GAMEPLAY)) {
            isNoTappersMode = settingsSharedPref.getBoolean(
                    SettingsActivity.APP_PREFERENCES_GAMEPLAY,
                    false);
        }
        return isNoTappersMode;
    }

    @Override
    public void onGameEnded() {
        if (!game.gameEnded) {
            Intent intentGameEnd = new Intent(GameActivity.this, GameEndedActivity.class);
            intentGameEnd.putExtra(getString(R.string.GameEndedIntentWin), false);
            startActivity(intentGameEnd);
        }
        //TODO: putExtra song to have possibility to restart game
    }

    private void setupSurface(Game game) {
        surfaceView.destroyDrawingCache();
        surfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
        surfaceView.setGame(game);
    }

    private void initViews() {
        surfaceView = (MySurfaceView) findViewById(R.id.mySurfaceView);
        pauseView = (RelativeLayout) findViewById(R.id.pauseView);
        btnEndGame = (Button) findViewById(R.id.btnGameEnded);
        btnNewSong = (Button) findViewById(R.id.btnNewSong);
        btnResume = (Button) findViewById(R.id.btnResume);
        btnMainMenu = (Button) findViewById(R.id.btnMainMenu);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnTouchListeners() {
        btnNewSong.setOnTouchListener(onTouchListener);
        btnEndGame.setOnTouchListener(onTouchListener);
        btnMainMenu.setOnTouchListener(onTouchListener);
        btnResume.setOnTouchListener(onTouchListener);
    }

    public void startNeededActivity() {
        pauseView.setVisibility(View.GONE);
        switch (tappedButtonId) {
            case R.id.btnGameEnded:
                game.onGameEnded(false);
                break;
            case R.id.btnMainMenu:
                startActivity(new Intent(GameActivity.this, MainMenuActivity.class));
                break;
            case R.id.btnNewSong:
                startActivity(new Intent(GameActivity.this, SongsActivity.class));
                break;
        }
    }

    @Override
    protected void onStop() {
        onGameEnded();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (game.gameStarted) {
            if (pauseView.getVisibility() == View.GONE) {
                game.onPause();
                pauseView.setVisibility(View.VISIBLE);
            }
        } else {
            Toast waitForBeginToast = Toast.makeText(this, "Wait for game beginning",
                    Toast.LENGTH_LONG);
            waitForBeginToast.show();
        }
    }

    static class LoadResourcesAsyncTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog pDialog;
        private Drawable progressDrawable;
        private MySurfaceView surfaceView;

        LoadResourcesAsyncTask(Context context, MySurfaceView surface) {
            surfaceView = surface;
            pDialog = new ProgressDialog(context);
            progressDrawable = context.getResources().getDrawable(R.drawable.progress);
        }

        @Override
        protected void onPreExecute() {
            pDialog.setIndeterminateDrawable(progressDrawable);
            pDialog.setMessage("Loading resources... Get ready to rock!");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            surfaceView.getGame().loadBitmaps();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            surfaceView.startGame();
            pDialog.dismiss();
            pDialog = null;
            surfaceView = null;
            progressDrawable = null;
            super.onPostExecute(result);
        }
    }

}