package com.nano.karen.SpotifyStreamer;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class TrackListActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);

        Intent intent = getIntent();
        String message = intent.getStringExtra(Intent.EXTRA_TEXT);

        ((TextView)findViewById(R.id.test)).setText(message);

/*
        final List<String> artistsTracksList = new ArrayList<>();
        artistsTracksList.add(message + "1");
        artistsTracksList.add(message + "2");

        final ArrayAdapter<String> mTracksAdapter;
        mTracksAdapter = new ArrayAdapter<>(
                this, // The current context (this activity)
                R.layout.list_item_tracks, // The name of the layout ID.
                R.id.list_item_track_textview, // The ID of the textview to populate.
                artistsTracksList);

        final ListView listView = (ListView) findViewById(R.id.listview_artists);
        listView.setAdapter(mTracksAdapter);
*/
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
