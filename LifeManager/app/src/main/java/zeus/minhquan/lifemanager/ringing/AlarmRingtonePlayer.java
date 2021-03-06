package zeus.minhquan.lifemanager.ringing;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import zeus.minhquan.lifemanager.utils.Logger;

/**
 * Created by QuanT on 4/22/2017.
 */

public class AlarmRingtonePlayer {
    private MediaPlayer mPlayer;
    private Context mContext;

    public AlarmRingtonePlayer(Context context) {
        mContext = context;
    }

    public void initialize() {
        try {
            mPlayer = new MediaPlayer();
        } catch (Exception e) {
            Logger.trackException(e);
        }
    }

    public void cleanup() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void play(Uri toneUri) {
        try {
            if (mPlayer != null && !mPlayer.isPlaying()) {
                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
                mPlayer.setDataSource(mContext, toneUri);
                mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mPlayer.setLooping(true);
                mPlayer.prepareAsync();
            }
        } catch (Exception e) {
            Logger.trackException(e);
        }
    }

    public void stop() {
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            mPlayer.reset();
        }
    }
}
