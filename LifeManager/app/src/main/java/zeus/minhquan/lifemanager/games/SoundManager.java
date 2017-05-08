package zeus.minhquan.lifemanager.games;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by QuanT on 5/8/2017.
 */

public class SoundManager {
    private static final int NUMBER_OF_SOUND = 2;
    public static SoundPool soundPool = new SoundPool(NUMBER_OF_SOUND, AudioManager.STREAM_MUSIC, 0);

    public static ArrayList<Integer> soundList = new ArrayList<>();

    public static void loadSoundIntoList(Context context) {
        for (int i = 0; i < NUMBER_OF_SOUND; i++) {
            int resIDSound = context.getResources().getIdentifier("r_"+i, "raw", context.getPackageName());
            int soundPoolID = soundPool.load(context, resIDSound, 1);
            soundList.add(soundPoolID);
        }
    }

    static HashMap<String, Integer> listSoundId = new HashMap<>();

    static {
        listSoundId.put("0", 0);
        listSoundId.put("1", 1);
    }

    public static void playSound(String string) {
        soundPool.play(soundList.get(listSoundId.get(string)), 1.0f, 1.0f, 1, 0, 1.0f);
    }
}
