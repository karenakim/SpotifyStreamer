package com.nano.karen.SpotifyStreamer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.net.BindException;

public class StreamerService extends Service implements MediaPlayer.OnPreparedListener {

    private MediaPlayer mMediaPlayer = null;

    private static final String ACTION_PLAY = "com.example.action.PLAY";
    private static final String TAG = "StreamerService";

    @Override
    public void onCreate() {
        Log.d("my player", "StreamerService onCreate"+ "in tread " + Thread.currentThread().getName()+ "in tread " + Thread.currentThread().getName());
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String track = intent.getStringExtra("track");
        if (track.equals("play")) {
            Log.d(TAG, "in service play");
            start();
        }
        else if (track.equals("pause")) {
            Log.d(TAG, "in service pause");
            pause();

        }
        else {
            Log.d(TAG, "in service play track");
            play(track);

        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "StreamerService onDestroy"+ "in tread " + Thread.currentThread().getName());
        super.onDestroy();
        if (mMediaPlayer != null) {
            if(mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void play(String track) {
        Log.d(TAG, "play");
        mMediaPlayer.reset();
        mMediaPlayer.setOnPreparedListener(this);

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(track);
            mMediaPlayer.prepareAsync();
            Log.d("my player", "ready to play");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.e("Playback", "bad arguments");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Playback", "bad stream");
        }

        mMediaPlayer.start();
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void pause() {
        mMediaPlayer.pause();
    }

    public void start() {
        if (mMediaPlayer.isPlaying())
            Log.d("my player", "in start playing");
        mMediaPlayer.start();
    }

    public void seekTo(int progress) {
        mMediaPlayer.seekTo(progress);
    }
}

