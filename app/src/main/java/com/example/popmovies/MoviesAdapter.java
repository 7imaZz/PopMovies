package com.example.popmovies;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class MoviesAdapter extends BaseAdapter implements Filterable {

    private static final String BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w342";

    private Context context;
    private List<Movie> movies;
    private List<Movie> moviesFull;


    public MoviesAdapter(Context context) {
        this.context = context;
    }

    public MoviesAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
        moviesFull = new ArrayList<>(movies);
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
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

    @Override
    public Filter getFilter() {
        return moviesFilter;
    }

    private Filter moviesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Movie> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(moviesFull);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Movie movie : moviesFull){
                    if (movie.getTitle().toLowerCase().contains(filterPattern)){
                        filteredList.add(movie);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            movies.clear();
            movies.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };
}
