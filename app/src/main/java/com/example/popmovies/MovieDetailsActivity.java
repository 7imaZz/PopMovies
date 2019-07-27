package com.example.popmovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w500";

    private static final String BASE_TERM = "https://api.themoviedb.org/3/movie/";
    private static final String SIMILAR_TERM = "/similar?api_key=3b97af0112652688c49f023ecc57edb9";
    private static final String RECOMMENDATION_TERM = "/recommendations?api_key=3b97af0112652688c49f023ecc57edb9";




    private ArrayList<Movie> sMovies = new ArrayList<>();
    private ArrayList<Movie> rMovies = new ArrayList<>();
    private SimilarAdapter sAdapter;
    private SimilarAdapter rAdapter;


    private String movieId;

    @BindView(R.id.tv_title) TextView titleTextView;
    @BindView(R.id.img_movie) ImageView movieImageView;
    @BindView(R.id.tv_released_date) TextView dateTextView;
    @BindView(R.id.tv_vote_rate) TextView voteTextView;
    @BindView(R.id.tv_overview) TextView overviewTextView;
    @BindView(R.id.pb_pic) ProgressBar progressBar;
    @BindView(R.id.rv_similar) RecyclerView similarRecyclerView;
    @BindView(R.id.rv_reco) RecyclerView recommendedRecyclerView;
    @BindView(R.id.tv_favorites) TextView favouriteButton;

    private AppDatabase db;
    private Movie movie;

    private int favFlag = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = AppDatabase.getInstance(this);





        /*titleTextView = findViewById(R.id.tv_title);
        movieImageView = findViewById(R.id.img_movie);
        dateTextView = findViewById(R.id.tv_released_date);
        durationTextView = findViewById(R.id.tv_duration);
        voteTextView = findViewById(R.id.tv_vote_rate);
        overviewTextView = findViewById(R.id.tv_overview);*/
        ButterKnife.bind(this);

        String title = getIntent().getStringExtra(Constants.TITLE);
        String poster = getIntent().getStringExtra(Constants.POSTER_PATH);
        String releaseDate = getIntent().getStringExtra(Constants.RELEASE_DATE);
        String vote = getIntent().getStringExtra(Constants.VOTE_AVERAGE);
        String overview = getIntent().getStringExtra(Constants.OVERVIEW);
        movieId = getIntent().getStringExtra(Constants._ID);

        movie = new Movie(title, poster, overview, Double.parseDouble(vote), releaseDate, movieId);

        titleTextView.setText(title);
        dateTextView.setText(releaseDate);
        voteTextView.setText(vote);
        overviewTextView.setText(overview);



        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        similarRecyclerView.setLayoutManager(layoutManager);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recommendedRecyclerView.setLayoutManager(layoutManager1);

        new MoviesDetailsAsyncTask().execute(BASE_TERM+movieId+SIMILAR_TERM);
        new RecoMoviesDetailsAsyncTask().execute(BASE_TERM+movieId+RECOMMENDATION_TERM);


        SharedPreferences preferences = getSharedPreferences(movieId, Context.MODE_PRIVATE);
        favFlag = preferences.getInt(movieId, 0);


        if (favFlag ==1){
            favouriteButton.setBackgroundColor(Color.GRAY);
        }


        Picasso.get()
                .load(BASE_URL+IMAGE_SIZE+poster)
                .placeholder(R.mipmap.placeholder)
                .into(movieImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (favFlag ==0){
                    new AddToFavouriteAsyncTask().execute();
                    favouriteButton.setBackgroundColor(Color.GRAY);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(movieId, 1);
                    editor.apply();
                    favFlag = 1;
                }else{
                    new DeleteFromFavouriteAsyncTask().execute();
                    favouriteButton.setBackgroundResource(R.color.fav);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(movieId, 0);
                    editor.apply();
                    favFlag = 0;
                }
            }


        });

    }


    public class MoviesDetailsAsyncTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(String... siteUrl) {

            String text;

            if (sMovies.isEmpty()){
                try {

                    URL url = new URL(siteUrl[0]);

                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    text = stream2String(inputStream);

                    sMovies = extractFromJson(text);

                    return sMovies;


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                return sMovies;
            }
            return sMovies;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {

            sAdapter = new SimilarAdapter(MovieDetailsActivity.this, movies);

            similarRecyclerView.setAdapter(sAdapter);

        }
    }


    public class RecoMoviesDetailsAsyncTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(String... siteUrl) {

            String text;

            if (rMovies.isEmpty()){
                try {

                    URL url = new URL(siteUrl[0]);

                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    text = stream2String(inputStream);

                    rMovies = extractFromJson(text);

                    return rMovies;


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                return rMovies;
            }
            return rMovies;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {

            rAdapter = new SimilarAdapter(MovieDetailsActivity.this, movies);

            recommendedRecyclerView.setAdapter(rAdapter);

        }
    }

    public class AddToFavouriteAsyncTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            db.movieDao().insertMovie(movie);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(getApplicationContext(), "Movie Added To Favourites", Toast.LENGTH_SHORT).show();
        }
    }



    public class DeleteFromFavouriteAsyncTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            db.movieDao().deleteMovie(movieId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(getApplicationContext(), "Deleted From Favourites", Toast.LENGTH_SHORT).show();
        }
    }


    public String stream2String(InputStream inputStream){

        String line;
        StringBuilder text = new StringBuilder();

        BufferedReader reader =  new BufferedReader(new InputStreamReader(inputStream));

        try{
            while((line = reader.readLine()) != null){
                text.append(line);
            }
        }catch (IOException e){}

        return text.toString();
    }


    public ArrayList<Movie> extractFromJson(String json){
        ArrayList<Movie> movies = new ArrayList<>();
        try {

            JSONObject root = new JSONObject(json);
            JSONArray results = root.getJSONArray(Constants.RESULTS);

            for (int i=0; i<results.length(); i++){

                JSONObject currentMovie = results.getJSONObject(i);

                String title = currentMovie.getString(Constants.TITLE);
                String posterPath = currentMovie.getString(Constants.POSTER_PATH);
                String overview = currentMovie.getString(Constants.OVERVIEW);
                double voteAvg = currentMovie.getDouble(Constants.VOTE_AVERAGE);
                String releaseDate = currentMovie.getString(Constants.RELEASE_DATE);
                String movieId = currentMovie.getString(Constants._ID);

                movies.add(new Movie(title, posterPath, overview, voteAvg, releaseDate, movieId));
            }

            return movies;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
