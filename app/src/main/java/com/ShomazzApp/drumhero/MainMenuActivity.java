package com.ShomazzApp.drumhero;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.ShomazzApp.drumhero.R;
import com.ShomazzApp.drumhero.utils.DBManager;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.utils.PermissionsHelper;
import com.mopub.common.util.Utils;

public class MainMenuActivity extends Activity {

	private static Intent intentSongsActivity, intentSettingsActivity;
	private Button btnStart, btnSettings;
	private Animation anim0;
	boolean tappedOnBack = false;
	private SharedPreferences mSettings;

	private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					switch (v.getId()) {
						case R.id.btnStart:
							btnStart.setBackgroundResource(R.drawable.start_button_holded);
							break;
						case R.id.btnSettings:
							btnSettings.setBackgroundResource(R.drawable.settings_button_holded);
							break;
					}
					break;
				case MotionEvent.ACTION_MOVE:
					break;
				case MotionEvent.ACTION_CANCEL:
					switch (v.getId()) {
						case R.id.btnStart:
							btnStart.setBackgroundResource(R.drawable.start_button);
							break;
						case R.id.btnSettings:
							btnSettings.setBackgroundResource(R.drawable.settings_button);
							break;
					}
					break;
				case MotionEvent.ACTION_UP:
					switch (v.getId()) {
						case R.id.btnStart:
							intentSongsActivity.putExtra(getString(R.string.SongsIntentDeleteSongs), false);
							startActivity(intentSongsActivity);
							btnStart.setBackgroundResource(R.drawable.start_button);
							break;
						case R.id.btnSettings:
							btnSettings.setBackgroundResource(R.drawable.settings_button);
							startActivity(intentSettingsActivity);
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
		setContentView(R.layout.activity_menu);
		Appodeal.setTesting(false);
		Appodeal.show(this, Appodeal.BANNER_BOTTOM);
		Appodeal.requestAndroidMPermissions(this, new PermissionsHelper.AppodealPermissionCallbacks(){
			@Override
			public void writeExternalStorageResponse(int result) {
			}

			@Override
			public void accessCoarseLocationResponse(int result) {
			}
		});
		btnStart = (Button) findViewById(R.id.btnStart);
		btnSettings = (Button) findViewById(R.id.btnSettings);
		anim0 = AnimationUtils.loadAnimation(this, R.anim.anim0);
		btnStart.startAnimation(anim0);
		btnSettings.startAnimation(anim0);
		tappedOnBack = false;
		ConstructorActivity.isConstructorActivity = false;
		intentSongsActivity = new Intent(this, SongsActivity.class);
		intentSettingsActivity = new Intent(this, SettingsActivity.class);
		btnSettings.setOnTouchListener(onTouchListener);
		btnStart.setOnTouchListener(onTouchListener);
		mSettings = getSharedPreferences(SettingsActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
		if(mSettings.contains(SettingsActivity.DB_VERSION)){
			if (mSettings.getInt(SettingsActivity.DB_VERSION, 0) < DBManager.DB_VERSION){
				SongsActivity.copyDB(this);
			}
		} else {
			SongsActivity.copyDB(this);
			SharedPreferences.Editor editor = mSettings.edit();
			editor.putInt(SettingsActivity.DB_VERSION, DBManager.DB_VERSION);
			editor.apply();
		}
	}


	public void onBLABLA(View v) {
		Intent intent = new Intent(MainMenuActivity.this, ConstructorActivity.class);
		//startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Appodeal.onResume(this, Appodeal.BANNER_BOTTOM);
	}

	@Override
	public void onBackPressed() {
		if (!tappedOnBack == true) {
			tappedOnBack = true;
			Toast t = Toast.makeText(this, "Tap one more time to exit app", Toast.LENGTH_LONG);
			t.show();
		} else {
			moveTaskToBack(true);
			tappedOnBack = false;
		}
	}
}