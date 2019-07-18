package com.example.popmovies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.GridView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = findViewById(R.id.grid_view);

        ArrayList<Movie> movies = new ArrayList<>();

        movies.add(new Movie("Spider-Man","\\/rjbNpRMoVvqHmhmksbokcyCr7wn.jpg"));
        movies.add(new Movie("Spider-Man","\\/dzBtMocZuJbjLOXvrl4zGYigDzh.jpg"));
        movies.add(new Movie("Spider-Man","\\/xRWht48C2V8XNfzvPehyClOvDni.jpg"));
        movies.add(new Movie("Spider-Man","\\/w9kR8qbmQ01HwnvK4alvnQ2ca0L.jpg"));
        movies.add(new Movie("Spider-Man","\\/bk8LyaMqUtaQ9hUShuvFznQYQKR.jpg"));
        movies.add(new Movie("Spider-Man","\\/ziEuG1essDuWuC5lpWUaw1uXY2O.jpg"));
        movies.add(new Movie("Spider-Man","\\/86Y6qM8zTn3PFVfCm9J98Ph7JEB.jpg"));
        movies.add(new Movie("Spider-Man","\\/AtsgWhDnHTq68L0lLsUrCnM7TjG.jpg"));
        movies.add(new Movie("Spider-Man","\\/or06FN3Dka5tukK1e9sl16pB3iy.jpg"));
        movies.add(new Movie("Spider-Man","\\/jpfkzbIXgKZqCZAkEkFH2VYF63s.jpg"));
        movies.add(new Movie("Spider-Man","\\/A7XkpLfNH0El2yyDLc4b0KLAKvE.jpg"));
        movies.add(new Movie("Spider-Man","\\/7WsyChQLEftFiDOVTGkv3hFpyyt.jpg"));

        MoviesAdapter adapter = new MoviesAdapter(this, movies);

        gridView.setAdapter(adapter);

    }
}
