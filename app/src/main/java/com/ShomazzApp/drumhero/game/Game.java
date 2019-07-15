package com.ShomazzApp.drumhero.game;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.ShomazzApp.drumhero.ConstructorActivity;
import com.ShomazzApp.drumhero.GameEndedActivity;
import com.ShomazzApp.drumhero.R;
import com.ShomazzApp.drumhero.SongsActivity;
import com.ShomazzApp.drumhero.utils.DBManager;
import com.ShomazzApp.drumhero.utils.MySurfaceView;
import com.ShomazzApp.drumhero.utils.ResourcesHelper;

import java.util.ArrayList;

public class Game implements OnTouchListener {

    public static final int EASY = 1;
    public static final int MEDIUM = 2;
    public static final int HARD = 3;
    public static final int ONLYMUSIC = 1;
    public static final int GAME = 2;
    public static final int ONLYDRUMS = 3;
    private static final float heightOfBtn = 155;
    private static final float widthOfBtn = 160;
    private static final float XofBtn = 1715;
    private static final float YofBtn = 28;
    public String LOG = "Surface";
    public static Bitmap background;
    public static Bitmap btn;
    public static Bitmap btnPause;
    public static Bitmap btnPauseHolded;
    public static Bitmap scoreBarBitmap;
    public static Bitmap desTapperRed;
    public static Bitmap desTapperGreen;
    public static Bitmap desTapperYellow;
    public static Bitmap desTapperBlue;
    public static Bitmap crash1Red;
    public static Bitmap crash2Red;
    public static Bitmap noteRed;
    public static Bitmap crash1Green;
    public static Bitmap crash2Green;
    public static Bitmap noteGreen;
    public static Bitmap crash1Yellow;
    public static Bitmap crash2Yellow;
    public static Bitmap noteYellow;
    public static Bitmap crash1Blue;
    public static Bitmap crash2Blue;
    public static Bitmap noteBlue;
    public static Bitmap desLineRed;
    public static Bitmap touchedDesLineRed;
    public static Bitmap desLineGreen;
    public static Bitmap touchedDesLineGreen;
    public static Bitmap desLineYellow;
    public static Bitmap touchedDesLineYellow;
    public static Bitmap desLineBlue;
    public static Bitmap touchedDesLineBlue;
    public static Bitmap scoreIndicator0;
    public static Bitmap scoreIndicator1;
    public static Bitmap scoreIndicator2;
    public static Bitmap scoreIndicator3;
    public static Bitmap scoreIndicator4;
    public static Bitmap scoreIndicator5;
    public static Bitmap scoreIndicator6;
    public static Bitmap scoreIndicator7;
    public static Bitmap scoreIndicator8;
    public static Bitmap scoreIndicator9;
    public static Bitmap scoreIndicator10;
    public static Bitmap scoreIndicator11;
    public static Bitmap scoreIndicator12;
    public static Bitmap[] explosion;
    private static RectF stopButtonRect;
    private static String titleByName;
    private static String tableName;
    private static String filePath;
    private static int difficulty;
    private static int mode;
    private ArrayList<Object> trashCan = new ArrayList<>();
    public boolean noTappersMode;
    public boolean gameEnded = false;
    public boolean gameStarted = false;
    public boolean playing = false;
    public boolean isMidi;
    private Activity activity;
    private MediaPlayer mpDrumsOnly;
    private MediaPlayer mpMusicOnly;
    private DBManager db;
    private ScoreBar scoreBar;
    private CleanThread cleanThread;
    private Intent intentGameEnded;
    private ArrayList<Note> notes;
    private ArrayList<Note> notesClone;
    private ArrayList<Explosion> explosions = new ArrayList<>();
    private ArrayList<DestroyLine> destroyLines = new ArrayList<>();
    private ArrayList<DestroyTapper> destroyTappers = new ArrayList<>();
    private long startSongSecond;
    private long currentSecond;
    private long pauseSecond;
    private long lastTime = -1;
    private long deltaTime;
    private int duration;
    private int i = 0;

    public void destroy() {
        activity = null;
        clearBitmaps();
    }

