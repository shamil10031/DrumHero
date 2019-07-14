package com.ShomazzApp.drumhero.game;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.ShomazzApp.drumhero.utils.MySurfaceView;

public class Note implements IUpdatable, IDrawable {

    public boolean isDead = false;
    public static final int maxWidthNoTappers = 280;
    public static final int maxHeightNoTappers = 133;
    public static final int maxWidthDefault = 170;
    public static final int maxHeightDefault = 80;
    private int maxWidth;
    private int maxHeight;
    public static final int minWidthNoTappers = 74;
    public static final int minHeightNoTappers = 34;
    public static final int minWidthDefault = 90;
    public static final int minHeightDefault = 35;
    private int minWidth;
    private int minHeight;
    private int currentWidth;
    private int currentHeight;
    private float widthCoff;
    private float heightCoff;
    private static final float widthCoffNoTappers = 0.160f;
    private static final float heightCoffNoTappers = 0.080f;
    private static final float widthCoffDefault = 0.10f;
    private static final float heightCoffDefault = 0.06f;
    private static final float speedCoffInc = 0.0002f;
    public static final float startSpeedCoffY = 0.32f;
    private float currenSpeedCoffY;
    private float speedCoffX;
    public long startSecond;
    private Game game;
    private RectF noteRect;
    private float x, y = -minHeight;
    private int numberOfLine;
    private boolean noTappersMode;

    public Note(int numberOfLine, int startSecond, Game game) {

        this.game = game;
        this.startSecond = startSecond;
        this.numberOfLine = numberOfLine;
        if(game != null) {
            this.noTappersMode = game.noTappersMode;
        }

        if (!noTappersMode) {
            minWidth = minWidthDefault;
            minHeight = minHeightDefault;
            widthCoff = widthCoffDefault;
            heightCoff = heightCoffDefault;
            maxHeight = maxHeightDefault;
            maxWidth = maxWidthDefault;
            switch (numberOfLine) {
                case 1:
                    x = 800f;
                    speedCoffX = -0.083f;
                    break;
                case 2:
                    x = 910f;
                    speedCoffX = -0.033f;
                    break;
                case 3:
                    x = 1005f;
                    speedCoffX = 0.03f;
                    break;
                case 4:
                    x = 1110f;
                    speedCoffX = 0.0835f;
                    break;
            }
        } else {
            minWidth = minWidthNoTappers;
            minHeight = minHeightNoTappers;
            widthCoff = widthCoffNoTappers;
            heightCoff = heightCoffNoTappers;
            maxHeight = maxHeightNoTappers;
            maxWidth = maxWidthNoTappers;
            switch (numberOfLine) {
                case 1:
                    x = 815f;
                    speedCoffX = -0.175f;
                    break;
                case 2:
                    x = 910f;
                    speedCoffX = -0.048f;
                    break;
                case 3:
                    x = 1015f;
                    speedCoffX = 0.055f;
                    break;
                case 4:
                    x = 1110f;
                    speedCoffX = 0.185f;
                    break;
            }
        }
        currentHeight = minHeight;
        currentWidth = minWidth;
        currenSpeedCoffY = startSpeedCoffY;
    }

    public static int getNoteHeight(boolean noTappersMode) {
        if (noTappersMode) {
            return maxHeightNoTappers;
        } else {
            return maxHeightDefault;
        }
    }

    @Override
    public void update(long deltatime) {
        if (!isDead) {
            currenSpeedCoffY += deltatime * speedCoffInc;
            y += deltatime * currenSpeedCoffY;
            x += deltatime * speedCoffX;
            if (currentHeight < maxHeight) currentHeight += deltatime * heightCoff;
            if (currentWidth < maxWidth) currentWidth += deltatime * widthCoff;
            if (y > DestroyLine.getDestroyLineY(game.noTappersMode) + 2 * maxHeight) {
                game.destroyNote(this, false);
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (!isDead) {
            noteRect = new RectF((x - currentWidth / 2) / MySurfaceView.sizeWidthCoff,
                    (y - currentHeight / 2) / MySurfaceView.sizeHeightCoff,
                    (x + currentWidth / 2) / MySurfaceView.sizeWidthCoff,
                    (y + currentHeight / 2) / MySurfaceView.sizeHeightCoff);
            switch (numberOfLine) {
                case 1:
                    MySurfaceView.drawImage(Game.noteRed, noteRect, canvas);
                    break;
                case 2:
                    MySurfaceView.drawImage(Game.noteGreen, noteRect, canvas);
                    break;
                case 3:
                    MySurfaceView.drawImage(Game.noteYellow, noteRect, canvas);
                    break;
                case 4:
                    MySurfaceView.drawImage(Game.noteBlue, noteRect, canvas);
                    break;
            }
        }
    }

    public void onDestroy() {
        y = -maxHeight;
        isDead = true;
        game.addExplosion(this);
    }

    public static int getNoteWidth(boolean noTappersMode) {
        if (noTappersMode) {
            return maxWidthNoTappers;
        } else {
            return maxWidthDefault;
        }
    }

    public float getNoteX() {
        return x;
    }

    public float getNoteY() {
        return y;
    }

    public int getNumberOfLine() {
        return numberOfLine;
    }

    public long getStartSecond() {
        return startSecond;
    }

    @Override
    public boolean equals(Object o) {
        boolean b = false;
        if (o.getClass() == this.getClass()) {
            Note note = (Note) o;
            if (this.getStartSecond() == note.getStartSecond()
                    && this.getNumberOfLine() == note.getNumberOfLine()) {
                b = true;
            }
        }
        return b;
    }
}