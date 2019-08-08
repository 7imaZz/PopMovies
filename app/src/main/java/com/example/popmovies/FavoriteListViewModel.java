package com.example.popmovies;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class FavoriteListViewModel extends AndroidViewModel{

    private final LiveData<List<Movie>> moviesListLiveData;

    private AppDatabase appDatabase;

    public FavoriteListViewModel(@NonNull Application application) {
        super(application);

        appDatabase = AppDatabase.getInstance(this.getApplication());

        moviesListLiveData = appDatabase.movieDao().getAllMovies();
    }

    public LiveData<List<Movie>> getMoviesListLiveData() {
        return moviesListLiveData;
    }

    public void deleteMovie(Movie movie){
        new deleteItemAsyncTask(appDatabase).execute(movie);
    }

    private static class deleteItemAsyncTask extends AsyncTask<Movie, Void, Void>{

        private AppDatabase db;

        public deleteItemAsyncTask(AppDatabase database) {
            this.db = database;
        }

        @Override
        protected Void doInBackground(Movie... voids) {
            db.movieDao().deleteMovie(voids[0].getMovieId());
            return null;
        }
    }
}
