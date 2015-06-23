package com.nano.karen.SpotifyStreamer;

import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import static android.view.View.VISIBLE;

public class PlaybackActivity extends ActionBarActivity {


    StreamerService mService;
    boolean mBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_playback);

        Intent intent = getIntent();
        TrackListItem mTrack = intent.getExtras().getParcelable("my parcel");

        // start fragment
        DialogFragment playbackDialog = new PlaybackDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("my parcel", mTrack);
        playbackDialog.setArguments(bundle);
        playbackDialog.show(getFragmentManager(), "playback dialog");

        // experiment with service

        Intent sintent = new Intent(this, StreamerService.class);

        if (mService==null)
            Log.d("myservice", "null");

        bindService(sintent, mConnection, Context.BIND_AUTO_CREATE);

        if (mService==null)
            Log.d("myservice", "null");

        //startTrack();

    }


    private void startTrack(){
        //startService(new Intent(getBaseContext(), StreamerService.class));

    }

    private void stopTrack(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_playback, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        //mMediaPlayer.stop();
    }




    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            StreamerService.LocalBinder binder = (StreamerService.LocalBinder) service;
            mService = binder.getService();

            if (mService==null)
                Log.d("myservice", "still null");

            mService.playSong();

            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

}
