package projects.nyinyihtunlwin.freetime.network;

import android.content.Context;

/**
 * Created by Nyi Nyi Htun Lwin on 12/6/2017.
 */

public interface MovieDataAgent {

    void loadPopularMovies(String apiKey, int pageNo, String region, Context context);

    void loadNowOnCinemaMovies(String apiKey, int pageNo, String region, Context context);

    void loadUpcomingMovies(String apiKey, int pageNo, String region, Context context);

    void loadTopRatedMovies(String apiKey, int pageNo, String region, Context context);

    void loadMovieGenres(String apiKey, Context context);

    void loadMovieDetails(int movieId, String apiKey);

    void loadMovieTrailers(int movieId, String apiKey);

    void loadMovieReviews(int movieId, String apiKey);

    void loadMovieCredits(int movieId, String apiKey);

    void loadSimilarMovies(int movieid,String apiKey);

    void startSearching(String apiKey, int pageNo, String query);
}
