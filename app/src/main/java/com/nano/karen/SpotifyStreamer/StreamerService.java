package com.nano.karen.SpotifyStreamer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.BindException;

public class StreamerService extends Service implements MediaPlayer.OnPreparedListener {

    MediaPlayer mMediaPlayer = null;
    private final IBinder mBinder = new LocalBinder();

    private static final String ACTION_PLAY = "com.example.action.PLAY";

    public StreamerService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();

    }

    public class LocalBinder extends Binder {
        StreamerService getService() {
            return StreamerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

       /* if (intent.getAction().equals(ACTION_PLAY)) {
            Toast.makeText(this, "Service started with intent", Toast.LENGTH_LONG).show();
            mMediaPlayer = new MediaPlayer();

            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync(); // prepare async to not block main thread
        }*/

        Toast.makeText(this, "Service started without intent", Toast.LENGTH_LONG).show();
        return flags;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service destroyed", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }

    public void playSong(){
        Toast.makeText(this, "Service bound", Toast.LENGTH_LONG).show();

        //mMediaPlayer.release();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(getString(R.string.default_artist_track));
            mMediaPlayer.prepare();
        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
            Log.e("Playback", "bad arguments");
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.e("Playback", "bad stream");
        }
        mMediaPlayer.start();
    }
}
