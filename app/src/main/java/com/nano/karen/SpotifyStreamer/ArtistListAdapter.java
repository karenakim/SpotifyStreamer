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
public class ArtistListAdapter extends ArrayAdapter<ArtistListItem> {
    Context context;
    public ArtistListAdapter(Context context, int list_item_artist, List<ArtistListItem> users) {
        super(context, list_item_artist, users);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ArtistListItem rowItem = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_artist, null);
        }

        // Lookup view for data population
        ImageView imageView = (ImageView) convertView.findViewById(R.id.list_item_artist_image);
        TextView nameView = (TextView) convertView.findViewById(R.id.list_item_artist_textview);

        // Populate the data into the template view using the data object
        //imageView.setImageResource(rowItem.artistImageID);
        if (!rowItem.artistImageURL.equals("")) {
            Picasso.with(context)
                    .load(rowItem.artistImageURL)
                    .resize(80, 80)
                    .error(R.drawable.dragon) // default image
                    .into(imageView);
        } else {
            Picasso.with(context)
                    .load(R.drawable.dragon)
                    .resize(80, 80)
                    .into(imageView);
        }


        nameView.setText(rowItem.artistName);
        // Return the completed view to render on screen
        return convertView;
    }
}

/*
class ArtistListItem {
    public ArtistListItem(String imageID, String name){
        artistImageURL = imageID;
        artistName = name;
    }
    String artistImageURL;
    String artistName;
}*/


class ArtistListItem implements Parcelable {
    public String artistName;
    public String artistImageURL;
    public String artistID;

    public ArtistListItem(Artist artist) {
        artistName = artist.name;
        artistImageURL = artist.images.get(0).url;
    }

    public ArtistListItem(String imageID, String name, String id){
        artistImageURL = imageID;
        artistName = name;
        artistID = id;
    }

    public ArtistListItem(Parcel in) {
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
        dest.writeString(artistName);
        dest.writeString(artistImageURL);
        dest.writeString(artistID);
    }

    private void ReadFromParcel(Parcel in) {
        artistName = in.readString();
        artistImageURL = in.readString();
        artistID = in.readString();
    }
}