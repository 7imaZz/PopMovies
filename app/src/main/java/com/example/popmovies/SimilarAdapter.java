package com.example.popmovies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SimilarAdapter extends RecyclerView.Adapter<SimilarAdapter.MoviesViewHolder>{

    private static final String BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w342";

    public class MoviesViewHolder extends RecyclerView.ViewHolder{

        private ImageView movieImageView;
        private TextView actorNameTextView;
        private TextView characterNameTextView;
        private ProgressBar progressBar;

        public MoviesViewHolder(@NonNull View itemView) {
            super(itemView);
            movieImageView = itemView.findViewById(R.id.img_recommended);
            actorNameTextView = itemView.findViewById(R.id.tv_actor_name);
            characterNameTextView = itemView.findViewById(R.id.tv_char_name);
            progressBar = itemView.findViewById(R.id.pb_pho);

        }
    }

    private Context context;
    private ArrayList<Movie> movies;
    private List<Actor> actors;
    private boolean isActor;

    public SimilarAdapter(Context context, ArrayList<Movie> movies, boolean isActor) {
        this.context = context;
        this.movies = movies;
        this.isActor = isActor;
    }

    public SimilarAdapter(Context context, List<Actor> actors, boolean isActor) {
        this.context = context;
        this.actors = actors;
        this.isActor = isActor;
    }

    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder holder, int position) {
        if (!isActor){
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
            holder.characterNameTextView.setVisibility(View.GONE);
            holder.actorNameTextView.setVisibility(View.GONE);

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
        }else {
            Actor currentActor = actors.get(position);

            Picasso.get()
                    .load(BASE_URL+IMAGE_SIZE+currentActor.getProfilePath())
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

            holder.characterNameTextView.setVisibility(View.VISIBLE);
            holder.actorNameTextView.setVisibility(View.VISIBLE);

            holder.actorNameTextView.setText(currentActor.getName());
            holder.characterNameTextView.setText("("+currentActor.getCharacter()+")");
        }
    }

    @Override
    public int getItemCount() {
        if(!isActor)
            return movies.size();
        else
            return actors.size();
    }

}
