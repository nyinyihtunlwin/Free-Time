package projects.nyinyihtunlwin.zcar.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.annotations.SerializedName;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import projects.nyinyihtunlwin.zcar.R;
import projects.nyinyihtunlwin.zcar.adapters.CastAdapter;
import projects.nyinyihtunlwin.zcar.adapters.GenreAdapter;
import projects.nyinyihtunlwin.zcar.adapters.TrailersAdapter;
import projects.nyinyihtunlwin.zcar.data.models.MovieModel;
import projects.nyinyihtunlwin.zcar.data.vo.GenreVO;
import projects.nyinyihtunlwin.zcar.data.vo.MovieVO;
import projects.nyinyihtunlwin.zcar.data.vo.ReviewVO;
import projects.nyinyihtunlwin.zcar.delegates.MovieDetailsDelegate;
import projects.nyinyihtunlwin.zcar.events.RestApiEvents;
import projects.nyinyihtunlwin.zcar.network.MovieDataAgent;
import projects.nyinyihtunlwin.zcar.persistence.MovieContract;
import projects.nyinyihtunlwin.zcar.utils.AppConstants;

public class MovieDetailsActivity extends BaseActivity implements MovieDetailsDelegate, View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String KEY_MOVIE_ID = "movie_id";

    public static final int MOVIE_DETAILS_LOADER_ID = 1000001;
    public static final int MOVIE_GENRES_LOADER_ID = 1000002;


    @BindView(R.id.iv_back)
    ImageView ivBack;

    @BindView(R.id.iv_movie_logo)
    ImageView ivMovieLogo;

    @BindView(R.id.iv_movie_back)
    ImageView ivMovieBack;

    @BindView(R.id.tv_movie_name)
    TextView tvMovieName;

    @BindView(R.id.tv_released_date)
    TextView tvReleasedDate;

    @BindView(R.id.rv_genre)
    RecyclerView rvGenre;

    @BindView(R.id.tv_rate)
    TextView tvRate;

    @BindView(R.id.tv_overview)
    TextView tvOverview;

    @BindView(R.id.rv_movie_trailers)
    RecyclerView rvTrailers;

    @BindView(R.id.tv_title_movie_name)
    TextView tvTitleMovieName;

    @BindView(R.id.ll_time)
    LinearLayout llTime;

    @BindView(R.id.tv_time)
    TextView tvTime;

    @BindView(R.id.tv_trailers)
    TextView tvTrailers;

    @BindView(R.id.avi)
    AVLoadingIndicatorView loadingView;

    @BindView(R.id.tv_reviews)
    TextView tvReviews;

    @BindView(R.id.ll_reviews)
    LinearLayout llReviews;

    @BindView(R.id.rv_movie_casts)
    RecyclerView rvMovieCasts;

    @BindView(R.id.tv_casts)
    TextView tvCasts;

    @BindView(R.id.iv_share)
    ImageView ivShare;

    private String currentMovieId;
    private List<Integer> currentGenreIds;


    private GenreAdapter mGenreAdapter;
    private TrailersAdapter mTrailersAdapter;
    private CastAdapter mCastAdapter;

    private String movieTagline, imdbId, homepage;

    public static Intent newIntent(Context context, String movieId) {
        Intent intent = new Intent(context, MovieDetailsActivity.class);
        intent.putExtra(KEY_MOVIE_ID, movieId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this, this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadingView.setVisibility(View.VISIBLE);

        if (getIntent().getStringExtra(KEY_MOVIE_ID) != null) {
            currentMovieId = getIntent().getStringExtra(KEY_MOVIE_ID);
            MovieModel.getInstance().startLoadingMovieDetails(currentMovieId);
            MovieModel.getInstance().startLoadingMovieTrailers(currentMovieId);
            MovieModel.getInstance().startLoadingMovieReviews(currentMovieId);
            MovieModel.getInstance().startLoadingMovieCredits(currentMovieId);
        }
        currentGenreIds = new ArrayList<>();

        mGenreAdapter = new GenreAdapter(getApplicationContext());
        rvGenre.setAdapter(mGenreAdapter);
        rvGenre.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvGenre.setHasFixedSize(true);

        mTrailersAdapter = new TrailersAdapter(getApplicationContext(), this);
        rvTrailers.setAdapter(mTrailersAdapter);
        rvTrailers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvTrailers.setHasFixedSize(true);

        mCastAdapter = new CastAdapter(getApplicationContext());
        rvMovieCasts.setAdapter(mCastAdapter);
        rvMovieCasts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvMovieCasts.setHasFixedSize(true);

        ivBack.setOnClickListener(this);
        ivShare.setOnClickListener(this);

        getSupportLoaderManager().initLoader(MOVIE_DETAILS_LOADER_ID, null, this);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    tvTitleMovieName.setVisibility(View.VISIBLE);
                    isShow = true;
                } else if (isShow) {
                    tvTitleMovieName.setVisibility(View.GONE);
                    isShow = false;
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_share:
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Intent movieShareIntent = new Intent(Intent.ACTION_SEND);
                movieShareIntent.setType("text/plain");
                String extraText = "";
                extraText += tvTitleMovieName.getText().toString() + "\n";
                if (movieTagline != null) extraText += movieTagline + "\n";
                if (imdbId != null) extraText += AppConstants.IMDB_BASE_URL + imdbId + "\n";
                if (homepage != null) extraText += homepage;
                movieShareIntent.putExtra(Intent.EXTRA_TEXT, extraText);
                startActivity(movieShareIntent);
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        switch (id) {
            case MOVIE_DETAILS_LOADER_ID:
                cursorLoader = new CursorLoader(getApplicationContext(),
                        MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                        new String[]{currentMovieId},
                        null);
                break;
            case MOVIE_GENRES_LOADER_ID:
                cursorLoader = new CursorLoader(getApplicationContext(),
                        MovieContract.MovieGenreEntry.CONTENT_URI,
                        null,
                        MovieContract.MovieGenreEntry.COLUMN_MOVIE_ID + "=?",
                        new String[]{String.valueOf(currentMovieId)},
                        null);
                break;
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            switch (loader.getId()) {
                case MOVIE_DETAILS_LOADER_ID:
                    MovieVO movieVO = MovieVO.parseFromCursor(getApplicationContext(), data);
                    bindData(movieVO);
                    break;
                case MOVIE_GENRES_LOADER_ID:
                    List<GenreVO> genreList = new ArrayList<>();
                    do {
                        GenreVO genreVO = GenreVO.parseFromCursor(data);
                        genreList.add(genreVO);
                    } while (data.moveToNext());
                    bindGenres(genreList);
                    break;
            }
        }
    }

    private void bindGenres(List<GenreVO> genreList) {
        mGenreAdapter.setNewData(genreList);
    }

    private void bindData(MovieVO movieVO) {

        movieTagline = movieVO.getTagline();
        homepage = movieVO.getHomepage();
        imdbId = movieVO.getImdbId();

        getSupportLoaderManager().initLoader(MOVIE_GENRES_LOADER_ID, null, this);
        tvTitleMovieName.setText(movieVO.getTitle());
        tvMovieName.setText(movieVO.getOriginalTitle());
        tvReleasedDate.setText(movieVO.getReleasedDate());
        tvRate.setText(movieVO.getVoteAverage() + "/10");
        tvOverview.setText(movieVO.getOverview());
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.movie_placeholder)
                .centerCrop();
        Glide.with(getApplicationContext()).load(AppConstants.IMAGE_LOADING_BASE_URL + movieVO.getBackDropPath()).apply(requestOptions).into(ivMovieBack);
        Glide.with(getApplicationContext()).load(AppConstants.IMAGE_LOADING_BASE_URL + movieVO.getPosterPath()).apply(requestOptions).into(ivMovieLogo);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onMovieDetailsLoaded(RestApiEvents.MovieDetailsDataLoadedEvent event) {
        if (event.getmMovie().getRuntime() != null) {
            llTime.setVisibility(View.VISIBLE);
            int hour = event.getmMovie().getRuntime() / 60;
            int min = event.getmMovie().getRevenue() % 60;
            tvTime.setText(hour + " hr " + min + " mins");
            if (min == 0) {
                tvTime.setText(hour + " hr ");
            }
            if (hour == 0) {
                tvTime.setText(min + " mins");
            }
            if (hour == 0 && min == 0) {
                llTime.setVisibility(View.GONE);
            }
        } else {
            llTime.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onMovieTrailersLoaded(RestApiEvents.MovieTrailersDataLoadedEvent event) {
        tvTrailers.setVisibility(View.VISIBLE);
        mTrailersAdapter.setNewData(event.getmTrailers());
    }

    @Subscribe
    public void onMovieCastsDataLoaded(RestApiEvents.MovieCreditsDataLoadedEvent event) {
        tvCasts.setVisibility(View.VISIBLE);
        mCastAdapter.setNewData(event.getMovieCasts());
    }

    @Subscribe
    public void onMovieReviewsDataLoaded(RestApiEvents.MovieReviewsDataLoadedEvent event) {
        loadingView.setVisibility(View.GONE);
        tvReviews.setVisibility(View.VISIBLE);
        for (ReviewVO reviewVO : event.getReviews()) {
            LinearLayout linearlayout = new LinearLayout(this);
            linearlayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layoutParamsParent = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams layoutParamsViews = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsParent.setMargins(0, 38, 0, 0);
            linearlayout.setLayoutParams(layoutParamsParent);

            TextView tvContent = new TextView(this);
            TextView tvAuthor = new TextView(this);
            tvContent.setTextSize(14);
            tvAuthor.setTextSize(18);
            tvAuthor.setTypeface(Typeface.createFromAsset(getAssets(), "berylium_rg_it.ttf"));
            tvContent.setTextColor(getResources().getColor(R.color.icons));
            tvAuthor.setTextColor(getResources().getColor(R.color.icons));

            tvContent.setText(reviewVO.getContent());
            tvAuthor.setText("Written by " + reviewVO.getAuthor());

            tvContent.setLayoutParams(layoutParamsViews);
            tvAuthor.setLayoutParams(layoutParamsViews);
            linearlayout.addView(tvContent);
            linearlayout.addView(tvAuthor);
            llReviews.addView(linearlayout);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onErrorInvokingAPI(RestApiEvents.ErrorInvokingAPIEvent event) {
        loadingView.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClickTriler(String trailerKey) {
        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.YOUTUBE_WATCH_BASE_URL + trailerKey));
        startActivity(youtubeIntent);
    }
}
