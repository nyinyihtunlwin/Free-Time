package projects.nyinyihtunlwin.zcar.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import projects.nyinyihtunlwin.zcar.R;
import projects.nyinyihtunlwin.zcar.adapters.SearchResultAdapter;
import projects.nyinyihtunlwin.zcar.components.SmartRecyclerView;
import projects.nyinyihtunlwin.zcar.components.SmartScrollListener;
import projects.nyinyihtunlwin.zcar.data.vo.SearchResultVO;
import projects.nyinyihtunlwin.zcar.delegates.SearchResultDelegate;
import projects.nyinyihtunlwin.zcar.mvp.presenters.SearchPresenter;
import projects.nyinyihtunlwin.zcar.mvp.views.SearchView;
import projects.nyinyihtunlwin.zcar.utils.AppConstants;
import projects.nyinyihtunlwin.zcar.utils.AppUtils;

/**
 * Created by Dell on 3/11/2018.
 */

public class SearchActivity extends BaseActivity implements SearchView, SearchResultDelegate {

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        return intent;
    }

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.loading)
    AVLoadingIndicatorView loading;

    @BindView(R.id.et_search)
    EditText etSearch;

    @BindView(R.id.tv_message)
    TextView tvMessage;

    @BindView(R.id.rv_result)
    SmartRecyclerView rvResult;

    private SearchResultAdapter mAdapter;
    private SearchPresenter mPresenter;

    private SmartScrollListener mSmartScrollListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this, this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mPresenter = new SearchPresenter(getApplicationContext());
        mPresenter.onCreate(this);

        mSmartScrollListener = new SmartScrollListener(new SmartScrollListener.OnSmartScrollListener() {
            @Override
            public void onListEndReached() {
                mPresenter.onResultListEndReached();
            }
        });

        mAdapter = new SearchResultAdapter(getApplicationContext(), this);
        rvResult.setAdapter(mAdapter);
        rvResult.setHasFixedSize(true);
        rvResult.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        rvResult.addOnScrollListener(mSmartScrollListener);

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int keyCode, KeyEvent keyEvent) {
                if (keyCode == EditorInfo.IME_ACTION_SEARCH) {
                    if (!etSearch.getText().toString().equals("")) {
                        String query = etSearch.getText().toString();
                        if (!query.equals("")) {
                            mAdapter.clearData();
                            hideSoftKeyboard(getApplicationContext());
                            if (AppUtils.getObjInstance().hasConnection()) {
                                mPresenter.onTapSearch(query);
                            } else {
                                tvMessage.setText("No internet connection!");
                            }
                        } else {
                            tvMessage.setError("Enter keywords.");
                        }
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    @Override
    public void displaySearchResults(List<SearchResultVO> resultList) {
        mAdapter.appendNewData(resultList);
        loading.setVisibility(View.GONE);
        tvMessage.setVisibility(View.GONE);
    }

    @Override
    public void showLoding() {
        loading.setVisibility(View.VISIBLE);
    }

    @Override
    public void navigateToDetails(String movieId, String mediaType) {
        Intent intent = null;
        switch (mediaType) {
            case AppConstants.TYPE_SEARCH_MOVIE:
                intent = MovieDetailsActivity.newIntent(getApplicationContext(), movieId);
                break;
            case AppConstants.TYPE_SEARCH_TV_SHOW:
                intent = TvShowDetailsActivity.newIntent(getApplicationContext(), movieId);
                break;
            case AppConstants.TYPE_SEARCH_PERSON:
                break;
        }
        startActivity(intent);
    }


    @Override
    public void onClickResultItems(String movieId, String mediaType) {
        mPresenter.onTapResult(movieId, mediaType);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
