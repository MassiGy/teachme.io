package com.example.teachmeio;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;

public class SoundManager  {

    private SoundPool mSoundPool;
    private HashMap mSoundPoolMap;
    private AudioManager mAudioManager;
    private Context mContext;

    //Initialisation du service MediaPlayer
    public void initSounds(Context theContext){
        mContext = theContext;
        mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        mSoundPoolMap = new HashMap();
        mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);

    }

    //Ajout d'un son a la playlist
    public void addSound(int index, int SoundID){
        mSoundPoolMap.put(index, mSoundPool.load(mContext, SoundID, 1));
    }

    //une seule fois
    public void playSound(int index){
        float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mSoundPool.play((Integer)mSoundPoolMap.get(index), streamVolume, streamVolume, 1, 0, 1f);
    }

    //mode en replay
    public void playLoopedSound(int index){
        float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mSoundPool.play((Integer)mSoundPoolMap.get(index), streamVolume, streamVolume, 1, -1, 1f);
    }
}
