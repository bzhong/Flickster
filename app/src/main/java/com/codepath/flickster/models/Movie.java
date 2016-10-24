package com.codepath.flickster.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Movie {
    private int id;
    private String title;
    private String posterUrl;
    private String backdropUrl;
    private String overview;
    private float populatiry;
    private float avgScore;
    private String releaseDate;
    private MovieTypes type;

    public enum MovieTypes {
        HIGH_SCORE, LOW_SCORE
    }

    public static Movie fromJson(JSONObject jsonObject) {
        Movie movie = new Movie();
        try {
            movie.id = Integer.parseInt(jsonObject.getString("id"));
            movie.title = jsonObject.getString("title");
            movie.posterUrl = jsonObject.getString("poster_path");
            movie.backdropUrl = jsonObject.getString("backdrop_path");
            movie.overview = jsonObject.getString("overview");
            movie.populatiry = Float.parseFloat(jsonObject.getString("popularity"));
            movie.releaseDate = jsonObject.getString("release_date");
            movie.avgScore = Float.parseFloat(jsonObject.getString("vote_average"));
            if (movie.avgScore > 5) {
                movie.type = MovieTypes.HIGH_SCORE;
            } else {
                movie.type = MovieTypes.LOW_SCORE;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movie;
    }

    public static ArrayList<Movie> fromJson(JSONArray jsonArray) {
        JSONObject movieJson;
        ArrayList<Movie> movies = new ArrayList<Movie>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); ++i) {
            try {
                movieJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Movie movie = Movie.fromJson(movieJson);
            if (movie != null) {
                movies.add(movie);
            }
        }

        return movies;
    }

    public int getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getPosterUrl() {
        return this.posterUrl;
    }

    public String getBackdropUrl() {
        return this.backdropUrl;
    }

    public String getOverview() {
        return this.overview;
    }

    public float getPopularity() {
        return this.populatiry;
    }

    public float getAvgScore() {
        return this.avgScore;
    }

    public MovieTypes getType() {
        return this.type;
    }

    public String getReleaseDate() {
        return this.releaseDate;
    }
}
