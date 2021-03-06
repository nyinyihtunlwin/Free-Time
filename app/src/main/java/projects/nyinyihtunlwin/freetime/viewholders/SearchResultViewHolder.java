package projects.nyinyihtunlwin.freetime.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import projects.nyinyihtunlwin.freetime.R;
import projects.nyinyihtunlwin.freetime.data.vo.SearchResultVO;
import projects.nyinyihtunlwin.freetime.delegates.SearchResultDelegate;
import projects.nyinyihtunlwin.freetime.utils.AppConstants;

/**
 * Created by Dell on 3/14/2018.
 */

public class SearchResultViewHolder extends BaseViewHolder<SearchResultVO> {

    @BindView(R.id.iv_movie)
    ImageView ivMovie;

    @BindView(R.id.tv_type)
    TextView tvType;

    private SearchResultVO mData;
    private SearchResultDelegate mSearchResultDelegate;

    public SearchResultViewHolder(View itemView, SearchResultDelegate searchResultDelegate) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mSearchResultDelegate = searchResultDelegate;
    }

    @Override
    public void setData(SearchResultVO mData) {
        this.mData = mData;
        tvType.setText(getType(mData.getMediaType()));
        Glide.with(itemView.getRootView().getContext()).load(AppConstants.IMAGE_LOADING_BASE_URL + mData.getPosterPath()).apply(AppConstants.requestOptions).into(ivMovie);
    }

    @Override
    public void onClick(View view) {
        mSearchResultDelegate.onClickResultItems(String.valueOf(mData.getId()), mData.getMediaType());
    }

    public String getType(String type) {
        switch (type) {
            case AppConstants.TYPE_SEARCH_MOVIE:
                return "Movie";
            case AppConstants.TYPE_SEARCH_TV_SHOW:
                return "TV Show";
            case AppConstants.TYPE_SEARCH_PERSON:
                return "Person";
            default:
                return "Movie";
        }
    }
}
