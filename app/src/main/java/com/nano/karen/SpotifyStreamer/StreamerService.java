package com.nano.karen.SpotifyStreamer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.BindException;

public class StreamerService extends Service implements MediaPlayer.OnPreparedListener {

    private MediaPlayer mMediaPlayer = null;
    private IBinder mBinder;

    private static final String ACTION_PLAY = "com.example.action.PLAY";
    private static final String TAG = "StreamerService";

    @Override
    public void onCreate() {
        Log.d("my player", "StreamerService onCreate"+ "in tread " + Thread.currentThread().getName()+ "in tread " + Thread.currentThread().getName());
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
    }

    public class LocalBinder extends Binder {
        StreamerService getService() {
            Log.d("my player", "StreamerService getService: service returned "+StreamerService.this.toString()+ " in tread " + Thread.currentThread().getName());
            return StreamerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("my player", "StreamerService onBind"+ " in tread " + Thread.currentThread().getName());
        if (mBinder == null){
            mBinder = new LocalBinder();
        }
        return mBinder;
        //return null;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("my player", "StreamerService onStartCommand"+ "in tread " + Thread.currentThread().getName());



        if (intent.getAction().equals(ACTION_PLAY)) {

            mMediaPlayer.reset();
            mMediaPlayer.setOnPreparedListener(this);

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mMediaPlayer.setDataSource(getString(R.string.default_artist_track));
                mMediaPlayer.prepareAsync();
                Log.d("my player", "ready to play");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                Log.e("Playback", "bad arguments");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Playback", "bad stream");
            }
        }

        return 0;
    }

    @Override
    public void onDestroy() {
        Log.d("my player", "StreamerService onDestroy"+ "in tread " + Thread.currentThread().getName());
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

