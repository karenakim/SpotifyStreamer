package com.nano.karen.SpotifyStreamer;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static android.view.View.VISIBLE;

public class PlaybackDialogFragment extends DialogFragment {

    TrackListItem mTrack;
    private View rootView;
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
    private boolean isPlaying = true;
    private boolean mBound;

    private StreamerService mService;

    static String TAG = "PlaybackDialogFragment";

    public PlaybackDialogFragment() {
        super();
        Log.d("my player", "PlayBackDialogFragment() constructor");
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.d("my player", "onServiceConnected()");
            StreamerService.LocalBinder binder = (StreamerService.LocalBinder) service;
            mService = binder.getService();
            int duration = mService.getDuration();
            Log.d("my player", "duration"+duration);
            mStart.setText("0.00");
            mEnd.setText(String.format("%.2f", duration / 1000.0));

            mSeekbar.setMax(duration);
            mSeekbar.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            mSeekbar.setProgress(mService.getCurrentPosition());
                            mSeekbar.postDelayed(this, 500);
                        }
                    }, 1000);

            mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser)
                        mService.seekTo(progress);
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
                    if (isPlaying) {
                    /*Intent sintent = new Intent(getActivity(), StreamerService.class);
                    sintent.putExtra("track", "pause");
                    getActivity().startService(sintent);*/
                        mService.pause();
                        mPlayPause.setVisibility(VISIBLE);
                        mPlayPause.setImageDrawable(mPlayDrawable);
                        isPlaying = false;
                    } else {
                    /*Intent sintent = new Intent(getActivity(), StreamerService.class);
                    sintent.putExtra("track", "play");
                    getActivity().startService(sintent);*/
                        mService.start();
                        mPlayPause.setVisibility(VISIBLE);
                        mPlayPause.setImageDrawable(mPauseDrawable);
                        isPlaying = true;
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

            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d("my player", "onServiceDisconnected()");
            mBound = false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("my player", "onCreate()");

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mTrack  = bundle.getParcelable("my parcel");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Log.d("my player", "onAttach()");
        Intent sintent = new Intent(activity, StreamerService.class);
        activity.bindService(sintent, mConnection, Context.BIND_AUTO_CREATE);

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


        Log.d("my player", "onCreateView()");
        rootView = inflater.inflate(R.layout.playback_dialog, container, false);
        buildView(rootView);
        return rootView;
    }

    private void buildView(View rootView){
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
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //super.onSaveInstanceState(savedInstanceState);
        //savedInstanceState.putInt("currentTrackPosition", mMediaPlayer.getCurrentPosition());
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        //getActivity().unbindService(mConnection);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public interface OnPlayListener{
        public void playNext();
        public void playPrev();
    };

}
