package com.ShomazzApp.drumhero;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class SplashActivity extends Activity {

    private static final long SPLASH_VISIBLE_SECOND = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);
        Intent intent = new Intent(this, MainMenuActivity.class);
        Thread logoTimer = new Thread() {
            public void run() {
                try {
                    int logoTimer = 0;
                    while (logoTimer < SPLASH_VISIBLE_SECOND * 1000) {
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
        logoTimer.start();
    }
}
