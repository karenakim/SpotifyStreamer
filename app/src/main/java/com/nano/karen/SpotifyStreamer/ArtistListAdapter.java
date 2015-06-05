package com.nano.karen.SpotifyStreamer;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

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
        imageView.setImageResource(rowItem.artistImageID);
        nameView.setText(rowItem.artistName);
        // Return the completed view to render on screen
        return convertView;
    }
}

class ArtistListItem {
    public ArtistListItem(int imageID, String name){
        artistImageID = imageID;
        artistName = name;
    }
    int artistImageID;
    String artistName;
}