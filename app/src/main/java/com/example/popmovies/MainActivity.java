package com.example.popmovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;


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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String RESULTS = "results";


    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/popular?api_key=3b97af0112652688c49f023ecc57edb9";

    private GridView gridView;
    private ProgressBar progressBar;
    private TextView noNetworkTextView;

    private ArrayList<Movie> popList = new ArrayList<>();
    private ArrayList<Movie> rateList = new ArrayList<>();
    private ArrayList<Movie> movies = new ArrayList<>();

    private MoviesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = findViewById(R.id.grid_view);
        progressBar = findViewById(R.id.progress);
        noNetworkTextView = findViewById(R.id.tv_empty);

        noNetworkTextView.setVisibility(View.GONE);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()){
            new MoviesAsyncTask().execute(BASE_URL);
        }else{
            progressBar.setVisibility(View.GONE);
            noNetworkTextView.setVisibility(View.VISIBLE);
        }

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

            if (movies.isEmpty()){
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
            }else {
                return movies;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {

            progressBar.setVisibility(View.INVISIBLE);

            popList = movies;

            adapter = new MoviesAdapter(MainActivity.this, popList);

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

                String title = currentMovie.getString(Constants.TITLE);
                String posterPath = currentMovie.getString(Constants.POSTER_PATH);
                String overview = currentMovie.getString(Constants.OVERVIEW);
                double voteAvg = currentMovie.getDouble(Constants.VOTE_AVERAGE);
                String releaseDate = currentMovie.getString(Constants.RELEASE_DATE);

                movies.add(new Movie(title, posterPath, overview, voteAvg, releaseDate));
            }

            return movies;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id =  item.getItemId();
        if (id==R.id.menu_most_pop){
            adapter = new MoviesAdapter(getApplicationContext(), popList);
            finish();
            startActivity(getIntent());
            item.setEnabled(false);
        }else if (id==R.id.menu_highest_rated){

            noNetworkTextView.setVisibility(View.INVISIBLE);

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()){
                rateList = movies;
                Collections.sort(rateList, new Comparator<Movie>(){
                    @Override
                    public int compare(Movie movie1, Movie movie2) {
                        return Double.compare(movie1.getVoteAverage(), movie2.getVoteAverage());
                    }
                });


                Collections.reverse(rateList);
                movies = rateList;
                new MoviesAsyncTask().execute(BASE_URL);
                item.setEnabled(false);
            }else{
                noNetworkTextView.setVisibility(View.VISIBLE);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
