package com.ShomazzApp.drumhero.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private static Context context;
    private static Paint paint;
    private static Bitmap background;
    private Motor motorDraw;
    private MotorUpdate motorUpdate;
    private Canvas canvas;
    private Game game;
    private SurfaceHolder surHolder;

    public MySurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
        setup(context);
        Log.d(LOG, "Constructor method called");
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        setup(context);
        Log.d(LOG, "Constructor method called");
    }

    public static void drawImage(Bitmap bitmap, RectF r, Canvas canvas) {
        Matrix m = new Matrix();
        m.postScale((float) (r.right - r.left) / bitmap.getWidth(),
                (float) (r.bottom - r.top) / bitmap.getHeight());
        m.postTranslate(r.left, r.top);
        canvas.drawBitmap(bitmap, m, paint);
    }

    public static Bitmap getBitmap(String name) {
        int id = context.getResources().getIdentifier(name, null,
                context.getPackageName());
        Bitmap bitmap = BitmapFactory
                .decodeResource(context.getResources(), id);
        return bitmap;
    }

    public SurfaceHolder getSurfaceHolder() {
        return surHolder;
    }

    public void setup(Context context) {
        this.game = null;
        this.context = context;
        this.paint = new Paint();
        Log.d(LOG, "Setup() called");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.d(LOG, "SurfaceChanged() called");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (sizeHeightCoff == 0 && sizeWidthCoff == 0) {
            sizeHeightCoff = myDeviceHeight / holder.getSurfaceFrame().bottom;
            sizeWidthCoff = myDeviceWidth / holder.getSurfaceFrame().right;
        }
        surHolder = holder;
        Bitmap d = Game.background;
        float scale = (float) d.getWidth() / (float) getWidth();
        int newWidth = Math.round(d.getWidth() / scale);
        int newHeight = Math.round(d.getHeight() / scale);
        background = Bitmap.createScaledBitmap(d, newWidth, newHeight, true);
        motorDraw = new Motor();
        motorUpdate = new MotorUpdate();
        startGame();
        this.setOnTouchListener(game);
        Log.d(LOG, "SurfaceCreated() called");
    }

    private void startGame() {
        game.start();
        motorUpdate.start();
        motorDraw.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            motorUpdate.turnOff();
            motorDraw.turnOff();
        } catch (Exception e){
            e.printStackTrace();
        }
        game.onDestroy();
        game = null;
        Log.d(LOG, "SurfaceDestroyed() called");
    }

    public void update() {
        if (game != null)
            game.update();
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
            while (isRunning)
                update();
        }

        public void turnOff() {
            isRunning = false;
        }

    }

    class Motor extends Thread {
        SurfaceHolder holder = getHolder();
        boolean isRunning = true;

        @Override
        public synchronized void start() {
            super.start();
            Log.d(LOG, "Motor start() called");
        }

        public void turnOff() {
            isRunning = false;
        }

        public void run() {
            Log.d(LOG, "Motor run() called");
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
            if (game != null) {
                game.draw(canvas);
            } else
                canvas.drawColor(Color.CYAN);
        }
    }
}
