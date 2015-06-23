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


public class MainActivity extends ActionBarActivity
        implements ArtistListFragment.OnArtistSelectedListener, TrackListFragment.OnTrackSelectedListener, PlaybackDialogFragment.OnPlayListener {

    private boolean mTwoPane;
    private TrackListFragment mTrackListFragment;

    private StreamerService mService;
    private boolean mBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.track_list_container) != null) {
            mTwoPane = true;

            FragmentManager fm = getFragmentManager();
            mTrackListFragment = (TrackListFragment) fm.findFragmentByTag(TrackListFragment.TAG);

            if ( mTrackListFragment == null) {
                Log.d("two pane", "new tracklistfragment created");
                mTrackListFragment = new TrackListFragment();
                fm.beginTransaction()
                        .replace(R.id.track_list_container, mTrackListFragment, TrackListFragment.TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }


        Intent sintent = new Intent(this, StreamerService.class);
        bindService(sintent, mConnection, Context.BIND_AUTO_CREATE);


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

        /*Fragment prev = getFragmentManager().findFragmentByTag(PlaybackDialogFragment.TAG);
        if (prev != null) {
            ft.remove(prev);
        }*/

        ft.addToBackStack(null); // not working, still have the fragment on stack

        // why the next two lines won't work? They should be equivallent to the show call below, right?
        //ft.replace(R.id.container, playbackDialog, PlaybackDialogFragment.TAG);
        //ft.commit();
        playbackDialog.show(ft, PlaybackDialogFragment.TAG);


        mService.play(curTrack.trackPreviewURL);
    }

    @Override
    public StreamerService getStreamerService(){
        return mService;
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            StreamerService.LocalBinder binder = (StreamerService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
    }
}
