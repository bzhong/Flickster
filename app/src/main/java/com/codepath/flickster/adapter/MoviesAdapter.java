package com.codepath.flickster.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.databinding.library.baseAdapters.BR;
import com.codepath.flickster.R;
import com.codepath.flickster.databinding.ItemMovieBinding;
import com.codepath.flickster.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class MoviesAdapter extends ArrayAdapter<Movie> {
    private final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";

    // View lookup cache
    static class HighScoreMovieViewHolder {
        @BindView(R.id.poster) ImageView poster;
        @BindView(R.id.progressBar) ProgressBar progressBar;

        public HighScoreMovieViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class LowScoreMovieViewHolder {
        @BindView(R.id.poster) ImageView poster;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.overview) TextView overview;
        @BindView(R.id.progressBar) ProgressBar progressBar;

        private ItemMovieBinding binding;

        public LowScoreMovieViewHolder(View view) {
            ButterKnife.bind(this, view);
            binding = DataBindingUtil.bind(view);
        }

        public ItemMovieBinding getBinding() {
            return binding;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType().ordinal();
    }

    @Override
    public int getViewTypeCount() {
        return Movie.MovieTypes.values().length;
    }

    public MoviesAdapter(Context context, ArrayList<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        if (viewType == Movie.MovieTypes.HIGH_SCORE.ordinal()) {
            return getHighScoreView(position, convertView, parent);
        } else {
            return getLowScoreView(position, convertView, parent);
        }
    }

    private View getHighScoreView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);
        HighScoreMovieViewHolder viewHolder;
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_full_backdrop, parent, false);
            viewHolder = new HighScoreMovieViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (HighScoreMovieViewHolder) convertView.getTag();

        }
        setHighScoreViewHolder(viewHolder, movie);
        // Return the completed view to render on screen
        return convertView;
    }

    private void setHighScoreViewHolder(HighScoreMovieViewHolder viewHolder, Movie movie) {
        // Add progress bar
        final ProgressBar progressBar = viewHolder.progressBar;
        progressBar.setVisibility(View.VISIBLE);
        Picasso.with(getContext()).
                load(IMAGE_BASE_URL + getImageUrl(movie)).
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

    private View getLowScoreView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);
        LowScoreMovieViewHolder viewHolder;
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_movie, parent, false);
            viewHolder = new LowScoreMovieViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (LowScoreMovieViewHolder) convertView.getTag();

        }
        setLowScoreViewHolder(viewHolder, movie);
        // Return the completed view to render on screen
        return convertView;
    }

    private void setLowScoreViewHolder(LowScoreMovieViewHolder viewHolder, Movie movie) {
        // Populate the data into the template view using the data object
        viewHolder.getBinding().setVariable(BR.movie, movie);

        // Add progress bar
        final ProgressBar progressBar = viewHolder.progressBar;
        progressBar.setVisibility(View.VISIBLE);
        Picasso.with(getContext()).
                load(IMAGE_BASE_URL + getImageUrl(movie)).
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
        if (isPortraitMode() &&
                movie.getType().ordinal() != Movie.MovieTypes.HIGH_SCORE.ordinal()) {
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
