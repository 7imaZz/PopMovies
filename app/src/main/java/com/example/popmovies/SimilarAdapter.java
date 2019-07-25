package com.example.popmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SimilarAdapter extends RecyclerView.Adapter<SimilarAdapter.MoviesViewHolder>{

    private static final String BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w342";

    public class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ItemClickListener itemClickListener;

        private ImageView movieImageView;
        private ProgressBar progressBar;

        public MoviesViewHolder(@NonNull View itemView) {
            super(itemView);
            movieImageView = itemView.findViewById(R.id.img_recommended);
            progressBar = itemView.findViewById(R.id.pb_pho);

            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }
    }

    private Context context;
    private ArrayList<Movie> movies;

    public SimilarAdapter(Context context, ArrayList<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder holder, int position) {

        Movie currentMovie = movies.get(position);

        Picasso.get()
                .load(BASE_URL+IMAGE_SIZE+currentMovie.getPosterPath())
                .placeholder(R.mipmap.placeholder)
                .fit()
                .into(holder.movieImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

        holder.movieImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) context).getIntent().putExtra(Constants.TITLE, currentMovie.getTitle());
                ((Activity) context).getIntent().putExtra(Constants.POSTER_PATH, currentMovie.getPosterPath());
                ((Activity) context).getIntent().putExtra(Constants.VOTE_AVERAGE, currentMovie.getVoteAverage()+"");
                ((Activity) context).getIntent().putExtra(Constants.OVERVIEW, currentMovie.getOverview());
                ((Activity) context).getIntent().putExtra(Constants.RELEASE_DATE, currentMovie.getReleaseDate());
                ((Activity) context).getIntent().putExtra(Constants._ID, currentMovie.getMovieId());

                ((Activity) context).finish();

                context.startActivity(((Activity) context).getIntent());
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

}
