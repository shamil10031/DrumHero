package com.ShomazzApp.drumhero;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ShomazzApp.drumhero.utils.DBManager;
import com.appodeal.ads.Appodeal;

public class SettingsActivity extends Activity {

	public static boolean noTappersMode;
	public static final String APP_PREFERENCES = "settings";
	public static final String APP_PREFERENCES_GAMEPLAY = "gameplay";
	public static final String APP_PREFERENCES_BEGINNERMODE = "beginnermode";
	public static final String DB_VERSION = "dbversion";
	private SharedPreferences mSettings;
	private static Toast toastScoreClear;
	private static Intent songsActivityIntent;
	private static Intent mainMenuIntent;
	private static RelativeLayout rlayAccept;
	private static Button btnYes, btnNo, btnClearScores, btnDeleteSongs;
	private static ImageView gamePlayDefault, gamePlayNoTappers;
	private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					switch (v.getId()) {
						case R.id.gamePlayDefault:
							gamePlayDefault.setImageResource(R.drawable.gameplay_default_holded);
							gamePlayNoTappers.setImageResource(R.drawable.gameplay_no_tappers);
							noTappersMode = false;
							System.out.println("From settings gamePlayDEFAULT!");
							break;
						case R.id.gamePlayNoTappers:
							gamePlayDefault.setImageResource(R.drawable.gameplay_default);
							gamePlayNoTappers.setImageResource(R.drawable.gameplay_no_tappers_holded);
							noTappersMode = true;
							System.out.println("From settings gamePlayNOTAPPERS!");
							break;
						case R.id.btnAgreeClearScores:
							btnYes.setBackgroundResource(R.drawable.yes_button_holded);
							break;
						case R.id.btnDisAgreeClearScores:
							btnNo.setBackgroundResource(R.drawable.no_button_holded);
							break;
						case R.id.btnClearScoreTable:
							if (rlayAccept.getVisibility() != View.VISIBLE) {
								btnClearScores.setBackgroundResource(R.drawable.clear_all_scores_button_holded);
							}
							break;
						case R.id.btnDeleteSongs:
							if (rlayAccept.getVisibility() != View.VISIBLE) {
								btnDeleteSongs.setBackgroundResource(R.drawable.delete_songs_button_holded);
							}
							break;
					}
					break;
				case MotionEvent.ACTION_MOVE:
					break;
				case MotionEvent.ACTION_UP:
					switch (v.getId()) {
						case R.id.btnAgreeClearScores:
							btnYes.setBackgroundResource(R.drawable.yes_button);
							DBManager.getInstance(getApplicationContext()).clearTable(DBManager.SCORE_TABLE_NAME);
							toastScoreClear.show();
							rlayAccept.setVisibility(View.GONE);
							break;
						case R.id.btnDisAgreeClearScores:
							btnNo.setBackgroundResource(R.drawable.no_button);
							rlayAccept.setVisibility(View.GONE);
							break;
						case R.id.btnClearScoreTable:
							btnClearScores.setBackgroundResource(R.drawable.clear_all_scores_button);
							if (rlayAccept.getVisibility() != View.VISIBLE) {
								rlayAccept.setVisibility(View.VISIBLE);
							}
							break;
						case R.id.btnDeleteSongs:
							btnDeleteSongs.setBackgroundResource(R.drawable.delete_songs_button);
							if (rlayAccept.getVisibility() != View.VISIBLE) {
								startActivity(songsActivityIntent);
							}
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
		Appodeal.show(this, Appodeal.BANNER_BOTTOM);
		setContentView(R.layout.activity_settings);
		rlayAccept = (RelativeLayout) findViewById(R.id.clearScoresAcceptLayout);
		gamePlayDefault = (ImageView) findViewById(R.id.gamePlayDefault);
		gamePlayNoTappers = (ImageView) findViewById(R.id.gamePlayNoTappers);
		btnYes = (Button) findViewById(R.id.btnAgreeClearScores);
		btnNo = (Button) findViewById(R.id.btnDisAgreeClearScores);
		btnDeleteSongs = (Button) findViewById(R.id.btnDeleteSongs);
		btnClearScores = (Button) findViewById(R.id.btnClearScoreTable);
		toastScoreClear = Toast.makeText(this, "All scores cleared", Toast.LENGTH_SHORT);
		songsActivityIntent = new Intent(this, SongsActivity.class);
		mainMenuIntent = new Intent(this, MainMenuActivity.class);
		songsActivityIntent.putExtra(getString(R.string.SongsIntentDeleteSongs), true);
		gamePlayDefault.setOnTouchListener(onTouchListener);
		gamePlayNoTappers.setOnTouchListener(onTouchListener);
		btnYes.setOnTouchListener(onTouchListener);
		btnNo.setOnTouchListener(onTouchListener);
		btnClearScores.setOnTouchListener(onTouchListener);
		btnDeleteSongs.setOnTouchListener(onTouchListener);
		mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
		if(mSettings.contains(APP_PREFERENCES_GAMEPLAY)){
			noTappersMode = mSettings.getBoolean(APP_PREFERENCES_GAMEPLAY,false);
		} else {
			noTappersMode = false;
			SharedPreferences.Editor editor = mSettings.edit();
			editor.putBoolean(APP_PREFERENCES_GAMEPLAY, false);
			editor.apply();
		}
		if (noTappersMode){
			gamePlayDefault.setImageResource(R.drawable.gameplay_default);
			gamePlayNoTappers.setImageResource(R.drawable.gameplay_no_tappers_holded);
		} else {
			gamePlayDefault.setImageResource(R.drawable.gameplay_default_holded);
			gamePlayNoTappers.setImageResource(R.drawable.gameplay_no_tappers);
		}
	}

	@Override
	public void onBackPressed() {
		if (rlayAccept.getVisibility() != View.VISIBLE) {
			startActivity(mainMenuIntent);
		} else {
			rlayAccept.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Appodeal.onResume(this, Appodeal.BANNER_BOTTOM);
	}

	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putBoolean(APP_PREFERENCES_GAMEPLAY, noTappersMode);
		editor.apply();
	}
}