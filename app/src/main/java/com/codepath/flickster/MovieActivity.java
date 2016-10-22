package com.codepath.flickster;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.flickster.adapter.MoviesAdapter;
import com.codepath.flickster.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MovieActivity extends AppCompatActivity {
    ArrayList<Movie> movies;
    ListView lv;
    MoviesAdapter adapter;

    private SwipeRefreshLayout swipeContainer;
    private final String movieIndexUrl =
            "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        lv = (ListView) findViewById(R.id.lvMovies);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchMovies();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        fetchMovies();
    }

    private void fetchMovies() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(movieIndexUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray movieJson = response.getJSONArray("results");
                    movies = Movie.fromJson(movieJson);
                    for (int i = 0; i < movies.size(); ++i) {
                        Log.d("posterUrl", movies.get(i).getPosterUrl());
                    }
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
}
