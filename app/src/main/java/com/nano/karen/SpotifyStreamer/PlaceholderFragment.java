package com.nano.karen.SpotifyStreamer;


import android.content.Intent;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlaceholderFragment extends Fragment {


    private SpotifyApi api;
    private SpotifyService spotify;

    public PlaceholderFragment() {
        api = new SpotifyApi();
        spotify = api.getService();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final View rootView = inflater.inflate(R.layout.fragment_placeholder, container, false);
        final EditText editText = (EditText) rootView.findViewById(R.id.editArtistName);

        final List<String> artistsNamesList = new ArrayList<>();
        final ArrayAdapter<String> mArtistsAdapter;
        mArtistsAdapter = new ArrayAdapter<>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_artist, // The name of the layout ID.
                        R.id.list_item_forecast_textview, // The ID of the textview to populate.
                        artistsNamesList);

        final ListView listView = (ListView) rootView.findViewById(R.id.listview_artists);
        listView.setAdapter(mArtistsAdapter);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    String artistToSearch = editText.getText().toString();
                    artistsNamesList.clear();
                    spotify.searchArtists(artistToSearch, new Callback<ArtistsPager>() {
                        @Override
                        public void success(ArtistsPager artistsPager, Response response) {
                            Log.d("Artist success", artistsPager.toString());

                            for (Artist anArtist : artistsPager.artists.items) {
                                Log.d("Artist success", anArtist.name);
                                artistsNamesList.add(anArtist.name);
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
                    handled = true;
                }
                return handled;
            }
        });



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String forecast = mArtistsAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), TrackListActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(intent);
            }
        });


        return rootView;

    }






}
