package com.nano.karen.SpotifyStreamer;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

public class PlaybackActivity extends ActionBarActivity {
    private TextView artistNameView;
    private TextView albumNameView;
    private ImageView albumImageView;
    private TextView trackNameView;
    private TextView mStart;
    private TextView mEnd;
    private SeekBar mSeekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);

        Intent intent = getIntent();
        TrackListItem mTrack = intent.getExtras().getParcelable("my parcel");

        artistNameView = (TextView) findViewById(R.id.playback_artist_name);
        albumNameView = (TextView) findViewById(R.id.playback_album_name);
        albumImageView = (ImageView) findViewById(R.id.playback_album_image);
        trackNameView = (TextView) findViewById(R.id.playback_track_name);
        mStart = (TextView) findViewById(R.id.startText);
        mEnd = (TextView) findViewById(R.id.endText);
        mSeekbar = (SeekBar) findViewById(R.id.seekBar);

        artistNameView.setText(mTrack.artistName);
        trackNameView.setText(mTrack.trackName);
        albumNameView.setText(mTrack.albumName);
        mStart.setText("0");
        mEnd.setText("100");


        if (!mTrack.trackImageURL.equals("")) {
            Picasso.with(this)
                    .load(mTrack.trackImageURL)
                    .resize(600, 600)
                    .error(R.drawable.dragon) // default image
                    .into(albumImageView);
        } else {
            Picasso.with(this)
                    .load(R.drawable.dragon)
                    .resize(600, 600)
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

/*
        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mStart.setText(Utils.formatMillis(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopSeekbarUpdate();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                getMediaController().getTransportControls().seekTo(seekBar.getProgress());
                scheduleSeekbarUpdate();
            }
        }); */

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

    private void updateDuration(MediaMetadata metadata) {
        if (metadata == null) {
            return;
        }
        int duration = (int) metadata.getLong(MediaMetadata.METADATA_KEY_DURATION);
        mSeekbar.setMax(duration);
        mEnd.setText(duration);
    }



}
