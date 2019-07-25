package com.example.popmovies;

public class Movie {

    private String title;
    private String posterPath;
    private String overview;
    private Double voteAverage;
    private String releaseDate;
    private String movieId;

    public Movie(String title, String posterPath) {
        this.posterPath = posterPath;
    }

    public Movie(String title, String posterPath, String overview, Double voteAverage, String releaseDate) {
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    public Movie(String title, String posterPath, String overview, Double voteAverage, String releaseDate, String movieId) {
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getMovieId() {
        return movieId;
    }
}
