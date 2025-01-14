package com.ShomazzApp.drumhero;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ShomazzApp.drumhero.game.Game;
import com.ShomazzApp.drumhero.utils.MySurfaceView;

public class GameEndedActivity extends Activity {


    private int tappedButtonId;
    private RelativeLayout rlayGameEnded;
    public static float sizeCoff;
    public static String nothing = "n";
    private static Intent intentMainMenu = new Intent();
    private static Intent intentGameActivity = new Intent();
    private static Button btnRestart, btnMainMenu;
    private ImageView im;
    private TextView tViewBestScore;
    private TextView tViewScore;
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    switch (v.getId()) {
                        case R.id.btnBackToMenu:
                            btnMainMenu.setBackgroundResource(R.drawable.button_main_menu_holded);
                            break;
                        case R.id.btnRestart:
                            btnRestart.setBackgroundResource(R.drawable.button_restart_holded);
                            break;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    tappedButtonId = v.getId();
                    switch (tappedButtonId) {
                        case R.id.btnBackToMenu:
                            btnMainMenu.setBackgroundResource(R.drawable.button_main_menu);
                            startNeededActivity();
                            break;
                        case R.id.btnRestart:
                            btnRestart.setBackgroundResource(R.drawable.button_restart);
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
        setContentView(R.layout.activity_game_ended);
        //  Appodeal.show(this, Appodeal.BANNER_TOP);
        rlayGameEnded = (RelativeLayout) findViewById(R.id.rlayGameEnded);
        im = (ImageView) findViewById(R.id.imageViewOnGameEnded);
        if (getIntent().getBooleanExtra(getString(R.string.GameEndedIntentWin), true)) {
            im.setImageResource(R.drawable.you_are_drumhero);
        } else {
            im.setImageResource(R.drawable.game_over);
        }
        intentMainMenu = new Intent(GameEndedActivity.this, MainMenuActivity.class);
        intentGameActivity = new Intent(GameEndedActivity.this, GameActivity.class);
        tViewScore = (TextView) findViewById(R.id.textViewScore);
        tViewBestScore = (TextView) findViewById(R.id.textViewBestScore);
        btnRestart = (Button) findViewById(R.id.btnRestart);
        btnMainMenu = (Button) findViewById(R.id.btnBackToMenu);
        sizeCoff = MySurfaceView.myDeviceWidth / getResources().getDisplayMetrics().widthPixels;
        System.out.println("From GameEnded sizeCoff = " + MySurfaceView.myDeviceHeight + " / "
                + getResources().getDisplayMetrics().heightPixels + " = " + sizeCoff);
        if (sizeCoff >= 3.0f) {
            sizeCoff /= 1.3f;
        }
        tViewScore.setText(
                getIntent().getExtras().getInt(getString(R.string.GameEndedIntentScore)) + "");
        tViewBestScore.setText(
                getIntent().getExtras().getInt(getString(R.string.GameEndedIntentBestScore)) + "");
        /*tViewScore.setTextSize(17 * getResources().getDisplayMetrics().density);
        tViewBestScore.setTextSize(13 * getResources().getDisplayMetrics().density);*/
        tViewScore.setTextSize(30f / sizeCoff);
        tViewBestScore.setTextSize(26f / sizeCoff);
        btnMainMenu.setOnTouchListener(onTouchListener);
        btnRestart.setOnTouchListener(onTouchListener);
    }

    public void startNeededActivity() {
        if (tappedButtonId == R.id.btnRestart) {
            String songPath = getIntent().getStringExtra(getString(R.string.GameIntentSongPath));
            String tableName = getIntent().getStringExtra(getString(R.string.GameIntentTableName));
            String titleByName = getIntent().getStringExtra(getString(R.string.GameIntentTitleByName));
            int difficulty = getIntent().getIntExtra(getString(R.string.GameIntentDifficulty), Game.EASY);

            intentGameActivity.putExtra(getString(R.string.GameIntentSongPath), songPath);
            intentGameActivity.putExtra(getString(R.string.GameIntentTitleByName), titleByName);
            intentGameActivity.putExtra(getString(R.string.GameIntentTableName), tableName);
            intentGameActivity.putExtra(getString(R.string.GameIntentDifficulty), difficulty);

            startActivity(intentGameActivity);
        } else {
            startActivity(intentMainMenu);
        }
    }

    @Override
    public void onBackPressed() {
    }
}