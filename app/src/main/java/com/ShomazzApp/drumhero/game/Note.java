package com.ShomazzApp.drumhero.game;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;

import com.ShomazzApp.drumhero.utils.Constants;
import com.ShomazzApp.drumhero.utils.MySurfaceView;

public class Note implements IUpdatable, IDrawable {

    public boolean isDead = false;
    private int maxWidth;
    private int maxHeight;
    private int minWidth;
    private int minHeight;
    private float currentWidth;
    private float currentHeight;
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

    public Note(int numberOfLine, int startSecond, Game game) {
        this.game = game;
        this.startSecond = startSecond;
        this.numberOfLine = numberOfLine;
        if (game != null) {
            setupSizes(game.noTappersMode);
        }
    }

    private void setupSizes(boolean isNoTappersMode) {
        if (isNoTappersMode) {
            setupNoTappersModeSizes();
        } else {
            setupDefaultModeSizes();
        }
        currentHeight = minHeight;
        currentWidth = minWidth;
        currenSpeedCoffY = startSpeedCoffY;
    }

    private void setupNoTappersModeSizes() {
        minWidth = Constants.NoTappersMode.NOTE_MIN_WIDTH;
        minHeight = Constants.NoTappersMode.NOTE_MIN_HEIGHT;
        maxHeight = Constants.NoTappersMode.NOTE_MAX_HEIGHT;
        maxWidth = Constants.NoTappersMode.NOTE_MAX_WIDTH;
        widthCoff = widthCoffNoTappers;
        heightCoff = heightCoffNoTappers;
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

    private void setupDefaultModeSizes() {
        minWidth = Constants.TappersMode.NOTE_MIN_WIDTH;
        minHeight = Constants.TappersMode.NOTE_MIN_HEIGHT;
        maxHeight = Constants.TappersMode.NOTE_MAX_HEIGHT;
        maxWidth = Constants.TappersMode.NOTE_MAX_WIDTH;
        widthCoff = widthCoffDefault;
        heightCoff = heightCoffDefault;
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
    }

    @Override
    public void update(long deltatime) {
        if (!isDead) {
            currenSpeedCoffY += deltatime * speedCoffInc;
            y += deltatime * currenSpeedCoffY;
            x += deltatime * speedCoffX;
            if (currentHeight < maxHeight) {
                currentHeight += deltatime * heightCoff;
            }
            if (currentWidth < maxWidth) {
                currentWidth += deltatime * widthCoff;
            }
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

    public static int getNoteHeight(boolean noTappersMode) {
        if (noTappersMode) {
            return Constants.NoTappersMode.NOTE_MAX_HEIGHT;
        } else {
            return Constants.TappersMode.NOTE_MAX_HEIGHT;
        }
    }

    public static int getNoteWidth(boolean noTappersMode) {
        if (noTappersMode) {
            return Constants.NoTappersMode.NOTE_MAX_WIDTH;
        } else {
            return Constants.TappersMode.NOTE_MAX_WIDTH;
        }
    }

    public void onDestroy() {
        y = -maxHeight;
        isDead = true;
        game.addExplosion(this);
        game = null;
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
        if (o != null && o.getClass() == this.getClass()) {
            Note note = (Note) o;
            if (this.getStartSecond() == note.getStartSecond()
                    && this.getNumberOfLine() == note.getNumberOfLine()) {
                b = true;
            }
        }
        return b;
    }
}