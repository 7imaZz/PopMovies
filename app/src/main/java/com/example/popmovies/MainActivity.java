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

        movies.add(new Movie("Spider-Man","/rjbNpRMoVvqHmhmksbokcyCr7wn.jpg"));
        movies.add(new Movie("Spider-Man","/rjbNpRMoVvqHmhmksbokcyCr7wn.jpg"));
        movies.add(new Movie("Spider-Man","/rjbNpRMoVvqHmhmksbokcyCr7wn.jpg"));
        movies.add(new Movie("Spider-Man","/rjbNpRMoVvqHmhmksbokcyCr7wn.jpg"));
        movies.add(new Movie("Spider-Man","/rjbNpRMoVvqHmhmksbokcyCr7wn.jpg"));
        movies.add(new Movie("Spider-Man","/rjbNpRMoVvqHmhmksbokcyCr7wn.jpg"));
        movies.add(new Movie("Spider-Man","/rjbNpRMoVvqHmhmksbokcyCr7wn.jpg"));
        movies.add(new Movie("Spider-Man","/rjbNpRMoVvqHmhmksbokcyCr7wn.jpg"));
        movies.add(new Movie("Spider-Man","/rjbNpRMoVvqHmhmksbokcyCr7wn.jpg"));
        movies.add(new Movie("Spider-Man","/rjbNpRMoVvqHmhmksbokcyCr7wn.jpg"));
        movies.add(new Movie("Spider-Man","/rjbNpRMoVvqHmhmksbokcyCr7wn.jpg"));
        movies.add(new Movie("Spider-Man","/rjbNpRMoVvqHmhmksbokcyCr7wn.jpg"));

        MoviesAdapter adapter = new MoviesAdapter(this, movies);

        gridView.setAdapter(adapter);

    }
}
