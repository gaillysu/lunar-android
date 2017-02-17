package com.medcorp.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

/**
 * Created by med on 16/12/30.
 */

public class SoundPlayer {
    private Context context;
    private MediaPlayer player;
    public SoundPlayer(Context context) {
        this.context = context;
    }
    public void startPlayer(int resRawId)
    {
        player = MediaPlayer.create(context, resRawId);
        //create function will invoke native code to build Player object, some variables in native layer perhaps are shared and not released.
        if(player !=null && player.isPlaying())
        {
            player.stop();
        }
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume , 0);
        player.start();
    }

    public  void StopPlayer()
    {
        if(player!=null && player.isPlaying())
        {
            player.stop();
        }
    }
}
