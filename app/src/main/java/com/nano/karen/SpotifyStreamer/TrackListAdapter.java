package com.nano.karen.SpotifyStreamer;


import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;


/**
 * Created by karenjin on 6/5/15.
 */
public class TrackListAdapter extends ArrayAdapter<TrackListItem> {

    Context context;
    public TrackListAdapter(Context context, int list_item_track, List<TrackListItem> users) {
        super(context, list_item_track, users);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TrackListItem rowItem = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_tracks, null);
        }

        // Lookup view for data population
        ImageView imageView = (ImageView) convertView.findViewById(R.id.list_item_track_image);
        TextView nameView = (TextView) convertView.findViewById(R.id.list_item_track_textview);
        // Populate the data into the template view using the data object
        // imageView.setImageResource(rowItem.trackImageURL);

        Picasso.with(context)
                .load(rowItem.trackImageURL)
                .resize(80, 80)
                .error(R.drawable.dragon) // default image
                .into(imageView);

        nameView.setText(rowItem.trackName);
        // Return the completed view to render on screen
        return convertView;
    }
}

/*
class TrackListItem {
    public TrackListItem(String imageID, String name){
        trackImageURL = imageID;
        trackName = name;
    }

    String trackImageURL;
    String trackName;
} */

class TrackListItem implements Parcelable {
    public String trackName;
    public String trackImageURL;
    public String trackID;

    public TrackListItem(Artist artist) {
        trackName = artist.name;
        trackImageURL = artist.images.get(0).url;
    }

    public TrackListItem(String imageID, String name, String id){
        trackImageURL = imageID;
        trackName = name;
        trackID = id;
    }

    public TrackListItem(Parcel in) {
        ReadFromParcel(in);
    }

    public static final Parcelable.Creator<ArtistListItem> CREATOR = new Parcelable.Creator<ArtistListItem>() {
        public ArtistListItem createFromParcel(Parcel in ) {
            return new ArtistListItem( in );
        }

        public ArtistListItem[] newArray(int size) {
            return new ArtistListItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trackName);
        dest.writeString(trackImageURL);
        dest.writeString(trackID);
    }

    private void ReadFromParcel(Parcel in) {
        trackName = in.readString();
        trackImageURL = in.readString();
        trackID = in.readString();
    }
}