    public Game(Activity activity,
            String filePath,
            String tableName,
            String titleByName,
            int difficulty,
            int mode,
            boolean noTappersMode
    ) {
        if (filePath != null && tableName != null && titleByName != null) {
            this.titleByName = titleByName;
            this.tableName = tableName;
            this.filePath = filePath;
        }
        this.difficulty = difficulty;
        this.mode = mode;
        System.out.println("From Game mode == " + mode);
        this.activity = activity;
        this.noTappersMode = noTappersMode;
        notesClone = new ArrayList<>();
        intentGameEnded = new Intent(activity, GameEndedActivity.class);
        isMidi = createMediaPlayers(this.filePath);
        cleanThread = new CleanThread();
        scoreBar = new ScoreBar(this, SongsActivity.beginnerMode);
        for (int i = 0; i < 4; i++) {
            destroyLines.add(new DestroyLine(i + 1, this));
            if (!noTappersMode) {
                destroyTappers.add(new DestroyTapper(i + 1));
            }
        }
        db = new DBManager(activity.getApplicationContext(), this);
        notes = db.getNotesWithDifficulty(tableName, false, difficulty);
        System.out.println("From game notes.size() == " + notes.size());
    }

    public void removeObject(Object obj) {
        trashCan.add(obj);
    }

    public void loadBitmaps() {
        ResourcesHelper resourcesHelper = new ResourcesHelper(activity);
        background = noTappersMode ?
                resourcesHelper.getBitmap("drawable/game_background_no_tappers") :
                resourcesHelper.getBitmap("drawable/game_background");
        scoreIndicator0 = resourcesHelper.getBitmap("drawable/score_indicator_0");
        scoreIndicator1 = resourcesHelper.getBitmap("drawable/score_indicator_1");
        scoreIndicator2 = resourcesHelper.getBitmap("drawable/score_indicator_2");
        scoreIndicator3 = resourcesHelper.getBitmap("drawable/score_indicator_3");
        scoreIndicator4 = resourcesHelper.getBitmap("drawable/score_indicator_4");
        scoreIndicator5 = resourcesHelper.getBitmap("drawable/score_indicator_5");
        scoreIndicator6 = resourcesHelper.getBitmap("drawable/score_indicator_6");
        scoreIndicator7 = resourcesHelper.getBitmap("drawable/score_indicator_7");
        scoreIndicator8 = resourcesHelper.getBitmap("drawable/score_indicator_8");
        scoreIndicator9 = resourcesHelper.getBitmap("drawable/score_indicator_9");
        scoreIndicator10 = resourcesHelper.getBitmap("drawable/score_indicator_10");
        scoreIndicator11 = resourcesHelper.getBitmap("drawable/score_indicator_11");
        scoreIndicator12 = resourcesHelper.getBitmap("drawable/score_indicator_12");
        noteRed = resourcesHelper.getBitmap("drawable/red_note");
        noteBlue = resourcesHelper.getBitmap("drawable/blue_note");
        noteGreen = resourcesHelper.getBitmap("drawable/green_note");
        crash1Red = resourcesHelper.getBitmap("drawable/red_crash1");
        crash2Red = resourcesHelper.getBitmap("drawable/red_crash2");
        noteYellow = resourcesHelper.getBitmap("drawable/yellow_note");
        crash1Blue = resourcesHelper.getBitmap("drawable/blue_crash1");
        crash2Blue = resourcesHelper.getBitmap("drawable/blue_crash2");
        desLineRed = resourcesHelper.getBitmap("drawable/red_desline");
        crash2Green = resourcesHelper.getBitmap("drawable/green_crash2");
        crash1Green = resourcesHelper.getBitmap("drawable/green_crash1");
        desLineBlue = resourcesHelper.getBitmap("drawable/blue_desline");
        crash1Yellow = resourcesHelper.getBitmap("drawable/yellow_crash1");
        desLineGreen = resourcesHelper.getBitmap("drawable/green_desline");
        desTapperRed = resourcesHelper.getBitmap("drawable/red_destapper");
        crash2Yellow = resourcesHelper.getBitmap("drawable/yellow_crash2");
        desTapperBlue = resourcesHelper.getBitmap("drawable/blue_destapper");
        desLineYellow = resourcesHelper.getBitmap("drawable/yellow_desline");
        scoreBarBitmap = resourcesHelper.getBitmap("drawable/score_bar");
        btn = btnPause = resourcesHelper.getBitmap("drawable/stop_button");
        btnPauseHolded = resourcesHelper.getBitmap("drawable/stop_button_holded");
        desTapperGreen = resourcesHelper.getBitmap("drawable/green_destapper");
        desTapperYellow = resourcesHelper.getBitmap("drawable/yellow_destapper");
        touchedDesLineRed = resourcesHelper.getBitmap("drawable/red_desline2");
        touchedDesLineBlue = resourcesHelper.getBitmap("drawable/blue_desline2");
        touchedDesLineGreen = resourcesHelper.getBitmap("drawable/green_desline2");
        touchedDesLineYellow = resourcesHelper.getBitmap("drawable/yellow_desline2");
        explosion = new Bitmap[4];
        explosion[0] = resourcesHelper.getBitmap("drawable/starred");
        explosion[1] = resourcesHelper.getBitmap("drawable/stargreen");
        explosion[2] = resourcesHelper.getBitmap("drawable/staryellow");
        explosion[3] = resourcesHelper.getBitmap("drawable/starblue");
    }

