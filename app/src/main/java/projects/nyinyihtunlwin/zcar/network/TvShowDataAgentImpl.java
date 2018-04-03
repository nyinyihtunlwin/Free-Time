package projects.nyinyihtunlwin.zcar.network;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import projects.nyinyihtunlwin.zcar.data.vo.tvshows.TvShowVO;
import projects.nyinyihtunlwin.zcar.events.MoviesiEvents;
import projects.nyinyihtunlwin.zcar.events.TvShowsEvents;
import projects.nyinyihtunlwin.zcar.network.responses.movies.GetMovieCreditsResponse;
import projects.nyinyihtunlwin.zcar.network.responses.movies.GetMovieReviewsResponse;
import projects.nyinyihtunlwin.zcar.network.responses.movies.GetMovieTrailersResponse;
import projects.nyinyihtunlwin.zcar.network.responses.tvshows.TvAiringTodayResponse;
import projects.nyinyihtunlwin.zcar.network.responses.tvshows.TvMostPopularResponse;
import projects.nyinyihtunlwin.zcar.network.responses.tvshows.TvOnTheAirResponse;
import projects.nyinyihtunlwin.zcar.network.responses.tvshows.TvTopRatedResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Dell on 2/22/2018.
 */

public class TvShowDataAgentImpl implements TvShowDataAgent {

    private static TvShowDataAgentImpl objectInstance;

    private MovieAPI movieAPI;

    private TvShowDataAgentImpl() {
        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        // time 60 sec is optimal.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(okHttpClient)
                .build();

        movieAPI = retrofit.create(MovieAPI.class);
    }

    public static TvShowDataAgentImpl getObjectInstance() {
        if (objectInstance == null) {
            objectInstance = new TvShowDataAgentImpl();
        }
        return objectInstance;
    }


    @Override
    public void loadTvAiringToday(String apiKey, int pageNo, String region, final Context context) {
        Call<TvAiringTodayResponse> loadTvAiringTodayCall = movieAPI.loadTvAiringToday(apiKey, pageNo);
        loadTvAiringTodayCall.enqueue(new MovieCallback<TvAiringTodayResponse>() {
            @Override
            public void onResponse(Call<TvAiringTodayResponse> call, Response<TvAiringTodayResponse> response) {
                TvAiringTodayResponse getTvAiringTodayResponse = response.body();
                if (getTvAiringTodayResponse != null
                        && getTvAiringTodayResponse.getTvShows().size() > 0) {
                    TvShowsEvents.TvAiringTodayEvent tvAiringTodayEvent = new TvShowsEvents.TvAiringTodayEvent(
                            getTvAiringTodayResponse.getPage(), getTvAiringTodayResponse.getTvShows(), context
                    );
                    EventBus.getDefault().post(tvAiringTodayEvent);
                }
            }
        });
    }

    @Override
    public void loadTvOnTheAir(String apiKey, int pageNo, String region, final Context context) {
        Call<TvOnTheAirResponse> loadTvOnTheAirCall = movieAPI.loadTvOnTheAir(apiKey, pageNo);
        loadTvOnTheAirCall.enqueue(new MovieCallback<TvOnTheAirResponse>() {
            @Override
            public void onResponse(Call<TvOnTheAirResponse> call, Response<TvOnTheAirResponse> response) {
                TvOnTheAirResponse getTvOnTheAirResponse = response.body();
                if (getTvOnTheAirResponse != null
                        && getTvOnTheAirResponse.getTvShows().size() > 0) {
                    TvShowsEvents.TvOnTheAirEvent tvOnTheAirEvent = new TvShowsEvents.TvOnTheAirEvent(
                            getTvOnTheAirResponse.getPage(), getTvOnTheAirResponse.getTvShows(), context
                    );
                    EventBus.getDefault().post(tvOnTheAirEvent);
                }
            }
        });
    }

    @Override
    public void loadTvMostPopular(String apiKey, int pageNo, String region, final Context context) {
        Call<TvMostPopularResponse> loadTvMostPopularCall = movieAPI.loadTvMostPopular(apiKey, pageNo);
        loadTvMostPopularCall.enqueue(new MovieCallback<TvMostPopularResponse>() {
            @Override
            public void onResponse(Call<TvMostPopularResponse> call, Response<TvMostPopularResponse> response) {
                TvMostPopularResponse getTvMostPopularResponse = response.body();
                if (getTvMostPopularResponse != null
                        && getTvMostPopularResponse.getTvShows().size() > 0) {
                    TvShowsEvents.TvMostPopularEvent tvMostPopularEvent = new TvShowsEvents.TvMostPopularEvent(
                            getTvMostPopularResponse.getPage(), getTvMostPopularResponse.getTvShows(), context
                    );
                    EventBus.getDefault().post(tvMostPopularEvent);
                }
            }
        });
    }

