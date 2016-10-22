package com.codepath.flickster.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.flickster.R;
import com.codepath.flickster.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class MoviesAdapter extends ArrayAdapter<Movie> {
    private final String imageBaseUrl = "https://image.tmdb.org/t/p/w500";
    ProgressBar progressBar;

    // View lookup cache
    private static class ViewHolder {
        ImageView poster;
        TextView title;
        TextView overview;
        ProgressBar progressBar;
    }

    public MoviesAdapter(Context context, ArrayList<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Movie movie = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_movie, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.overview = (TextView) convertView.findViewById(R.id.overview);
            viewHolder.poster = (ImageView) convertView.findViewById(R.id.poster);
            viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();

        }
        setViewHolder(viewHolder, movie);
        // Return the completed view to render on screen
        return convertView;
    }

    private void setViewHolder(ViewHolder viewHolder, Movie movie) {
        // Populate the data into the template view using the data object
        viewHolder.title.setText(movie.getTitle());
        viewHolder.overview.setText(movie.getOverview());

        // Add progress bar
        final ProgressBar progressBar = viewHolder.progressBar;
        progressBar.setVisibility(View.VISIBLE);
//        viewHolder.progressBar.setVisibility(View.VISIBLE);
        Picasso.with(getContext()).
                load(imageBaseUrl + getImageUrl(movie)).
                fit().
                centerInside().
                transform(new RoundedCornersTransformation(20, 20)).
                placeholder(R.drawable.placeholder).
                into(viewHolder.poster, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    private String getImageUrl(Movie movie) {
        if (isPortraitMode()) {
            return movie.getPosterUrl();
        } else {
            return movie.getBackdropUrl();
        }
    }

    private boolean isPortraitMode() {
        int orientation = getContext().getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_PORTRAIT;
    }

}
