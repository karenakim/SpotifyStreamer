package com.nano.karen.SpotifyStreamer;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nano.karen.SpotifyStreamer.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrackListFragment extends Fragment {

    private SpotifyApi api;
    private SpotifyService spotify;
    private ArrayList<TrackListItem> artistTracksList;

    static String TAG;
    final String tracksBundleID = "tracksBundle";

    public TrackListFragment() {
        api = new SpotifyApi();
        spotify = api.getService();
        artistTracksList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final View rootView = inflater.inflate(R.layout.track_list_fragment, container, false);

        Intent intent = getActivity().getIntent();
        final String artistIDToSearch = intent.getStringExtra("artistID");
        final String artistNameToSearch = intent.getStringExtra("artistName");


        if (savedInstanceState != null) {
            artistTracksList = savedInstanceState.getParcelableArrayList(tracksBundleID);
        }

        final TrackListAdapter mTracksAdapter = new TrackListAdapter(
                getActivity(), // The current context (this activity)
                R.layout.list_item_tracks, // The name of the layout ID.
                artistTracksList);

        final ListView listView = (ListView) rootView.findViewById(R.id.listview_tracks);
        listView.setAdapter(mTracksAdapter);

        if (artistTracksList.isEmpty()) {
            Map<String, Object> option = new HashMap<>();
            option.put("country", "US");
            spotify.getArtistTopTrack(artistIDToSearch, option, new Callback<Tracks>() {
                @Override
                public void success(Tracks topTracks, Response response) {
                    //Log.d("Track success", topTracks.toString());
                    for (Track aTrack : topTracks.tracks) {
                        //Log.d("Track success", aTrack.toString());

                        for (Image i : aTrack.album.images) {
                            Log.d("Track images", i + " size is " + i.height + " by " + i.width);
                            Log.d("Track images URL", " " + i.url);
                        }
                        Log.d("Track images", " ");

                        artistTracksList.add(
                                new TrackListItem(artistNameToSearch, aTrack.album.name, aTrack.album.images.get(0).url, aTrack.name, aTrack.preview_url));
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTracksAdapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("Track failure", error.toString());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(getActivity(), "Track not found!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                }
            });
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getActivity(), PlaybackActivity.class);
                //intent.putExtra(Intent.EXTRA_TEXT, mTracksAdapter.getItem(position).trackPreviewURL);
                intent.putExtra("my parcel", mTracksAdapter.getItem(position));
                startActivity(intent);
            }
        });

        return rootView;
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
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(tracksBundleID, artistTracksList);
        super.onSaveInstanceState(savedInstanceState);
    }



}
