package com.nano.karen.SpotifyStreamer;


import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity
        implements ArtistListFragment.OnArtistSelectedListener, TrackListFragment.OnTrackSelectedListener, PlaybackDialogFragment.OnPlayListener {

    private boolean mTwoPane;
    private TrackListFragment mTrackListFragment;

    //private StreamerService mService;
    //private boolean mBound;

    private static final String ACTION_PLAY = "com.example.action.PLAY";


    /*
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            StreamerService.LocalBinder binder = (StreamerService.LocalBinder) service;
            mService = binder.getService();
            Log.d("my player", "in onServiceConnected "+mService.toString()+ "in tread " + Thread.currentThread().getName());
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d("my player", "lost service"+mService.toString());
            mBound = false;
        }
    }; */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("my player", "onCreate");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Intent sintent = new Intent(this, StreamerService.class);
        sintent.setAction(ACTION_PLAY);
        startService(sintent);*/

        //Log.d("my player", "return value of startService: " + startService(sintent));
        //Log.d("my player", "return value of bindService: " + getApplicationContext().bindService(sintent, mConnection, Context.BIND_AUTO_CREATE));
        //Log.d("my player", "in onCreate after bindService "+mConnection.toString()+ "in tread " + Thread.currentThread().getName());

        if (findViewById(R.id.track_list_container) != null) {
            mTwoPane = true;

            FragmentManager fm = getFragmentManager();
            mTrackListFragment = (TrackListFragment) fm.findFragmentByTag(TrackListFragment.TAG);

            if ( mTrackListFragment == null) {

                mTrackListFragment = new TrackListFragment();
                fm.beginTransaction()
                        .replace(R.id.track_list_container, mTrackListFragment, TrackListFragment.TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public void onArtistSelected(String artistId, String artistName) {
        if(mTwoPane){
            mTrackListFragment.selectArtist(artistId, artistName);
        } else {
            Intent intent = new Intent(this, TrackListActivity.class);
            intent.putExtra("artistID", artistId);
            intent.putExtra("artistName", artistName);
            startActivity(intent);
        }
    }

    @Override
    public void playNext(){
        onTrackSelected(mTrackListFragment.selectNextTrack());
    }

    @Override
    public void playPrev(){
        onTrackSelected(mTrackListFragment.selectPrevTrack());
    }

    @Override
    public void onTrackSelected(TrackListItem curTrack) {
        DialogFragment playbackDialog = new PlaybackDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("my parcel", curTrack);
        playbackDialog.setArguments(bundle);

        FragmentTransaction ft = getFragmentManager().beginTransaction();

        Fragment prev = getFragmentManager().findFragmentByTag(PlaybackDialogFragment.TAG);
        if (prev != null) {
            Log.d("my dialog", prev.toString());
            ft.remove(prev);
        }

        playbackDialog.show(ft, PlaybackDialogFragment.TAG);

        // send an intent instead here
        //mService.play(curTrack.trackPreviewURL);

        Intent sintent = new Intent(this, StreamerService.class);
        sintent.putExtra("track", curTrack.trackPreviewURL);
        //sintent.setAction(ACTION_PLAY);
        startService(sintent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent sintent = new Intent(this, StreamerService.class);
        stopService(sintent);
    }

    @Override
    protected void onDestroy() {
        Log.d("my player", "in onDestroy");
        super.onDestroy();
        //getApplicationContext().unbindService(mConnection);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
