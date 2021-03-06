package projects.nyinyihtunlwin.freetime.mvp.views;

import java.util.List;

import projects.nyinyihtunlwin.freetime.data.vo.tvshows.TvShowVO;

public interface TvShowView {

    void displayTvShowList(List<TvShowVO> tvShowList);

    void showLoding();

    void navigateToTvShowDetails(String tvShowId);

    void onConnectionError(String message, int retryConnectionType);

    void onApiError(String message);
}
