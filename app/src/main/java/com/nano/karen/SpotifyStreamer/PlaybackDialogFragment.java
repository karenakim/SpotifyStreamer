package com.nano.karen.SpotifyStreamer;

import android.app.DialogFragment;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import static android.view.View.VISIBLE;

public class PlaybackDialogFragment extends DialogFragment {

    MediaPlayer mMediaPlayer;
    TrackListItem mTrack;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMediaPlayer = new MediaPlayer();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mTrack  = bundle.getParcelable("my parcel");
        }
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.playback_dialog, container, false);
        getDialog().setTitle("Now playing");

        artistNameView = (TextView) rootView.findViewById(R.id.playback_artist_name);
        albumNameView = (TextView) rootView.findViewById(R.id.playback_album_name);
        albumImageView = (ImageView) rootView.findViewById(R.id.playback_album_image);
        trackNameView = (TextView) rootView.findViewById(R.id.playback_track_name);

        mStart = (TextView) rootView.findViewById(R.id.startText);
        mEnd = (TextView) rootView.findViewById(R.id.endText);
        mSeekbar = (SeekBar) rootView.findViewById(R.id.seekBar);
        mPlayPause = (ImageView) rootView.findViewById(R.id.playback_play_pause);
        mSkipNext = (ImageView) rootView.findViewById(R.id.playback_next);
        mSkipPrev = (ImageView) rootView.findViewById(R.id.playback_prev);

        mPauseDrawable = getActivity().getDrawable(android.R.drawable.ic_media_pause);
        mPlayDrawable = getActivity().getDrawable(android.R.drawable.ic_media_play);

        artistNameView.setText(mTrack.artistName);
        trackNameView.setText(mTrack.trackName);
        albumNameView.setText(mTrack.albumName);


        if (!mTrack.trackImageURL.equals("")) {
            Picasso.with(getActivity())
                    .load(mTrack.trackImageURL)
                    .resize(600, 600)
                    .error(R.drawable.dragon) // default image
                    .into(albumImageView);
        } else {
            Picasso.with(getActivity())
                    .load(R.drawable.dragon)
                    .resize(600, 600)
                    .into(albumImageView);
        }


        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            //mMediaPlayer.setDataSource(trackURL);
            mMediaPlayer.setDataSource(mTrack.trackPreviewURL);
            Log.d("myservice", mTrack.trackPreviewURL);
            mMediaPlayer.prepare();
        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
            Log.e("Playback", "bad arguments");
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.e("Playback", "bad stream");
        }


        int duration = mMediaPlayer.getDuration();
        mSeekbar.setMax(duration);
        mStart.setText("0.00");
        mEnd.setText(String.format("%.2f", duration / 100000.0));
        mSeekbar.setProgress(mMediaPlayer.getCurrentPosition());
        mSeekbar.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        mSeekbar.setProgress(mMediaPlayer.getCurrentPosition());
                        mSeekbar.postDelayed(this, 500);
                    }
                }, 1000);


        //mMediaPlayer.start();

        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    mMediaPlayer.seekTo(progress);
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

                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    mPlayPause.setVisibility(VISIBLE);
                    mPlayPause.setImageDrawable(mPlayDrawable);
                } else {
                    mMediaPlayer.start();
                    mPlayPause.setVisibility(VISIBLE);
                    mPlayPause.setImageDrawable(mPauseDrawable);
                }
            }
        });
        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //super.onSaveInstanceState(savedInstanceState);
        //savedInstanceState.putInt("currentTrackPosition", mMediaPlayer.getCurrentPosition());
    }


    @Override
    public void onStop() {
        super.onStop();
        mMediaPlayer.stop();
    }

}
