package com.nano.karen.SpotifyStreamer;


import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity
        implements ArtistListFragment.OnArtistSelectedListener {

    private boolean mTwoPane;
    private TrackListFragment mTrackListFragment;

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
}
