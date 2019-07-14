package com.ShomazzApp.drumhero.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.ShomazzApp.drumhero.game.Game;

public class MySurfaceView extends SurfaceView implements
        SurfaceHolder.Callback {

    public static final String LOG = "GameSurface";
    public static final float myDeviceHeight = 1080.0f;
    public static final float myDeviceWidth = 1920.0f;
    public static float sizeHeightCoff = 0;
    public static float sizeWidthCoff = 0;
    private static Paint paint;
    private Bitmap background;
    private MotorDraw motorDraw;
    private MotorUpdate motorUpdate;
    private Canvas canvas;
    private Game game;
    private SurfaceHolder surHolder;

    public MySurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
        setup();
        Log.d(LOG, "Constructor method called");
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        setup();
        Log.d(LOG, "Constructor method called");
    }

    public static void drawImage(Bitmap bitmap, RectF r, Canvas canvas) {
        Matrix m = new Matrix();
        m.postScale((float) (r.right - r.left) / bitmap.getWidth(),
                (float) (r.bottom - r.top) / bitmap.getHeight());
        m.postTranslate(r.left, r.top);
        canvas.drawBitmap(bitmap, m, paint);
    }

    public void setup() {
        this.paint = new Paint();
        Log.d(LOG, "Setup() called");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(LOG, "SurfaceChanged() called");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (sizeHeightCoff == 0 && sizeWidthCoff == 0) {
            sizeHeightCoff = myDeviceHeight / holder.getSurfaceFrame().bottom;
            sizeWidthCoff = myDeviceWidth / holder.getSurfaceFrame().right;
        }
        motorDraw = new MotorDraw();
        motorUpdate = new MotorUpdate();
        //startGame
        this.setOnTouchListener(game);
        Log.d(LOG, "SurfaceCreated() called");
    }

    public Bitmap createScaledBackgroundBitmap(){
        Bitmap background = Game.background;
        float scale = (float) background.getWidth() / (float) getWidth();
        int newWidth = Math.round(background.getWidth() / scale);
        int newHeight = Math.round(background.getHeight() / scale);
        return Bitmap.createScaledBitmap(background, newWidth, newHeight, true);
    }

    public void startGame() {
        background = createScaledBackgroundBitmap();
        game.start();
        motorUpdate.start();
        motorDraw.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            motorUpdate.turnOff();
            motorDraw.turnOff();
        } catch (Exception e) {
            e.printStackTrace();
        }
        game.onDestroy();
        game = null;
        Log.d(LOG, "SurfaceDestroyed() called");
    }

    public void update() {
        if (game != null && !game.gameEnded) {
            game.update();
        }
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    class MotorUpdate extends Thread {

        private boolean isRunning = true;

        public void run() {
            while (isRunning) {
                update();
            }
        }

        public void turnOff() {
            isRunning = false;
        }

    }

    class MotorDraw extends Thread {

        SurfaceHolder holder = getHolder();
        boolean isRunning = true;

        @Override
        public synchronized void start() {
            super.start();
            Log.d(LOG, "MotorDraw start() called");
        }

        public void turnOff() {
            isRunning = false;
        }

        @Override
        public void run() {
            Log.d(LOG, "MotorDraw run() called");
            while (isRunning) {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    canvas.drawBitmap(background, 0, 0, null);
                    draw(canvas);
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }

        protected void draw(Canvas canvas) {
            if (game != null && !game.gameEnded) {
                game.draw(canvas);
            } else {
                canvas.drawColor(Color.CYAN);
            }
        }
    }
}
