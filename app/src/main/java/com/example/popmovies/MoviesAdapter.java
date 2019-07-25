package com.example.popmovies;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesAdapter extends BaseAdapter {

    private static final String BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w342";

    private Context context;
    private ArrayList<Movie> movies;

    public MoviesAdapter(Context context, ArrayList<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int i) {
        return movies.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.grid_item, viewGroup, false);
        }

        final Movie currentMovie = (Movie) getItem(i);

        ImageView posterImageView = view.findViewById(R.id.img_poster);
        ProgressBar progressBar = view.findViewById(R.id.pb_image);
        TextView titleTextView = view.findViewById(R.id.tv_movie_title);

        titleTextView.setText(currentMovie.getTitle());

        posterImageView.setContentDescription(currentMovie.getTitle());

        Picasso.get().load(BASE_URL+IMAGE_SIZE+currentMovie.getPosterPath())
                .placeholder(R.mipmap.placeholder)
                .fit()
                .into(posterImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                        titleTextView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        titleTextView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });

        posterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, MovieDetailsActivity.class);

                intent.putExtra(Constants.TITLE, currentMovie.getTitle());
                intent.putExtra(Constants.POSTER_PATH, currentMovie.getPosterPath());
                intent.putExtra(Constants.VOTE_AVERAGE, currentMovie.getVoteAverage()+"");
                intent.putExtra(Constants.OVERVIEW, currentMovie.getOverview());
                intent.putExtra(Constants.RELEASE_DATE, currentMovie.getReleaseDate());
                intent.putExtra(Constants._ID, currentMovie.getMovieId());

                context.startActivity(intent);
            }
        });

        return view;
    }

    public ArrayList<Movie> getMovies() {
        return movies;
    }
}
