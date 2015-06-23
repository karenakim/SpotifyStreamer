package com.nano.karen.SpotifyStreamer;


import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
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
    private int curTrackIndex;

    static String TAG = "TrackListFragment";
    final String tracksBundleID = "tracksBundle";
    final int MAX_TRACK = 10;

    private String artistIDToSearch;
    private String artistNameToSearch;

    private View rootView;
    private ListView listView;
    private TrackListAdapter mTracksAdapter;

    private OnTrackSelectedListener mCallback;

    public TrackListFragment() {
        api = new SpotifyApi();
        spotify = api.getService();
        artistTracksList = new ArrayList<>();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnTrackSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTrackSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.track_list_fragment, container, false);

        Intent intent = getActivity().getIntent();
        artistIDToSearch = intent.getStringExtra("artistID");
        artistNameToSearch = intent.getStringExtra("artistName");

        if (savedInstanceState != null) {
            artistTracksList = savedInstanceState.getParcelableArrayList(tracksBundleID);
        }

        mTracksAdapter = new TrackListAdapter(
                getActivity(), // The current context (this activity)
                R.layout.list_item_tracks, // The name of the layout ID.
                artistTracksList);

        final ListView listView = (ListView) rootView.findViewById(R.id.listview_tracks);
        listView.setAdapter(mTracksAdapter);

        if (artistTracksList.isEmpty() && artistIDToSearch!=null) {
            Map<String, Object> option = new HashMap<>();
            option.put("country", "US");
            spotify.getArtistTopTrack(artistIDToSearch, option, new Callback<Tracks>() {
                @Override
                public void success(Tracks topTracks, Response response) {
                    for (Track aTrack : topTracks.tracks) {
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
                // try 1:
                /*Intent intent = new Intent(getActivity(), PlaybackActivity.class);
                intent.putExtra("my parcel", mTracksAdapter.getItem(position));
                startActivity(intent);*/

                // try 2: start the playback dialog with a bundle instead
                /*DialogFragment playbackDialog = new PlaybackDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("my parcel", mTracksAdapter.getItem(position));
                playbackDialog.setArguments(bundle);
                playbackDialog.show(getFragmentManager(), "playback dialog");*/
                //try 3:  do a callback on parent activity
                mCallback.onTrackSelected(mTracksAdapter.getItem(position));
                curTrackIndex = position;
            }
        });


        return rootView;
    }

    // This method will be accessed by the activity.
    public TrackListItem selectNextTrack(){
        if (curTrackIndex==MAX_TRACK) // wrap to first track
            curTrackIndex = 0;
        else
            curTrackIndex++;

        return mTracksAdapter.getItem(curTrackIndex);
    }

    // This method will be accessed by the activity.
    public TrackListItem selectPrevTrack(){
        if (curTrackIndex==0) // wrap to first track
            curTrackIndex = MAX_TRACK;
        else
            curTrackIndex--;

        return mTracksAdapter.getItem(curTrackIndex);
    }

    // This method will be accessed by the activity.
    public void selectArtist(String artistId, String artistName){
        // Execute adapter and get list updated.
        artistIDToSearch = artistId;
        artistNameToSearch = artistName;

        //Log.d("two pane", this.toString()+" is the track fragment in selectArtist");
        Map<String, Object> option = new HashMap<>();
        option.put("country", "US");
        spotify.getArtistTopTrack(artistIDToSearch, option, new Callback<Tracks>() {
            @Override
            public void success(Tracks topTracks, Response response) {
                artistTracksList.clear();
                for (Track aTrack : topTracks.tracks) {

                    if (!aTrack.album.images.isEmpty()) {
                        artistTracksList.add(
                                new TrackListItem(artistNameToSearch, aTrack.album.name, aTrack.album.images.get(0).url, aTrack.name, aTrack.preview_url));
                    } else { // no image, use default one
                        artistTracksList.add(
                                new TrackListItem(artistNameToSearch, aTrack.album.name, getString(R.string.default_artist_image), aTrack.name, aTrack.preview_url));
                    }
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


    public interface OnTrackSelectedListener{
        public void onTrackSelected(TrackListItem curTrack);
    };

}
