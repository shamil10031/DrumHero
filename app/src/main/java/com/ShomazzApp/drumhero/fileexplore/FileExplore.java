package com.ShomazzApp.drumhero.fileexplore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ShomazzApp.drumhero.ConstructorActivity;
import com.ShomazzApp.drumhero.R;
import com.ShomazzApp.drumhero.SongsActivity;
import com.ShomazzApp.drumhero.game.Song;
import com.ShomazzApp.drumhero.utils.DBManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class FileExplore extends Activity {

    private static final String TAG = "F_PATH";
    private static final int DIALOG_LOAD_FILE = 1000;
    public static File constructorFile;
    Intent inn;
    ProgressDialog pDialog;
    RelativeLayout rlay;
    EditText editNameOfSongFromStorage;
    EditText editNameOfArtistFromStorage;
    ArrayList<String> str = new ArrayList<String>();
    Toast t;
    ListAdapter adapter;
    private Boolean firstLvl = true;
    private Item[] fileList;
    private File path = new File(Environment.getExternalStorageDirectory() + "");
    private String chosenFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_file_explore);
        loadFileList();
        showDialog(DIALOG_LOAD_FILE);
        editNameOfSongFromStorage = (EditText) findViewById(R.id.editNameOfSongFromStorage);
        editNameOfArtistFromStorage = (EditText) findViewById(R.id.editNameOfArtistFromStorage);
        rlay = (RelativeLayout) findViewById(R.id.saveSongViewFromStorage);
        t = Toast.makeText(this, "Choose MIDI file (.mid)!", Toast.LENGTH_LONG);
        inn = new Intent(this, ConstructorActivity.class);
        constructorFile = null;
    }

    public void onPushNamesFromStorage(View v) {
        if (constructorFile != null) {
            if (editNameOfSongFromStorage.getText().toString().equals(null)
                    || editNameOfArtistFromStorage.getText().toString()
                    .equals(null)
                    || editNameOfSongFromStorage.getText().toString().equals("")
                    || editNameOfArtistFromStorage.getText().toString().equals("")
                    || editNameOfSongFromStorage.getText().toString().equals(" ")
                    || editNameOfArtistFromStorage.getText().toString().equals(" ")) {
                Toast t = Toast.makeText(this, "Fill In The Gaps", Toast.LENGTH_LONG);
                t.show();
            } else {
                Song s = new Song(
                        editNameOfSongFromStorage.getText().toString(),
                        editNameOfArtistFromStorage.getText().toString(),
                        Song.makeTableName(editNameOfSongFromStorage.getText()
                                .toString()
                                + " - "
                                + editNameOfArtistFromStorage.getText().toString()),
                        constructorFile.getName(), SongsActivity.songArrayList
                        .size() + 1);

                System.out.println(
                        "From FileExplore onPushNames() constructorFile.getAbsolutePath =  "
                                + constructorFile.getAbsolutePath());
                AddToDB2 toDBTask2 = new AddToDB2(s, constructorFile);
                toDBTask2.execute();
                editNameOfSongFromStorage.setText("");
                editNameOfArtistFromStorage.setText("");
                rlay.setVisibility(View.GONE);
            }
        } else {
            loadFileList();
            showDialog(DIALOG_LOAD_FILE);
            Toast t = Toast.makeText(this, "Choose File!", Toast.LENGTH_LONG);
            t.show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FileExplore.this, SongsActivity.class);
        startActivity(intent);
    }

    private void loadFileList() {
        try {
            path.mkdirs();
        } catch (SecurityException e) {
            Log.e(TAG, "unable to write on the sd card ");
        }

        // Checks whether path exists
        if (path.exists()) {
            FilenameFilter filter = (dir, filename) -> {
                File sel = new File(dir, filename);
                // Filters based on whether the file is hidden or not
                return (sel.isFile() || sel.isDirectory())
                        && !sel.isHidden();

            };

            String[] fList = path.list(filter);
            fileList = new Item[fList.length];
            for (int i = 0; i < fList.length; i++) {
                fileList[i] = new Item(fList[i], R.drawable.file_icon);

                // Convert into file path
                File sel = new File(path, fList[i]);

                // Set drawables
                if (sel.isDirectory()) {
                    fileList[i].icon = R.drawable.directory_icon;
                }
            }

            if (!firstLvl) {
                Item temp[] = new Item[fileList.length + 1];
                for (int i = 0; i < fileList.length; i++) {
                    temp[i + 1] = fileList[i];
                }
                temp[0] = new Item("Up", R.drawable.directory_up);
                fileList = temp;
            }
        } else {
            Log.e(TAG, "path does not exist");
        }

        adapter = new ArrayAdapter<Item>(this,
                android.R.layout.select_dialog_item, android.R.id.text1,
                fileList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // creates view
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view
                        .findViewById(android.R.id.text1);

                // put the image on the text view
                textView.setCompoundDrawablesWithIntrinsicBounds(
                        fileList[position].icon, 0, 0, 0);

                // add margin between image and text (support various screen
                // densities)
                int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
                textView.setCompoundDrawablePadding(dp5);

                return view;
            }
        };

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new Builder(this);

        if (fileList == null) {
            Log.e(TAG, "No files loaded");
            dialog = builder.create();
            return dialog;
        }

        switch (id) {
            case DIALOG_LOAD_FILE:
                builder.setTitle("Choose your MIDI file");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chosenFile = fileList[which].file;
                        File sel = new File(path + "/" + chosenFile);
                        if (sel.isDirectory()) {
                            firstLvl = false;

                            // Adds chosen directory to list
                            str.add(chosenFile);
                            fileList = null;
                            path = new File(sel + "");

                            loadFileList();

                            removeDialog(DIALOG_LOAD_FILE);
                            showDialog(DIALOG_LOAD_FILE);
                        }

                        // Checks if 'up' was clicked
                        else if (chosenFile.equalsIgnoreCase("up") && !sel.exists()) {

                            // present directory removed from list
                            String s = str.remove(str.size() - 1);

                            // path modified to exclude present directory
                            path = new File(path.toString().substring(0,
                                    path.toString().lastIndexOf(s)));
                            fileList = null;

                            // if there are no more directories in the list, then
                            // its the first level
                            if (str.isEmpty()) {
                                firstLvl = true;
                            }
                            loadFileList();

                            removeDialog(DIALOG_LOAD_FILE);
                            showDialog(DIALOG_LOAD_FILE);
                        }
                        // File picked
                        else {
                            if (sel.getName().endsWith(".mid")) {
                                constructorFile = sel;
                                if (!ConstructorActivity.isConstructorActivity) {
                                    rlay.setVisibility(View.VISIBLE);
                                } else {
                                    startActivity(inn);
                                }
                                try {
                                    String destPath = Environment.getExternalStorageDirectory()
                                            .toString() + ConstructorActivity.MUSICFOLDER + "/" +
                                            constructorFile.getName();
                                    FileInputStream in = new FileInputStream(constructorFile);
                                    OutputStream out = new FileOutputStream(destPath);

                                    byte[] buffer = new byte[1024];
                                    int length;
                                    while ((length = in.read(buffer)) > 0) {
                                        out.write(buffer, 0, length);
                                    }
                                    in.close();
                                    out.close();

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    Log.v("TAG", "ioexeption");
                                    e.printStackTrace();
                                }
                                System.out.println(
                                        "FromFileExplore constructorFile.getAbsolutePath == "
                                                + constructorFile.getAbsolutePath());
                            } else {
                                t.show();
                                removeDialog(DIALOG_LOAD_FILE);
                                showDialog(DIALOG_LOAD_FILE);
                            }
                        }

                    }
                });
                break;
        }
        dialog = builder.show();
        return dialog;
    }

    public class AddToDB2 extends AsyncTask<String, String, Void> {

        Song song;
        File file;

        public AddToDB2(Song song, File file) {
            this.song = song;
            this.file = file;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(FileExplore.this);
            pDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
            pDialog.setMessage("Your song is adding... It may take some time...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... arg0) {
            DBManager.getInstance(getApplicationContext()).addSongToDB(
                    song,
                    ConstructorActivity.getLinesToTable(ConstructorActivity
                            .getArrayFromMidi(constructorFile.getAbsolutePath())));
            ConstructorActivity.makeAllMidiFilesForGame(constructorFile.getAbsolutePath());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            System.out.println("From FileExplore fileName == " + file.getName());
            Intent intent = new Intent(FileExplore.this, SongsActivity.class);
            startActivity(intent);
            pDialog.dismiss();
            rlay.setVisibility(View.GONE);
        }

    }

    private class Item {

        public String file;
        public int icon;

        public Item(String file, Integer icon) {
            this.file = file;
            this.icon = icon;
        }

        @Override
        public String toString() {
            return file;
        }
    }

}