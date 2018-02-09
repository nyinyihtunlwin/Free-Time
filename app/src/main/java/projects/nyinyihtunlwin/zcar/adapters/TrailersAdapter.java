package projects.nyinyihtunlwin.zcar.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import projects.nyinyihtunlwin.zcar.R;
import projects.nyinyihtunlwin.zcar.data.vo.TrailerVO;
import projects.nyinyihtunlwin.zcar.delegates.MovieDetailsDelegate;
import projects.nyinyihtunlwin.zcar.viewholders.TrailersViewHolder;

/**
 * Created by Dell on 2/6/2018.
 */

public class TrailersAdapter extends BaseRecyclerAdapter<TrailersViewHolder, TrailerVO> {

    private MovieDetailsDelegate mMovieDetailsDelegate;

    public TrailersAdapter(Context context, MovieDetailsDelegate movieDetailsDelegate) {
        super(context);
        this.mMovieDetailsDelegate = movieDetailsDelegate;
    }

    @Override
    public TrailersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.view_item_movie_trailer, parent, false);
        return new TrailersViewHolder(view, mMovieDetailsDelegate);
    }
}
