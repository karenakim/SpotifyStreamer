package com.nano.karen.SpotifyStreamer;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    final Map<String, String>  artistIdMap;
    static String TAG;

    public PlaceholderFragment() {
        api = new SpotifyApi();
        spotify = api.getService();
        artistIdMap = new HashMap<String, String>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final View rootView = inflater.inflate(R.layout.fragment_placeholder, container, false);
        final SearchView searchText = (SearchView) rootView.findViewById(R.id.editArtistName);

        final List<ArtistListItem> artistsList = new ArrayList<>();
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

                                Log.d("Artist success", artistsPager.toString());
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
                                        artistsList.add(new ArtistListItem(anArtist.images.get(0).url, anArtist.name));
                                    }
                                    else {
                                        artistsList.add(new ArtistListItem(getString(R.string.default_artist_image), anArtist.name));
                                    }
                                    artistIdMap.put(anArtist.name, anArtist.id); // save id in map
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
                        .putExtra(Intent.EXTRA_TEXT, artistIdMap.get(mArtistsAdapter.getItem(position).artistName));

                startActivity(intent);
            }
        });


        return rootView;

    }

}
