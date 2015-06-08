package com.nano.karen.SpotifyStreamer;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.nano.karen.SpotifyStreamer.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;


public class PlaybackActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);

        Intent intent = getIntent();
        TrackListItem mTrack = intent.getExtras().getParcelable("my parcel");

        TextView artistNameView = (TextView) findViewById(R.id.playback_artist_name);
        artistNameView.setText(mTrack.artistName);

        TextView albumNameView = (TextView) findViewById(R.id.playback_album_name);
        albumNameView.setText(mTrack.albumName);

        ImageView albumImageView = (ImageView) findViewById(R.id.playback_album_image);
        if (!mTrack.trackImageURL.equals("")) {
            Picasso.with(this)
                    .load(mTrack.trackImageURL)
                    .resize(80, 80)
                    .error(R.drawable.dragon) // default image
                    .into(albumImageView);
        } else {
            Picasso.with(this)
                    .load(R.drawable.dragon)
                    .resize(80, 80)
                    .into(albumImageView);
        }
        
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            //mediaPlayer.setDataSource(trackURL);
            mediaPlayer.setDataSource(mTrack.trackPreviewURL);
            mediaPlayer.prepare();
        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
            Log.e("Playback", "bad arguments");
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.e("Playback", "bad stream");
        }
        mediaPlayer.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_playback, menu);
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
