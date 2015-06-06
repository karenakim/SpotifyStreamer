package com.nano.karen.SpotifyStreamer;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.Call;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class TrackListActivity extends ActionBarActivity {

    private SpotifyApi api;
    private SpotifyService spotify;

    public TrackListActivity() {
        api = new SpotifyApi();
        spotify = api.getService();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);

        Intent intent = getIntent();
        String artistToSearch = intent.getStringExtra(Intent.EXTRA_TEXT);


        // set up list view adapter
        /*
        final List<String> artistsTracksList = new ArrayList<>();
        final ArrayAdapter<String> mTracksAdapter;
        mTracksAdapter = new ArrayAdapter<>(
                this, // The current context (this activity)
                R.layout.list_item_tracks, // The name of the layout ID.
                R.id.list_item_track_textview, // The ID of the textview to populate.
                artistsTracksList);

        final ListView listView = (ListView) findViewById(R.id.listview_tracks);
        listView.setAdapter(mTracksAdapter); */



        final List<TrackListItem> artistTracksList = new ArrayList<>();
        final TrackListAdapter mTracksAdapter = new TrackListAdapter(
                this, // The current context (this activity)
                R.layout.list_item_tracks, // The name of the layout ID.
                artistTracksList);

        final ListView listView = (ListView) findViewById(R.id.listview_tracks);
        listView.setAdapter(mTracksAdapter);



        Map<String, Object> option = new HashMap<>();
        option.put("country", "US");
        spotify.getArtistTopTrack(artistToSearch, option, new Callback<Tracks>() {
            @Override
            public void success(Tracks topTracks, Response response) {
                Log.d("Track success", topTracks.toString());
                for (Track aTrack : topTracks.tracks) {
                    Log.d("Track success", aTrack.toString());
                    artistTracksList.add(new TrackListItem(aTrack.album.images.get(0).url, aTrack.album.name));
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTracksAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Track failure", error.toString());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_track_list, menu);
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
}
