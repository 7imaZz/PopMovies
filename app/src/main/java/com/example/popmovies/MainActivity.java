package com.example.popmovies;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


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

public class MainActivity extends AppCompatActivity{


    private static final String POP_URL = "https://api.themoviedb.org/3/movie/popular?api_key=3b97af0112652688c49f023ecc57edb9&language=en-US&page=";
    private static final String TOP_RATED_URL = "https://api.themoviedb.org/3/movie/top_rated?api_key=3b97af0112652688c49f023ecc57edb9&language=en-US&page=";
    private static final String NOW_PLAYING = "https://api.themoviedb.org/3/movie/now_playing?api_key=3b97af0112652688c49f023ecc57edb9&language=en-US&page=";
    private static final String UPCOMING = "https://api.themoviedb.org/3/movie/upcoming?api_key=3b97af0112652688c49f023ecc57edb9&language=en-US&page=";

    private String searchedMovie;
    private static final String SEARCH_API = "https://api.themoviedb.org/3/search/movie?api_key=3b97af0112652688c49f023ecc57edb9&query=";
    private static final String PAGE = "&page=";

    //To Define Which URL Will Be Token, POP Or TOP
    private static final String URL_PATH = "url";

    private GridView gridView;
    private ProgressBar progressBar;
    private TextView noNetworkTextView;
    private Button previousButton;
    private Button nextButton;

    SearchView searchView;

    private ArrayList<Movie> movies = new ArrayList<>();

    private MoviesAdapter adapter;

    private int pageNum;
    private int totalPages;

    private int itemPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        gridView = findViewById(R.id.grid_view);
        progressBar = findViewById(R.id.progress);
        noNetworkTextView = findViewById(R.id.tv_empty);
        previousButton = findViewById(R.id.btn_prev);
        nextButton = findViewById(R.id.btn_next);

        if (getIntent().getStringExtra("num")==null){
            pageNum = 1;
        }else {
            pageNum = Integer.parseInt(getIntent().getStringExtra("num"));
        }


            if (getIntent().getStringExtra(URL_PATH) == null) {
                noNetworkTextView.setVisibility(View.GONE);
                new MoviesAsyncTask().execute(POP_URL + pageNum);
            } else {
                noNetworkTextView.setVisibility(View.GONE);
                new MoviesAsyncTask().execute(getIntent().getStringExtra(URL_PATH) + pageNum);

            }

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                itemPosition = firstVisibleItem;
                if(firstVisibleItem + visibleItemCount >= totalItemCount){
                    nextButton.setVisibility(View.VISIBLE);
                    previousButton.setVisibility(View.VISIBLE);
                }else {
                    nextButton.setVisibility(View.GONE);
                    previousButton.setVisibility(View.GONE);
                }
            }
        });

        if (pageNum == 1){
            previousButton.setEnabled(false);
        }else {
            previousButton.setEnabled(true);
        }

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageNum++;
                getIntent().putExtra("num", String.valueOf(pageNum));
                finish();
                startActivity(getIntent());
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageNum--;
                getIntent().putExtra("num", String.valueOf(pageNum));
                finish();
                startActivity(getIntent());
            }
        });



    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("position", itemPosition);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        int currPos = savedInstanceState.getInt("position");

        gridView.setSelection(currPos);

    }

    private void openInternetSettings() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
        startActivity(intent);
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
            return movies;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {

            progressBar.setVisibility(View.INVISIBLE);

            adapter = new MoviesAdapter(MainActivity.this, movies);

            gridView.setAdapter(adapter);

            if (getIntent().getStringExtra(URL_PATH) == null){
                setTitle("Most Popular ("+pageNum+" Of "+totalPages+")");
            }else{
                setTitle(getIntent().getStringExtra("title")+" ("+pageNum+" Of "+totalPages+")");
            }

            if (pageNum == totalPages){
                nextButton.setEnabled(false);
            }else {
                nextButton.setEnabled(true);
            }
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

            totalPages = root.getInt(Constants.TOTAL_PAGES);

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
        getMenuInflater().inflate(R.menu.main_menu, menu);
        // Retrieve the SearchView and plug it into SearchManager
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchedMovie = s.replaceAll(" ", "%20");
                pageNum = 1;
                getIntent().putExtra("num", pageNum);
                getIntent().putExtra(URL_PATH, SEARCH_API+searchedMovie+PAGE);
                getIntent().putExtra("title", "Found");
                finish();
                startActivity(getIntent());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id =  item.getItemId();

        if (id == R.id.action_favourites){
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
        }else if (id==R.id.menu_most_pop){
            pageNum = 1;
            getIntent().putExtra("num", pageNum);
            getIntent().putExtra(URL_PATH, POP_URL);
            getIntent().putExtra("title", "Most Popular");
            finish();
            startActivity(getIntent());
        }else if (id==R.id.menu_highest_rated){
            pageNum = 1;
            getIntent().putExtra("num", pageNum);
            getIntent().putExtra(URL_PATH, TOP_RATED_URL);
            getIntent().putExtra("title", "Highest Rated");
            finish();
            startActivity(getIntent());
        }else if (id==R.id.menu_now_playing){
            pageNum = 1;
            getIntent().putExtra("num", pageNum);
            getIntent().putExtra(URL_PATH, NOW_PLAYING);
            getIntent().putExtra("title", "Now Playing");
            finish();
            startActivity(getIntent());
        }else if(id==R.id.menu_upcoming){
            pageNum = 1;
            getIntent().putExtra("num", pageNum);
            getIntent().putExtra(URL_PATH, UPCOMING);
            getIntent().putExtra("title", "Upcoming");
            finish();
            startActivity(getIntent());
        }
        return super.onOptionsItemSelected(item);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
