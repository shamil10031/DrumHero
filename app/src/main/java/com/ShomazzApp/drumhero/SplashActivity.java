package com.ShomazzApp.drumhero;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;

import com.ShomazzApp.drumhero.utils.RoboErrorReporter;
import com.ShomazzApp.drumhero.R;
import com.appodeal.ads.Appodeal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		RoboErrorReporter.bindReporter(this.getApplicationContext());
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		final Intent intent = new Intent(this, MainMenuActivity.class);
        Thread logoTimer = new Thread() {
			public void run() {
				try {
					int logoTimer = 0;
					while (logoTimer < 3000) {
						sleep(100);
						logoTimer = logoTimer + 100;
					}
					startActivity(intent);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					finish();
				}
			}
		};
		String appKey = "60d00add4dd8e9387cd36db8a856be0ae4f4f1d410ebc0f0";
		Appodeal.initialize(this, appKey, Appodeal.BANNER_BOTTOM );
		Appodeal.show(this, Appodeal.BANNER_BOTTOM);
		logoTimer.start();
	}
}
