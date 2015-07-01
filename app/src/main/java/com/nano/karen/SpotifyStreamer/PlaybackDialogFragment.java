package com.nano.karen.SpotifyStreamer;

import android.app.Activity;
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

    private OnPlayListener mCallback;
    private  StreamerService service;

    static String TAG = "PlaybackDialogFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mTrack  = bundle.getParcelable("my parcel");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnPlayListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPlayListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
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

        service = mCallback.getStreamerService();
        Log.d("my player", mCallback.toString());
        if (service == null) {
            Log.d("my player", "null pointer!!!" + Thread.currentThread().getName());
            Log.d("my player", mCallback.toString());
        }
        int duration = service.getDuration();
        mSeekbar.setMax(duration);
        mStart.setText("0.00");
        mEnd.setText(String.format("%.2f", duration / 100000.0));
        mSeekbar.setProgress(service.getCurrentPosition());
        mSeekbar.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        mSeekbar.setProgress(service.getCurrentPosition());
                        mSeekbar.postDelayed(this, 500);
                    }
                }, 1000);


        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    service.seekTo(progress);
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
                if (service.isPlaying()) {
                    Log.d("my player", "still playing");
                    service.pause();
                    mPlayPause.setVisibility(VISIBLE);
                    mPlayPause.setImageDrawable(mPlayDrawable);
                } else {
                    Log.d("my player", "not playing");
                    service.start();
                    mPlayPause.setVisibility(VISIBLE);
                    mPlayPause.setImageDrawable(mPauseDrawable);
                }
            }
        });

        mSkipNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.playNext();
            }
        });

        mSkipPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.playPrev();
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
    public void onPause() {
        super.onPause();
        service.pause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public interface OnPlayListener{
        public StreamerService getStreamerService();
        public void playNext();
        public void playPrev();
    };


}
