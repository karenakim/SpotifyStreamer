package com.nano.karen.SpotifyStreamer;


import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class TrackListActivity extends ActionBarActivity
        implements TrackListFragment.OnTrackSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.track_list_container, new TrackListFragment(), TrackListFragment.TAG)
                    .commit();
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

    public void playTrack(String trackURL){
           // TODO: 6/23/15  need logic from main activity
    }


    @Override
    public void onTrackSelected(TrackListItem curTrack) {

        DialogFragment playbackDialog = new PlaybackDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("my parcel", curTrack);
        playbackDialog.setArguments(bundle);

        android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        //ft.commit();
        playbackDialog.show(ft, "playback dialog");


        //playbackDialog.show(getFragmentManager(), "playback dialog");

        /*FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_playback_dialog, playbackDialog, PlaybackDialogFragment.TAG);
        transaction.addToBackStack(null);
        transaction.commit();
*/
        playTrack(curTrack.trackPreviewURL);

    }
}
