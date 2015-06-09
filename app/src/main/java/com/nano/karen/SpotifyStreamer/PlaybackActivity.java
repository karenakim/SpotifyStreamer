package com.nano.karen.SpotifyStreamer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import static android.view.View.VISIBLE;

public class PlaybackActivity extends ActionBarActivity {

    MediaPlayer mediaPlayer;

    private TextView artistNameView;
    private TextView albumNameView;
    private ImageView albumImageView;
    private TextView trackNameView;
    private TextView mStart;
    private TextView mEnd;
    private SeekBar mSeekbar;
    private ImageView mSkipPrev;
    private ImageView mSkipNext;
    private ImageView mPlayPause;
    private Drawable mPauseDrawable;
    private Drawable mPlayDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);

        Intent intent = getIntent();
        TrackListItem mTrack = intent.getExtras().getParcelable("my parcel");

        mediaPlayer = new MediaPlayer();

        artistNameView = (TextView) findViewById(R.id.playback_artist_name);
        albumNameView = (TextView) findViewById(R.id.playback_album_name);
        albumImageView = (ImageView) findViewById(R.id.playback_album_image);
        trackNameView = (TextView) findViewById(R.id.playback_track_name);
        mStart = (TextView) findViewById(R.id.startText);
        mEnd = (TextView) findViewById(R.id.endText);
        mSeekbar = (SeekBar) findViewById(R.id.seekBar);
        mPlayPause = (ImageView) findViewById(R.id.playback_play_pause);
        mSkipNext = (ImageView) findViewById(R.id.playback_next);
        mSkipPrev = (ImageView) findViewById(R.id.playback_prev);

        mPauseDrawable = getDrawable(android.R.drawable.ic_media_pause);
        mPlayDrawable = getDrawable(android.R.drawable.ic_media_play);

        artistNameView.setText(mTrack.artistName);
        trackNameView.setText(mTrack.trackName);
        albumNameView.setText(mTrack.albumName);


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

        int duration = mediaPlayer.getDuration();
        mSeekbar.setMax(duration);
        mStart.setText("0.00");
        mEnd.setText(String.format("%.2f", duration / 100000.0));
        mSeekbar.setProgress(mediaPlayer.getCurrentPosition());
        mSeekbar.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        mSeekbar.setProgress(mediaPlayer.getCurrentPosition());
                        mSeekbar.postDelayed(this, 500);
                    }
                }, 1000);
        mediaPlayer.start();


        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    mediaPlayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    mPlayPause.setVisibility(VISIBLE);
                    mPlayPause.setImageDrawable(mPlayDrawable);
                } else {
                    mediaPlayer.start();
                    mPlayPause.setVisibility(VISIBLE);
                    mPlayPause.setImageDrawable(mPauseDrawable);

                }
            }
        });
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
