package com.example.popmovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
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
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w500";
    private static final String DIR_IMAGE_SIZE = "w342";


    private static final String BASE_TERM = "https://api.themoviedb.org/3/movie/";
    private static final String SIMILAR_TERM = "/similar?api_key=3b97af0112652688c49f023ecc57edb9";
    private static final String RECOMMENDATION_TERM = "/recommendations?api_key=3b97af0112652688c49f023ecc57edb9";
    private static final String CASTS_TERM = "/casts?api_key=3b97af0112652688c49f023ecc57edb9";
    private static final String VIDEO_TERM = "/videos?api_key=3b97af0112652688c49f023ecc57edb9&language=en-US";
    private static final String REVIEWS_TERM = "/reviews?api_key=3b97af0112652688c49f023ecc57edb9";
    private static final String EGYBEST_QUERY = "https://www.egy.best/explore/?q=";

    private ArrayList<Movie> sMovies = new ArrayList<>();
    private ArrayList<Movie> rMovies = new ArrayList<>();
    ArrayList<Review> reviews = new ArrayList<>();
    private List<Actor> actors = new ArrayList<>();



    private SimilarAdapter sAdapter;
    private SimilarAdapter rAdapter;
    private SimilarAdapter actorAdapter;
    private ReviewAdapter reviewAdapter;
    private FavoriteListViewModel viewModel;


    private String movieId;
    private String directorName = "";
    private String directorImagePath = "";

    @BindView(R.id.web_trailer) WebView mWebView;
    @BindView(R.id.img_movie) ImageView movieImageView;
    @BindView(R.id.tv_released_date) TextView dateTextView;
    @BindView(R.id.tv_vote_rate) TextView voteTextView;
    @BindView(R.id.tv_overview) TextView overviewTextView;
    @BindView(R.id.pb_pic) ProgressBar progressBar;
    @BindView(R.id.pb_video) ProgressBar videoProgressBar;
    @BindView(R.id.rv_similar) RecyclerView similarRecyclerView;
    @BindView(R.id.rv_reco) RecyclerView recommendedRecyclerView;
    @BindView(R.id.rv_cast) RecyclerView castRecyclerView;
    @BindView(R.id.tv_favorites) TextView favouriteButton;
    @BindView(R.id.tv_egybest) TextView egyBestButton;
    @BindView(R.id.label_cast) TextView labelCast;
    @BindView(R.id.label_similar) TextView labelSimilar;
    @BindView(R.id.label_reco) TextView labelRecommended;
    @BindView(R.id.rating_movie) RatingBar ratingBar;
    @BindView(R.id.rc_reviews) RecyclerView reviewsRecyclerView;
    @BindView(R.id.label_rev) TextView reviewLabel;
    @BindView(R.id.img_dir) ImageView directorImage;
    @BindView(R.id.tv_dir_name) TextView directorNameTextView;

    private AppDatabase db;
    private Movie movie;

    private int favFlag = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = AppDatabase.getInstance(this);



        viewModel = ViewModelProviders.of(this).get(FavoriteListViewModel.class);



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

        String year = "";
        for (int i=0; i<4; i++){
            year += releaseDate.charAt(i);
        }

        String egybestUrl = EGYBEST_QUERY+title+" ("+year+")";

        setTitle(title);
        dateTextView.setText(releaseDate);
        voteTextView.setText(vote);
        overviewTextView.setText(overview);
        ratingBar.setRating((Float.valueOf(vote)/10)*5);



        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        similarRecyclerView.setLayoutManager(layoutManager);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recommendedRecyclerView.setLayoutManager(layoutManager1);

        LinearLayoutManager castManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        castRecyclerView.setLayoutManager(castManager);

        LinearLayoutManager reviewsManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        reviewsRecyclerView.setLayoutManager(reviewsManager);

        new MoviesDetailsAsyncTask().execute(BASE_TERM+movieId+SIMILAR_TERM);
        new RecoMoviesDetailsAsyncTask().execute(BASE_TERM+movieId+RECOMMENDATION_TERM);
        new ActorAsyncTask().execute(BASE_TERM+movieId+CASTS_TERM);
        new VideoAsyncTask().execute(BASE_TERM+movieId+VIDEO_TERM);
        new ReviewAsyncTask().execute(BASE_TERM+movieId+REVIEWS_TERM);





        SharedPreferences preferences = getSharedPreferences(movieId, Context.MODE_PRIVATE);
        favFlag = preferences.getInt(movieId, 0);


        if (favFlag ==1){
            favouriteButton.setBackgroundColor(Color.GRAY);
            favouriteButton.setText("Marked As Favorite".toUpperCase());
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

        egyBestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(egybestUrl));
                startActivity(intent);
            }
        });

        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (favFlag ==0){
                    new AddToFavouriteAsyncTask().execute();
                    favouriteButton.setBackgroundColor(Color.GRAY);
                    favouriteButton.setText("Marked As Favorite".toUpperCase());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(movieId, 1);
                    editor.apply();
                    favFlag = 1;
                }else{
                    viewModel.deleteMovie(movie);
                    favouriteButton.setBackgroundResource(R.color.fav);
                    favouriteButton.setText("Mark As Favorite".toUpperCase());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(movieId, 0);
                    editor.apply();
                    favFlag = 0;
                    Toast.makeText(getApplicationContext(), "Deleted From Favourites", Toast.LENGTH_SHORT).show();
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

            sAdapter = new SimilarAdapter(MovieDetailsActivity.this, movies, false);
            similarRecyclerView.setAdapter(sAdapter);

            if (movies.isEmpty()){
                labelSimilar.setVisibility(View.GONE);
            }

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

            rAdapter = new SimilarAdapter(MovieDetailsActivity.this, movies, false);
            recommendedRecyclerView.setAdapter(rAdapter);

            if (movies.isEmpty()){
                labelRecommended.setVisibility(View.GONE);
            }

        }
    }

    public class ActorAsyncTask extends AsyncTask <String, Void, List<Actor>>{



        @Override
        protected List<Actor> doInBackground(String... siteUrl) {
            String text;


            try {

                URL url = new URL(siteUrl[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                text = stream2String(inputStream);

                actors = extractActorsFromJson(text);


                return actors;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return actors;
        }

        @Override
        protected void onPostExecute(List<Actor> actors) {

            actorAdapter = new SimilarAdapter(MovieDetailsActivity.this, actors, true);
            castRecyclerView.setAdapter(actorAdapter);

            if (actors.isEmpty()){
                labelCast.setVisibility(View.GONE);
            }

            directorNameTextView.setText(directorName);

            Picasso.get()
                    .load(BASE_URL+DIR_IMAGE_SIZE+directorImagePath)
                    .placeholder(R.mipmap.placeholder)
                    .into(directorImage);

        }
    }

    public class ReviewAsyncTask extends AsyncTask<String, Void, ArrayList<Review>>{
        @Override
        protected ArrayList<Review> doInBackground(String... site) {

            String text;

            try {

                URL url = new URL(site[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                text = stream2String(inputStream);

                reviews = extractReviewsFromJson(text);

                return reviews;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return reviews;
        }

        @Override
        protected void onPostExecute(ArrayList<Review> reviews) {
            reviewAdapter = new ReviewAdapter(MovieDetailsActivity.this, reviews);

            reviewsRecyclerView.setAdapter(reviewAdapter);

            if (reviews.isEmpty()){
                reviewLabel.setVisibility(View.GONE);
            }
        }
    }

    public class VideoAsyncTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... site) {

            String text;
            String key = "";

            try {

                URL url = new URL(site[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                text = stream2String(inputStream);

                key = extractVideoKey(text);

                return key;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return key;
        }

        @Override
        protected void onPostExecute(String s) {

            videoProgressBar.setVisibility(View.GONE);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
            mWebView.loadUrl("https://www.youtube.com/embed/"+s+"?autoplay=1&vq=small");
            mWebView.setWebChromeClient(new WebChromeClient());

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

    public List<Actor> extractActorsFromJson(String json){

        List<Actor> actors = new ArrayList<>();
        try {

            JSONObject root = new JSONObject(json);
            JSONArray cast = root.getJSONArray("cast");

            for (int i=0; i<cast.length(); i++){

                JSONObject currentActor = cast.getJSONObject(i);

                String name = currentActor.getString("name");
                String character = currentActor.getString("character");
                String profilePath = currentActor.getString("profile_path");

                actors.add(new Actor(name, character, profilePath));
            }

            JSONArray crew = root.getJSONArray("crew");
            for (int i=0; i<crew.length(); i++){
                JSONObject dirObject = crew.getJSONObject(i);

                if (dirObject.getString("job").equals("Director")){
                    directorName = dirObject.getString("name");
                    directorImagePath = dirObject.getString("profile_path");
                    break;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return actors;
    }

    public ArrayList<Review> extractReviewsFromJson(String json){

        ArrayList<Review> reviews = new ArrayList<>();

        try {

            JSONObject root = new JSONObject(json);
            JSONArray results = root.getJSONArray("results");

            for (int i=0; i<results.length(); i++){

                JSONObject currentResult = results.getJSONObject(i);

                String name = currentResult.getString("author");
                String content = currentResult.getString("content");

                reviews.add(new Review(name, content));
            }
            return reviews;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return reviews;
    }
    public String extractVideoKey(String json){

        try {

            JSONObject root = new JSONObject(json);

            JSONArray results = root.getJSONArray("results");
            if (results.length()>0){
                JSONObject trailer = results.getJSONObject(0);
                String key = trailer.getString("key");
                return key;
            }
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
