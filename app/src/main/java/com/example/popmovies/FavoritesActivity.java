package com.example.popmovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private AppDatabase db;

    private GridView gridView;


    private List<Movie> movies = new ArrayList<>();
    private MoviesAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = AppDatabase.getInstance(this);

        gridView = findViewById(R.id.grid_view_fav);

        new GetMoviesAsyncTask().execute();
    }

    public class GetMoviesAsyncTask extends AsyncTask<Void, Void, List<Movie>>{

        @Override
        protected List<Movie> doInBackground(Void... voids) {
            movies = db.movieDao().getAllMovies();
            return movies;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            adapter = new MoviesAdapter(FavoritesActivity.this, movies);
            gridView.setAdapter(adapter);
            setTitle("Favorites ("+movies.size()+")");

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetMoviesAsyncTask().execute();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
