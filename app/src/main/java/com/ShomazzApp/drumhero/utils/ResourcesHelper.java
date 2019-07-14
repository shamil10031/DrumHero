package com.ShomazzApp.drumhero.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.ShomazzApp.drumhero.ConstructorActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ResourcesHelper {

    private Context context;

    public ResourcesHelper(Context context) {
        this.context = context;
    }

    public void copySongs() {
        try {
            copySong("how_you_remind_me_nickelback_music_only");
            copySong("how_you_remind_me_nickelback_drums_only");
            copySong("ACDCSatteliteBlues_drums_only");
            copySong("ACDCSatteliteBlues_music_only");
            copySong("EvanescenceBringMeToLife_drums_only");
            copySong("EvanescenceBringMeToLife_music_only");
            copySong("FooFightersLearntoFly_drums_only");
            copySong("FooFightersLearntoFly_music_only");
            copySong("LennyKravitzAreYouGonnaGoMyWay_drums_only");
            copySong("LennyKravitzAreYouGonnaGoMyWay_music_only");
            copySong("IHateEverythingAboutYou_drums_only");
            copySong("IHateEverythingAboutYou_music_only");
            copySong("StockholmSyndrome_drums_only");
            copySong("StockholmSyndrome_music_only");
            copySong("The Who - I Can See for Miles_drums_only");
            copySong("The Who - I Can See for Miles_music_only");
            copySong("Red Hot Chili Peppers - Californication_music_only");
            copySong("Red Hot Chili Peppers - Californication_drums_only");
            copySong("Moby - Porcelain_drums_only");
            copySong("Moby - Porcelain_music_only");
            copySong("Drain_You_1_music_only");
            copySong("Drain_You_1_drums_only");
            copySong("Blur - Song 2_music_only");
            copySong("Blur - Song 2_drums_only");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            destroy();
        }
    }

    private void copySong(String nameOfSong) throws IOException {
        String musicFolderPath = Environment.getExternalStorageDirectory().toString()
                + ConstructorActivity.MUSICFOLDER;
        String destPath = musicFolderPath + "/" + nameOfSong;
        File song = new File(destPath);
        if (!song.exists()) {
            File musicFolder = new File(musicFolderPath);
            if (!musicFolder.exists()) {
                musicFolder.mkdir();
            }
            InputStream in = context.getAssets().open(nameOfSong);
            OutputStream out = new FileOutputStream(destPath);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        }
    }

    public Bitmap getBitmap(String name) {
        int id = context.getResources().getIdentifier(name, null,
                context.getPackageName());
        return BitmapFactory.decodeResource(context.getResources(), id);
    }

    public void destroy() {
        context = null;
    }

}
