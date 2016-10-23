package com.codepath.flickster.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.codepath.flickster.R;
import com.codepath.flickster.models.Movie;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailActivity extends AppCompatActivity {
    VideoView videoView;
    ImageView backdropImage;
    TextView title;
    TextView releaseDate;
    TextView overview;
    RatingBar ratingBar;
    Movie movie;

    OkHttpClient client;

    private final String MOVIE_DETAIL_URL = "https://api.themoviedb.org/3/movie";
    private final String API_KEY = "a07e22bc18f5cb106bfe4cc1f83ad8ed";
    private final String IMAGE_BASE_URL= "https://image.tmdb.org/t/p/w500";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        client = new OkHttpClient();

        backdropImage = (ImageView) findViewById(R.id.backdrop);
        title = (TextView) findViewById(R.id.title);
        releaseDate = (TextView) findViewById(R.id.releaseDate);
        overview = (TextView) findViewById(R.id.overview);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        fetchMovieDetail(getIntent().getIntExtra("movieId", -1));
    }

    private void fetchMovieDetail(int movieId) {
        String url = getUrl(movieId);
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                try {
                    final JSONObject movieObject = new JSONObject(response.body().string());
                    DetailActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            movie = Movie.fromJson(movieObject);
                            renderMovieDetail(movie);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void renderMovieDetail(Movie movie) {
        title.setText(movie.getTitle());
        releaseDate.setText(movie.getReleaseDate());
        overview.setText(movie.getOverview());
        ratingBar.setRating(getRating(movie));

        Picasso.with(DetailActivity.this).
                load(IMAGE_BASE_URL + movie.getBackdropUrl()).
                fit().
                centerInside().
                transform(new RoundedCornersTransformation(20, 20)).
                placeholder(R.drawable.placeholder).
                into(backdropImage);
    }

    private String getUrl(int movidId) {
        List<String> list = Arrays.asList(MOVIE_DETAIL_URL, String.valueOf(movidId));
        String concatUrl = TextUtils.join("/", list);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(concatUrl).newBuilder();
        urlBuilder.addQueryParameter("api_key", API_KEY);
        return urlBuilder.build().toString();
    }

    private float getRating(Movie movie) {
        if (movie.getAvgScore() >= 5.0) {
            return (float) 5.0;
        } else {
            return (float) movie.getAvgScore();
        }
    }
}