    public void clearBitmaps() {
        background = null;
        btn = null;
        btnPause = null;
        btnPauseHolded = null;
        scoreBarBitmap = null;
        desTapperRed = null;
        desTapperGreen = null;
        desTapperYellow = null;
        desTapperBlue = null;
        crash1Red = null;
        crash2Red = null;
        noteRed = null;
        crash1Green = null;
        crash2Green = null;
        noteGreen = null;
        crash1Yellow = null;
        crash2Yellow = null;
        noteYellow = null;
        crash1Blue = null;
        crash2Blue = null;
        noteBlue = null;
        desLineRed = null;
        touchedDesLineRed = null;
        desLineGreen = null;
        touchedDesLineGreen = null;
        desLineYellow = null;
        touchedDesLineYellow = null;
        desLineBlue = null;
        touchedDesLineBlue = null;
        scoreIndicator0 = null;
        scoreIndicator1 = null;
        scoreIndicator2 = null;
        scoreIndicator3 = null;
        scoreIndicator4 = null;
        scoreIndicator5 = null;
        scoreIndicator6 = null;
        scoreIndicator7 = null;
        scoreIndicator8 = null;
        scoreIndicator9 = null;
        scoreIndicator10 = null;
        scoreIndicator11 = null;
        scoreIndicator12 = null;
        explosion = null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int actionMask = event.getActionMasked();
        int index = event.getActionIndex();
        if (playing) {
            switch (actionMask) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    if (!noTappersMode) {
                        for (int i = 0; i < destroyTappers.size(); i++) {
                            if (destroyTappers.get(i).in(event.getX(index),
                                    event.getY(index))) {
                                destroyLines.get(i).onTouchDown();
                                destroyTappers.get(i).onTouchDown();
                            }
                        }
                    } else {
                        for (int i = 0; i < destroyLines.size(); i++) {
                            if (destroyLines.get(i).in(event.getX(index),
                                    event.getY(index))) {
                                destroyLines.get(i).onTouchDown();
                            }
                        }
                    }
                    if (event.getX(index) > stopButtonRect.left && event.getY(index) >
                            stopButtonRect.top
                            && event.getX(index) < stopButtonRect.right
                            && event.getY(index) < stopButtonRect.bottom) {
                        btn = btnPauseHolded;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!noTappersMode) {
                        for (int i = 0; i < destroyTappers.size(); i++) {
                            if (destroyTappers.get(i).isHolded()
                                    && (event.getY() <= destroyTappers.get(i).getMainY() -
                                    destroyTappers.get(i).getHeight()
                                    || event.getY() >= destroyTappers.get(i).getMainY() +
                                    destroyTappers.get(i).getHeight())) {
                                destroyLines.get(i).onTouchUp();
                                destroyTappers.get(i).onTouchUp();
                            }
                        }
                    } else {
                        for (int i = 0; i < destroyLines.size(); i++) {
                            if (destroyLines.get(i).isHolded()
                                    && (event.getY() <= destroyLines.get(i).getY()
                                    || event.getY() >= destroyLines.get(i).getY() +
                                    destroyLines.get(i).getHeight())) {
                                destroyLines.get(i).onTouchUp();
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    if (!noTappersMode) {
                        for (int i = 0; i < destroyTappers.size(); i++) {
                            if (destroyTappers.get(i).in(event.getX(index),
                                    event.getY(index))) {
                                destroyLines.get(i).onTouchUp();
                                destroyTappers.get(i).onTouchUp();
                            }
                        }
                    } else {
                        for (int i = 0; i < destroyLines.size(); i++) {
                            if (destroyLines.get(i).in(event.getX(index),
                                    event.getY(index))) {
                                destroyLines.get(i).onTouchUp();
                            }
                        }
                    }
                    if (event.getX(index) > stopButtonRect.left && event.getY(index) >
                            stopButtonRect.top
                            && event.getX(index) < stopButtonRect.right
                            && event.getY(index) < stopButtonRect.bottom) {
                        btn = btnPause;
                        activity.onBackPressed();
                    }
                    break;
            }
        }
        return true;
    }

    public void draw(Canvas canvas) {
        drawAllArray(canvas, destroyLines);
        drawAllNotes(canvas, notesClone);
        if (!noTappersMode) {
            drawAllArray(canvas, destroyTappers);
        }
        drawAllArray(canvas, explosions);
        if (mode != ONLYMUSIC) {
            scoreBar.draw(canvas);
        }
        stopButtonRect = new RectF(XofBtn / MySurfaceView.sizeWidthCoff,
                YofBtn / MySurfaceView.sizeHeightCoff,
                (XofBtn + widthOfBtn) / MySurfaceView.sizeWidthCoff, (YofBtn + heightOfBtn)
                / MySurfaceView.sizeHeightCoff);
        MySurfaceView.drawImage(btn, stopButtonRect, canvas);
    }

    public void update() {
        if (playing) {
            if (notes.size() > i
                    && notes.get(i).getNoteY() <= DestroyLine.getDestroyLineY(noTappersMode)) {
                currentSecond = System.currentTimeMillis() - startSongSecond;
                if (!gameStarted &&
                        notes.get(0).getNoteY() - Note.getNoteHeight(noTappersMode) / 2 >=
                                DestroyLine.getDestroyLineY(noTappersMode)
                ) {
                    switch (mode) {
                        case ONLYDRUMS:
                            mpDrumsOnly.start();
                            break;
                        case ONLYMUSIC:
                            mpMusicOnly.start();
                            break;
                        case GAME:
                            mpMusicOnly.start();
                            mpDrumsOnly.start();
                            break;
                    }
                    gameStarted = true;
                }
                while (i < notes.size()
                        && notes.get(i).startSecond <= currentSecond) {
                    notesClone.add(notes.get(i));
                    i++;
                }
            }
            if (lastTime == -1) {
                lastTime = System.currentTimeMillis();
            }
            deltaTime = System.currentTimeMillis() - lastTime;
            if (deltaTime > 0) {
                lastTime = System.currentTimeMillis();
                updateAllNotes(notesClone, deltaTime);
                updateAllArray(explosions);
            }
            // GAME ENDS
            if (gameStarted
                    && System.currentTimeMillis() - startSongSecond >= duration + 1500) {
                playing = false;
                onGameEnded(true);
            }
        }
    }

    public boolean createMediaPlayers(String filePath) {
        isMidi = true;
        mpMusicOnly = MediaPlayer.create(activity,
                Uri.parse(Environment.getExternalStorageDirectory()
                        .getPath() + ConstructorActivity.MUSICFOLDER + "/"
                        + Song.makeFileNameOfPath(filePath,
                        ConstructorActivity.musicOnlyString)));
        mpDrumsOnly = MediaPlayer.create(activity,
                Uri.parse(Environment.getExternalStorageDirectory()
                        .getPath() + ConstructorActivity.MUSICFOLDER + "/"
                        + Song.makeFileNameOfPath(filePath,
                        ConstructorActivity.drumsOnlyString)));
        switch (mode) {
            case GAME:
                try {
                    mpMusicOnly.prepare();
                    mpDrumsOnly.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mpDrumsOnly.getDuration() > mpMusicOnly.getDuration()) {
                    duration = mpDrumsOnly.getDuration();
                    System.out.println("From Game mpDrumsOnly.getDuration() == " +
                            mpDrumsOnly.getDuration());
                } else {
                    duration = mpMusicOnly.getDuration();
                    System.out.println("From Game mpMusicOnly.getDuration() == " +
                            mpMusicOnly.getDuration());
                }
                break;
            case ONLYDRUMS:
                duration = mpDrumsOnly.getDuration();
                try {
                    mpDrumsOnly.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case ONLYMUSIC:
                duration = mpMusicOnly.getDuration();
                try {
                    mpMusicOnly.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        mpDrumsOnly.setVolume(1.1f, 1.1f);
        mpMusicOnly.setVolume(0.9f, 0.9f);
        return isMidi;
    }

    public void drawAllArray(Canvas canvas, ArrayList arr) {
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i) != null && arr.get(i) instanceof IDrawable) {
                ((IDrawable) arr.get(i)).draw(canvas);
            }
        }
    }

    public void updateAllArray(ArrayList arr) {
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i) != null && arr.get(i) instanceof IUpdatable) {
                ((IUpdatable) arr.get(i)).update(deltaTime);
            }
        }
    }

    public void drawAllNotes(Canvas canvas, ArrayList<Note> arr) {
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i) != null) {
                ((IDrawable) arr.get(i)).draw(canvas);
            }
        }
    }

    public void updateAllNotes(ArrayList<Note> arr, long deltaTime) {
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i) != null && arr.get(i) instanceof IUpdatable) {
                ((IUpdatable) arr.get(i)).update(deltaTime);
            }
        }
    }

    public void start() {
        Log.d(LOG, "Game start() called");
        startSongSecond = System.currentTimeMillis();
        playing = true;
        cleanThread.start();
    }

    void destroyNote(Note note, boolean touched) {
        if (gameStarted) {
            if (!touched) {
                notesClone.remove(note);
                scoreBar.resetStrikeScore();
                scoreBar.resetMultiplier();
                if (isMidi && mode != ONLYMUSIC) {
                    mpDrumsOnly.setVolume(0f, 0f);
                }
            } else {
                note.onDestroy();
                removeObject(note);
                if (isMidi && mode != ONLYMUSIC) {
                    mpDrumsOnly.setVolume(1f, 1f);
                }
                if (mode != ONLYMUSIC) {
                    scoreBar.incDestroyNotes();
                    scoreBar.increaseScore(10);
                    scoreBar.increaseStrikeScore();
                    switch (scoreBar.getStrikeScore()) {
                        case 14:
                        case 28:
                        case 42:
                            scoreBar.increaseMultiplier();
                            break;
                    }
                }
            }
        }
    }

    public void onDestroy() {
        Log.d(LOG, "Game onDestroy() called");
        playing = false;
        if (mpDrumsOnly != null) {
            mpDrumsOnly.stop();
            mpDrumsOnly.release();
        }
        if (mpMusicOnly != null) {
            mpMusicOnly.stop();
            mpMusicOnly.release();
        }
        scoreBar.onDestroy();
        notes.clear();
        notesClone.clear();
    }

    public void onResume() {
        Log.d(LOG, "Game onResume() called");
        notesClone.clear();
        playing = true;
        startSongSecond += System.currentTimeMillis() - pauseSecond;
        switch (mode) {
            case GAME:
                mpDrumsOnly.start();
                mpMusicOnly.start();
                break;
            case ONLYDRUMS:
                mpDrumsOnly.start();
                break;
            case ONLYMUSIC:
                mpMusicOnly.start();
                break;
        }

    }

    public void onPause() {
        Log.d(LOG, "Game onPause() called");
        pauseSecond = System.currentTimeMillis();
        playing = false;
        switch (mode) {
            case GAME:
                mpDrumsOnly.pause();
                mpMusicOnly.pause();
                break;
            case ONLYDRUMS:
                mpDrumsOnly.pause();
                break;
            case ONLYMUSIC:
                mpMusicOnly.pause();
                break;
        }
    }

    public void setGameEnded() {
        Log.d(LOG, "Game setGameEnded() called");
        onPause();
        cleanThread.interrupt();
        gameEnded = true;
        new Thread(() -> db.putScore(titleByName, scoreBar.getScore())).start();
    }

    public void onGameEnded(boolean win) {
        Log.d(LOG, "Game onGameEnded() called");
        setGameEnded();
        intentGameEnded.putExtra(activity.getString(R.string.GameEndedIntentWin), win);
        intentGameEnded.putExtra(activity.getString(R.string.GameEndedIntentScore),
                scoreBar.getScore());
        intentGameEnded.putExtra(activity.getString(R.string.GameIntentTitleByName), titleByName);
        intentGameEnded.putExtra(activity.getString(R.string.GameEndedIntentBestScore),
                db.getBestScore(titleByName));

        intentGameEnded.putExtra(activity.getString(R.string.GameIntentSongPath), filePath);
        intentGameEnded.putExtra(activity.getString(R.string.GameIntentTableName), tableName);
        intentGameEnded.putExtra(activity.getString(R.string.GameIntentTitleByName), titleByName);
        intentGameEnded.putExtra(activity.getString(R.string.GameIntentDifficulty), difficulty);

        activity.startActivity(intentGameEnded);
        destroy();
    }

    public void addExplosion(Note note) {
        explosions.add(new Explosion(note, this));
    }

    public ArrayList<Note> getNotesClone() {
        return notesClone;
    }

    public MediaPlayer getDrumsMediaPlayer() {
        return mpDrumsOnly;
    }

    public ScoreBar getScoreBar() {
        return scoreBar;
    }

    //This thread will clean trash
    class CleanThread extends Thread {

        @Override
        public void run() {
            while (true) {
                if (playing) {
                    try {
                        explosions.removeAll(trashCan);
                        notesClone.removeAll(trashCan);
                        trashCan.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}