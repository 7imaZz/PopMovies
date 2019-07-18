package com.example.popmovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w500";

    private TextView titleTextView;
    private ImageView movieImageView;
    private TextView dateTextView;
    private TextView durationTextView;
    private TextView voteTextView;
    private TextView overviewTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        titleTextView = findViewById(R.id.tv_title);
        movieImageView = findViewById(R.id.img_movie);
        dateTextView = findViewById(R.id.tv_released_date);
        durationTextView = findViewById(R.id.tv_duration);
        voteTextView = findViewById(R.id.tv_vote_rate);
        overviewTextView = findViewById(R.id.tv_overview);

        String title = getIntent().getStringExtra(Constants.TITLE);
        String poster = getIntent().getStringExtra(Constants.POSTER_PATH);
        String releaseDate = getIntent().getStringExtra(Constants.RELEASE_DATE);
        String vote = getIntent().getStringExtra(Constants.VOTE_AVERAGE);
        String overview = getIntent().getStringExtra(Constants.OVERVIEW);

        titleTextView.setText(title);
        dateTextView.setText(releaseDate);
        voteTextView.setText(vote);
        overviewTextView.setText(overview);

        Picasso.get().load(BASE_URL+IMAGE_SIZE+poster).into(movieImageView);

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
