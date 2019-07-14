package com.ShomazzApp.drumhero;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.ShomazzApp.drumhero.fileexplore.FileExplore;
import com.ShomazzApp.drumhero.game.Note;
import com.ShomazzApp.drumhero.game.Song;
import com.ShomazzApp.drumhero.utils.LineToTable;
import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;


public class ConstructorActivity extends Activity {

    public static String drumsOnlyString = "_drums_only";
    public static String musicOnlyString = "_music_only";
    public static boolean isConstructorActivity = false;
    public static String MUSICFOLDER = "/.MUSIC";
    public static String CACHEFOLDER = "/.Cache";
    ArrayList<Note> notes;
    TextView textNotes;
    ProgressDialog pDialog;

    public static void makeAllMidiFilesForGame(String path) {
        makeMidiFileForGame(path, musicOnlyString);
        makeMidiFileForGame(path, drumsOnlyString);
    }

    public static void makeMidiFileForGame(String path, String only) {
        MidiFile midiAll = new MidiFile();
        File fileOnly = new File(Environment.getExternalStorageDirectory()
                .getPath() + MUSICFOLDER + "/" + Song.makeFileNameOfPath(path, drumsOnlyString));
        if (only.equals(musicOnlyString)) {
            fileOnly = new File(Environment.getExternalStorageDirectory()
                    .getPath() + MUSICFOLDER + "/" + Song.makeFileNameOfPath(path, musicOnlyString));
        }
        System.out.println("From makeMidiFileFoGame string with only: " + only);
        System.out.println("From makeMidiFileFoGame fileOnly.getAbsolutePath : " + fileOnly.getAbsolutePath());
        MidiFile midiOnly = new MidiFile();
        try {
            midiAll = new MidiFile(new File(path));
            midiAll.writeToFile(fileOnly);
            midiOnly = new MidiFile(fileOnly);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<MidiTrack> tracks = midiOnly.getTracks();
        for (MidiTrack track : tracks) {
            List<MidiEvent> drums = new ArrayList<>();
            List<MidiEvent> otherEvents = new ArrayList<MidiEvent>();
            Iterator<MidiEvent> iev = track.getEvents().iterator();
            while (iev.hasNext()) {
                MidiEvent event = iev.next();
                if (event instanceof Tempo) {
                    drums.add(event);
                }
                if (event instanceof NoteOn) {
                    NoteOn note = (NoteOn) event;
                    if (note.getChannel() == 9 && note.getVelocity() > 50) {
                        drums.add(note);
                    } else {
                        otherEvents.add(note);
                    }
                }
            }
            if (only.equals(musicOnlyString)) {
                for (MidiEvent event : drums) {
                    if (!(event instanceof Tempo)) {
                        track.removeEvent(event);
                    }
                }
            } else {
                for (MidiEvent event : otherEvents) {
                    track.removeEvent(event);
                }
            }
        }

        try {
            for (MidiTrack track : midiOnly.getTracks())
                track.insertEvent(new NoteOn(0, 9, 4 + 35, 60));
            midiOnly.writeToFile(fileOnly);
            System.out.println("From makeMidiFileForGame gileOnly.getAbsolutePath == " + fileOnly.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<Note> getArrayFromMidi(String path) {
        System.out.println("From getArrayFromMidi path == " + path);
        ArrayList<Note> arr = new ArrayList<Note>();
        float coff = 1;
        MidiFile midi = null;
        try {
            midi = new MidiFile(new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long PPQ = midi.getResolution();

        List<MidiTrack> tracks = midi.getTracks();
        TreeSet<MidiEvent> drums = new TreeSet<MidiEvent>();

        for (MidiTrack track : tracks) {

            Iterator<MidiEvent> iev = track.getEvents().iterator();
            while (iev.hasNext()) {
                MidiEvent event = iev.next();
                if (event instanceof Tempo) {
                    drums.add(event);
                }
                if (event instanceof NoteOn) {
                    NoteOn note = (NoteOn) event;
                    if (note.getChannel() == 9 && note.getVelocity() > 50) {
                        drums.add(note);
                    }
                }
            }
        }
        Iterator<MidiEvent> iev = drums.iterator();
        while (iev.hasNext()) {
            MidiEvent ev = iev.next();
            if (ev instanceof Tempo) {
                coff = 60000 / (((Tempo) ev).getBpm() * PPQ);
                continue;
            }
            NoteOn event = (NoteOn) ev;
            if (event.getType() != event.NOTE_ON)
                continue;
            int startSecond = (int) (event.getTick() * coff);
            Note note = null;
            switch (event.getNoteValue() - 35) {
                case 4:
                    note = new Note(1, startSecond, null);
                    break;
                case 5:
                    note = new Note(1, startSecond, null);
                    break;
                case 23:
                    note = new Note(1, startSecond, null);
                    break;
                case 30:
                    note = new Note(1, startSecond, null);
                    break;
                case 31:
                    note = new Note(1, startSecond, null);
                    break;
                case 38:
                    note = new Note(1, startSecond, null);
                    break;
                case 39:
                    note = new Note(1, startSecond, null);
                    break;
                case 50:
                    note = new Note(1, startSecond, null);
                    break;
                case 1:
                    note = new Note(2, startSecond, null);
                    break;
                case 2:
                    note = new Note(2, startSecond, null);
                    break;
                case 3:
                    note = new Note(2, startSecond, null);
                    break;
                case 8:
                    note = new Note(2, startSecond, null);
                    break;
                case 12:
                    note = new Note(2, startSecond, null);
                    break;
                case 15:
                    note = new Note(2, startSecond, null);
                    break;
                case 25:
                    note = new Note(2, startSecond, null);
                    break;
                case 27:
                    note = new Note(2, startSecond, null);
                    break;
                case 29:
                    note = new Note(2, startSecond, null);
                    break;
                case 36:
                    note = new Note(2, startSecond, null);
                    break;
                case 40:
                    note = new Note(2, startSecond, null);
                    break;
                case 42:
                    note = new Note(2, startSecond, null);
                    break;
                case 43:
                    note = new Note(2, startSecond, null);
                    break;
                case 51:
                    note = new Note(2, startSecond, null);
                    break;
                case 52:
                    note = new Note(2, startSecond, null);
                    break;
                case 6:
                    note = new Note(3, startSecond, null);
                    break;
                case 10:
                    note = new Note(3, startSecond, null);
                    break;
                case 13:
                    note = new Note(3, startSecond, null);
                    break;
                case 21:
                    note = new Note(3, startSecond, null);
                    break;
                case 26:
                    note = new Note(3, startSecond, null);
                    break;
                case 28:
                    note = new Note(3, startSecond, null);
                    break;
                case 37:
                    note = new Note(3, startSecond, null);
                    break;
                case 41:
                    note = new Note(3, startSecond, null);
                    break;
                case 44:
                    note = new Note(3, startSecond, null);
                    break;
                case 7:
                    note = new Note(4, startSecond, null);
                    break;
                case 9:
                    note = new Note(4, startSecond, null);
                    break;
                case 11:
                    note = new Note(4, startSecond, null);
                    break;
                case 14:
                    note = new Note(4, startSecond, null);
                    break;
                case 16:
                    note = new Note(4, startSecond, null);
                    break;
                case 17:
                    note = new Note(4, startSecond, null);
                    break;
                case 18:
                    note = new Note(4, startSecond, null);
                    break;
                case 19:
                    note = new Note(4, startSecond, null);
                    break;
                case 20:
                    note = new Note(4, startSecond, null);
                    break;
                case 22:
                    note = new Note(4, startSecond, null);
                    break;
                case 24:
                    note = new Note(4, startSecond, null);
                    break;
                case 32:
                    note = new Note(4, startSecond, null);
                    break;
                case 33:
                    note = new Note(4, startSecond, null);
                    break;
                case 34:
                    note = new Note(4, startSecond, null);
                    break;
                case 35:
                    note = new Note(4, startSecond, null);
                    break;
                case 45:
                    note = new Note(4, startSecond, null);
                    break;
                case 46:
                    note = new Note(4, startSecond, null);
                    break;
                case 47:
                    note = new Note(4, startSecond, null);
                    break;
                case 48:
                    note = new Note(4, startSecond, null);
                    break;
                case 49:
                    note = new Note(4, startSecond, null);
                    break;
            }
            if (note != null) {
                boolean r = false;
                for (int i = arr.size() - 1; i >= 0; i--) {
                    if (arr.get(i).equals(note)) {
                        System.out.println(arr.get(i).getStartSecond() + "equals(" + note.getStartSecond() + ")");
                        r = true;
                        break;
                    }
                }
                if (!r) {
                    arr.add(note);
                    System.out.println("From get ArrayFromMidi arr.add("+note.getNumberOfLine()+", "+note.getStartSecond()+")");
                }
            }
        }
        return arr;
    }

    public static String[] getNotesStringFromArr(ArrayList<LineToTable> arr) {
        String[] arrString = new String[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            arrString[i] = arr.get(i).getLineString();
        }
        return arrString;
    }

    public static ArrayList<LineToTable> getLinesToTable(ArrayList<Note> arr) {
        ArrayList<LineToTable> arrToTable = new ArrayList<LineToTable>();
        for (int i = 0; i < arr.size(); i++) {
            arrToTable.add(new LineToTable(arr.get(i).getNumberOfLine(),
                    (double) arr.get(i).getStartSecond() / 1000));
        }
        return arrToTable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_constructor);
        textNotes = (TextView) findViewById(R.id.notesTextView);
        isConstructorActivity = true;
        // textNotes.setText(getNotesStringFromArr(getArrayFromMidi("")));
    }

    @Override
    protected void onResume() {
        isConstructorActivity = true;
        super.onResume();
    }

    public void onBrowseFile(View v) {
        Intent intent = new Intent(ConstructorActivity.this, FileExplore.class);
        this.startActivity(intent);
    }

    public void onMainM(View v) {
        Intent intent = new Intent(ConstructorActivity.this, MainMenuActivity.class);
        this.startActivity(intent);
    }

    public void onConvert(View v) {
        MyTask m = new MyTask(FileExplore.constructorFile.getAbsolutePath());
        m.execute();
    }

    public void onConstructorText(View v) {
        String s = textNotes.getText().toString();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboard.setText(s);
        Toast t = Toast.makeText(this, "Copied in bufer", 3500);
        t.show();
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        String path;

        public MyTask(String path) {
            this.path = path;
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ConstructorActivity.this);
            pDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
            pDialog.setMessage("Wait... Song will be ready now...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            notes = getArrayFromMidi(path);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // textNotes.setText(getNotesStringFromArr(notes));
            String n = "";
            String[] ar = getNotesStringFromArr(getLinesToTable(notes));
            for (int i = 0; i < ar.length; i++) {
                n += ar[i] + "\n";
            }
            textNotes.setText(n);
            pDialog.dismiss();
            Toast t = Toast.makeText(getApplicationContext(),
                    "Your song is ready :)", 2500);
            t.show();
            super.onPostExecute(result);
        }
    }
}
