package com.nano.karen.SpotifyStreamer;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ArtistListFragment extends Fragment {

    private SpotifyApi api;
    private SpotifyService spotify;
    private ArrayList<ArtistListItem> artistsList;


    private View rootView;
    private SearchView searchText;
    private ListView listView;

    OnArtistSelectedListener mCallback;

    ArtistListAdapter mArtistsAdapter;

    static String TAG = "ArtistListFragment";
    final String artistsBundleID = "artistsBundle";


    public ArtistListFragment() {
        api = new SpotifyApi();
        spotify = api.getService();
        artistsList = new ArrayList<>();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnArtistSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnArtistSelectedListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            artistsList = savedInstanceState.getParcelableArrayList(artistsBundleID);
            if (artistsList==null)
                artistsList = new ArrayList<>();
        }

        setRetainInstance(true);
        rootView = inflater.inflate(R.layout.artist_list_fragment, container, false);
        searchText = (SearchView) rootView.findViewById(R.id.editArtistName);

        mArtistsAdapter = new ArtistListAdapter(
                getActivity(), // The current context (this activity)
                R.layout.list_item_artist, // The name of the layout ID.
                artistsList);

        listView = (ListView) rootView.findViewById(R.id.listview_artists);
        listView.setAdapter(mArtistsAdapter);

        searchText.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String artistToSearch) {
                        artistsList.clear();
                        spotify.searchArtists(artistToSearch, new Callback<ArtistsPager>() {
                            @Override
                            public void success(ArtistsPager artistsPager, Response response) {

                                //Log.d("Artist success", artistsPager.toString());
                                if (artistsPager.artists.items.isEmpty()) {
                                    Log.d("Artist not found", artistsPager.toString());
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast toast = Toast.makeText(getActivity(), "Artist not found!", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }
                                    });
                                    return;
                                }

                                for (Artist anArtist : artistsPager.artists.items) {
                                    if (!anArtist.images.isEmpty()) {
                                        artistsList.add(new ArtistListItem(anArtist.images.get(0).url, anArtist.name, anArtist.id));
                                    } else {  // no image, use default one
                                        artistsList.add(new ArtistListItem(getString(R.string.default_artist_image), anArtist.name, anArtist.id));
                                    }
                                }

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mArtistsAdapter.notifyDataSetChanged();
                                    }
                                });

                                // inform activitiy on new search result
                                Log.d("two pane", "list initial tracks");
                                mCallback.onArtistSelected(mArtistsAdapter.getItem(0).artistID, mArtistsAdapter.getItem(0).artistName);

                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Log.d("Artist failure", error.toString());
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(getActivity(), "Artist not found!", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                            }
                        });
                        searchText.clearFocus();
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                /*  new let activity start the new activity
                Intent intent = new Intent(getActivity(), TrackListActivity.class);
                intent.putExtra("artistID", mArtistsAdapter.getItem(position).artistID);
                intent.putExtra("artistName", mArtistsAdapter.getItem(position).artistName);
                startActivity(intent); */
                // use call back instead now
                mCallback.onArtistSelected(mArtistsAdapter.getItem(position).artistID, mArtistsAdapter.getItem(position).artistName);
            }
        });
        return rootView;

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (!artistsList.isEmpty()) {
            savedInstanceState.putParcelableArrayList(artistsBundleID, artistsList);
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    public interface OnArtistSelectedListener{
        public void onArtistSelected(String artistID, String artistName);
    };
}

