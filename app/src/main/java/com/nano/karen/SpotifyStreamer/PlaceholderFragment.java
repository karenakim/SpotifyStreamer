package com.nano.karen.SpotifyStreamer;


import android.app.Fragment;
import android.content.Intent;
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


/**
 * Fragment for SpotifySteamer activity
 */
public class PlaceholderFragment extends Fragment {

    private SpotifyApi api;
    private SpotifyService spotify;
    private ArrayList<ArtistListItem> artistsList;

    static String TAG;
    final String artistsBundleID = "artistsBundle";

    public PlaceholderFragment() {
        api = new SpotifyApi();
        spotify = api.getService();
        artistsList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            artistsList = savedInstanceState.getParcelableArrayList(artistsBundleID);
        }

        final View rootView = inflater.inflate(R.layout.fragment_placeholder, container, false);
        final SearchView searchText = (SearchView) rootView.findViewById(R.id.editArtistName);

        final ArtistListAdapter mArtistsAdapter = new ArtistListAdapter(
                getActivity(), // The current context (this activity)
                R.layout.list_item_artist, // The name of the layout ID.
                artistsList);

        final ListView listView = (ListView) rootView.findViewById(R.id.listview_artists);
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
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Log.d("Artist failure", error.toString());
                            }
                        });
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
                Intent intent = new Intent(getActivity(), TrackListActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, mArtistsAdapter.getItem(position).artistID);
                startActivity(intent);
            }
        });
        return rootView;

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(artistsBundleID, artistsList);
        super.onSaveInstanceState(savedInstanceState);
    }

}

