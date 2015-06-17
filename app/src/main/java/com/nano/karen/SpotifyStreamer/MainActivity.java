package com.nano.karen.SpotifyStreamer;


import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity
    implements ArtistListFragment.OnArtistSelectedListener {

    private boolean mTwoPane;
    TrackListFragment mTrackListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.track_list_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                mTrackListFragment = new TrackListFragment();
                getFragmentManager()
                        .beginTransaction()
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
            // If it's two pane, we can assume the fragment
            // is already attached to the activity.
            // Don't forget to retain it's instance.
            Log.d("two pane", mTrackListFragment.toString()+" is the track fragment in main activity");

            mTrackListFragment.selectArtist(artistId, artistName);
        }
        else {
            // If there's no two pane, you can start your other
            // activity.
            Intent intent = new Intent(this, TrackListActivity.class);
            intent.putExtra("artistID", artistId);
            intent.putExtra("artistName", artistName);
            startActivity(intent);
        }

    }
}
