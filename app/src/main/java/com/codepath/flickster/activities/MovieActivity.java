package com.codepath.flickster.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.flickster.R;
import com.codepath.flickster.adapter.MoviesAdapter;
import com.codepath.flickster.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieActivity extends AppCompatActivity {
    ArrayList<Movie> movies;
    ListView lv;
    MoviesAdapter adapter;
    OkHttpClient client;

    private SwipeRefreshLayout swipeContainer;
    private final String MOVIE_INDEX_URL =
            "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        client = new OkHttpClient();
        lv = (ListView) findViewById(R.id.lvMovies);
        setupListViewListener();
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(this::fetchMovies);
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        fetchMovies2();
    }

    // use OkHttp
    private void fetchMovies2() {
        Request request = new Request.Builder()
                             .url(MOVIE_INDEX_URL)
                             .build();
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
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    final JSONArray movieJson = jsonObject.getJSONArray("results");
                    MovieActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            movies = Movie.fromJson(movieJson);
                            if (adapter != null) {
                                adapter.clear();
                                adapter.addAll(movies);
                            } else {
                                adapter = new MoviesAdapter(MovieActivity.this, movies);
                                lv.setAdapter(adapter);
                            }
                            swipeContainer.setRefreshing(false);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void fetchMovies() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(MOVIE_INDEX_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray movieJson = response.getJSONArray("results");
                    movies = Movie.fromJson(movieJson);
                    if (adapter != null) {
                        adapter.clear();
                        adapter.addAll(movies);
                    } else {
                        adapter = new MoviesAdapter(MovieActivity.this, movies);
                        lv.setAdapter(adapter);
                    }
                    swipeContainer.setRefreshing(false);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(
                    int statusCode,
                    Header[] headers,
                    String responseString,
                    Throwable throwable
            ) {
                Toast.makeText(MovieActivity.this, "FAIL", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupListViewListener() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openDetailActivity(i);
            }
        });
    }

    private void openDetailActivity(int pos) {
        Movie movie = movies.get(pos);
        Intent i = new Intent(MovieActivity.this, DetailActivity.class);
        i.putExtra("movieId", movie.getId());
        startActivity(i);
    }
}
