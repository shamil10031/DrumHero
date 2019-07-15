package com.ShomazzApp.drumhero.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.ShomazzApp.drumhero.utils.MySurfaceView;

import java.util.ArrayList;

public class Explosion implements IUpdatable, IDrawable {


    static final int PARTICLE_NUM = 13;
    int picNum;
    Vector tmp = new Vector();
    Paint paint = new Paint();
    private float diameter;
    private float start_diameter;
    private ArrayList<Vector> speeds = new ArrayList<>(PARTICLE_NUM);
    private Vector center;
    private int visibleCrashSecond = 270;
    private long startCrashSecond;
    private Bitmap crash1;
    private Bitmap crash2;
    private RectF crashRect;
    private RectF dst = new RectF();
    private Game game;

    public Explosion(Note from, Game game) {
        this.game = game;

        center = new Vector().set(DestroyLine.getDestroyLineX(from.getNumberOfLine(), game.noTappersMode)
                        + DestroyLine.getDestroyLineWidth(game.noTappersMode) / 2,
                DestroyLine.getDestroyLineY(game.noTappersMode) + from.getNoteHeight(game.noTappersMode) / 2);
        start_diameter = diameter = from.getNoteWidth(game.noTappersMode) / 4;

        //Explosion's generating
        for (int i = 0; i < PARTICLE_NUM; i++) {
            final Vector vector = new Vector((float) Math.random(), (float) Math.random()).sub(0.5f, 0.5f).norm()
                    .scale((float) Math.random());
            speeds.add(vector);

        }
        startCrashSecond = System.currentTimeMillis();
        picNum = from.getNumberOfLine() - 1;
        switch (picNum) {
            case 0:
                crash1 = Game.crash1Red;
                crash2 = Game.crash2Red;
                break;
            case 1:
                crash1 = Game.crash1Green;
                crash2 = Game.crash2Green;
                break;
            case 2:
                crash1 = Game.crash1Yellow;
                crash2 = Game.crash2Yellow;
                break;
            case 3:
                crash1 = Game.crash1Blue;
                crash2 = Game.crash2Blue;
                break;
        }
        crashRect = new RectF((DestroyLine.getDestroyLineX(from.getNumberOfLine(), game.noTappersMode)
                - DestroyLine.getDestroyLineWidth(game.noTappersMode) / 6) / MySurfaceView.sizeWidthCoff,
                (DestroyLine.getDestroyLineY(game.noTappersMode) - DestroyLine.getDestroyLineWidth(game.noTappersMode) / 2) / MySurfaceView.sizeHeightCoff,
                (DestroyLine.getDestroyLineX(from.getNumberOfLine(), game.noTappersMode) + DestroyLine.getDestroyLineWidth(game.noTappersMode)
                + (DestroyLine.getDestroyLineWidth(game.noTappersMode) / 6)) / MySurfaceView.sizeWidthCoff,
                (DestroyLine.getDestroyLineY(game.noTappersMode) + DestroyLine.getDestroyLineHeight(game.noTappersMode))/ MySurfaceView.sizeHeightCoff);
    }

    @Override
    public void draw(Canvas canvas) {

        float distance = (3 * start_diameter - diameter) * 1.5f;
        for (int i = 0; i < PARTICLE_NUM; i++) {
            Vector speed = speeds.get(i);

            tmp.set(speed).scale(distance).add(center);
            dst.set(0, 0,  diameter / MySurfaceView.sizeWidthCoff, diameter / MySurfaceView.sizeWidthCoff);
            dst.offset( tmp.x / MySurfaceView.sizeWidthCoff,  tmp.y / MySurfaceView.sizeHeightCoff);
            dst.offset( -diameter / 2 / MySurfaceView.sizeWidthCoff, -diameter / 2 / MySurfaceView.sizeHeightCoff);

            paint.setColor(Color.WHITE);
            paint.setAlpha((int) (250 * (diameter / start_diameter)));
            canvas.drawBitmap(Game.explosion[picNum], null, dst, paint);
        }

        if (System.currentTimeMillis() - startCrashSecond <= visibleCrashSecond) {
            if (System.currentTimeMillis() - startCrashSecond >= visibleCrashSecond / 2) {
                MySurfaceView.drawImage(crash2, crashRect, canvas);
            } else {
                MySurfaceView.drawImage(crash1, crashRect, canvas);
            }
        }
    }

    @Override
    public void update(long deltaTime) {
        diameter *= 0.99f;
        if (diameter <= 1)
            game.removeObject(this);
    }
}