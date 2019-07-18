package com.example.popmovies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String RESULTS = "results";
    private static final String TITLE = "title";
    private static final String POSTER_PATH = "poster_path";
    private static final String OVERVIEW = "overview";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String RELEASE_DATE = "release_date";

    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/popular?api_key=3b97af0112652688c49f023ecc57edb9";

    private GridView gridView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = findViewById(R.id.grid_view);
        progressBar = findViewById(R.id.progress);

        new MoviesAsyncTask().execute(BASE_URL);

    }

    public class MoviesAsyncTask extends AsyncTask<String, Void, ArrayList<Movie>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... siteUrl) {

            String text;
            ArrayList<Movie> movies;

            try {

                URL url = new URL(siteUrl[0]);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.connect();

                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                text = stream2String(inputStream);

                movies = extractFromJson(text);

                return movies;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {

            progressBar.setVisibility(View.INVISIBLE);

            MoviesAdapter adapter = new MoviesAdapter(MainActivity.this, movies);

            gridView.setAdapter(adapter);
        }
    }

    public String stream2String(InputStream inputStream){

        String line;
        String text = "";

        BufferedReader reader =  new BufferedReader(new InputStreamReader(inputStream));

        try{
            while((line = reader.readLine()) != null){
                text += line;
            }
        }catch (IOException e){}

        return text;
    }


    public ArrayList<Movie> extractFromJson(String json){
        ArrayList<Movie> movies = new ArrayList<>();
        try {

            JSONObject root = new JSONObject(json);
            JSONArray results = root.getJSONArray(RESULTS);

            if (results==null){
                return null;
            }

            for (int i=0; i<results.length(); i++){

                JSONObject currentMovie = results.getJSONObject(i);

                String title = currentMovie.getString(TITLE);
                String posterPath = currentMovie.getString(POSTER_PATH);
                String overview = currentMovie.getString(OVERVIEW);
                double voteAvg = currentMovie.getDouble(VOTE_AVERAGE);
                String releaseDate = currentMovie.getString(RELEASE_DATE);

                movies.add(new Movie(title, posterPath, overview, voteAvg, releaseDate));
            }

            return movies;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