    @Override
    public void loadTvTopRated(String apiKey, int pageNo, String region, final Context context) {
        Call<TvTopRatedResponse> loadTvTopRatedCall = movieAPI.loadTvTopRated(apiKey, pageNo);
        loadTvTopRatedCall.enqueue(new MovieCallback<TvTopRatedResponse>() {
            @Override
            public void onResponse(Call<TvTopRatedResponse> call, Response<TvTopRatedResponse> response) {
                TvTopRatedResponse getTvTopRatedResponse = response.body();
                if (getTvTopRatedResponse != null
                        && getTvTopRatedResponse.getTvShows().size() > 0) {
                    TvShowsEvents.TvTopRatedEvent tvTopRatedEvent = new TvShowsEvents.TvTopRatedEvent(
                            getTvTopRatedResponse.getPage(), getTvTopRatedResponse.getTvShows(), context
                    );
                    EventBus.getDefault().post(tvTopRatedEvent);
                }
            }
        });
    }

    @Override
    public void loadTvShowDetails(int movieId, String apiKey) {
        Call<TvShowVO> loadTVShowDetails = movieAPI.loadTVShowDetails(movieId, apiKey);
        loadTVShowDetails.enqueue(new Callback<TvShowVO>() {
            @Override
            public void onResponse(Call<TvShowVO> call, Response<TvShowVO> response) {
                TvShowVO movieDetailsResponse = response.body();
                if (movieDetailsResponse != null) {
                    EventBus.getDefault().post(new TvShowsEvents.TvShowDetailsDataLoadedEvent(movieDetailsResponse));
                    //  Log.e("details", "Runtime" + movieDetailsResponse.getRuntime());
                }
            }

            @Override
            public void onFailure(Call<TvShowVO> call, Throwable t) {
                TvShowsEvents.ErrorInvokingAPIEvent errorEvent
                        = new TvShowsEvents.ErrorInvokingAPIEvent(t.getMessage());
                EventBus.getDefault().post(errorEvent);
            }
        });
    }

    @Override
    public void loadTvShowTrailers(int movieId, String apiKey) {
        Call<GetMovieTrailersResponse> loadMovieTrailersResponse = movieAPI.loadTvShowTrailers(movieId, apiKey);
        loadMovieTrailersResponse.enqueue(new MovieCallback<GetMovieTrailersResponse>() {
            @Override
            public void onResponse(Call<GetMovieTrailersResponse> call, Response<GetMovieTrailersResponse> response) {
                GetMovieTrailersResponse getMovieTrailersResponse = response.body();
                if (getMovieTrailersResponse != null
                        && getMovieTrailersResponse.getVideos().size() > 0) {
                    EventBus.getDefault().post(new MoviesiEvents.MovieTrailersDataLoadedEvent(getMovieTrailersResponse.getVideos()));
                    Log.e("Trailers:", getMovieTrailersResponse.getVideos().size() + "");
                }
            }
        });
    }

    @Override
    public void loadTvShowReviews(int movieId, String apiKey) {
        Call<GetMovieReviewsResponse> loadMovieReviewsResponse = movieAPI.loadTvShowReviews(movieId, apiKey);
        loadMovieReviewsResponse.enqueue(new MovieCallback<GetMovieReviewsResponse>() {
            @Override
            public void onResponse(Call<GetMovieReviewsResponse> call, Response<GetMovieReviewsResponse> response) {
                GetMovieReviewsResponse getMovieReviewsResponse = response.body();
                if (getMovieReviewsResponse != null) {
                    if (getMovieReviewsResponse.getReviews().size() > 0) {
                        EventBus.getDefault().post(new MoviesiEvents.MovieReviewsDataLoadedEvent(getMovieReviewsResponse.getReviews()));
                    } else {
                        EventBus.getDefault().post(new MoviesiEvents.ErrorInvokingAPIEvent("No reviews for now!"));
                    }
                }
            }
        });
    }

    @Override
    public void loadTvShowCredits(int movieId, String apiKey) {
        Call<GetMovieCreditsResponse> loadMovieCreditsResponse = movieAPI.loadTvShowCredits(movieId, apiKey);
        loadMovieCreditsResponse.enqueue(new MovieCallback<GetMovieCreditsResponse>() {
            @Override
            public void onResponse(Call<GetMovieCreditsResponse> call, Response<GetMovieCreditsResponse> response) {
                GetMovieCreditsResponse getMovieCreditsResponse = response.body();
                if (getMovieCreditsResponse != null
                        && getMovieCreditsResponse.getCasts().size() > 0) {
                    EventBus.getDefault().post(new MoviesiEvents.MovieCreditsDataLoadedEvent(getMovieCreditsResponse.getCasts()));
                }
            }
        });
    }
}
