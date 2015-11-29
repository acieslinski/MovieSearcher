package pl.acieslinski.moviefun.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import pl.acieslinski.moviefun.Application;
import pl.acieslinski.moviefun.R;
import pl.acieslinski.moviefun.connection.ApiAdapter;
import pl.acieslinski.moviefun.models.Search;
import pl.acieslinski.moviefun.models.SearchEvent;
import pl.acieslinski.moviefun.models.Video;
import pl.acieslinski.moviefun.views.EmptyRecyclerView;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.functions.Action1;

/**
 * @author Arkadiusz Cieśliński 14.11.15.
 *         <acieslinski@gmail.com>
 */

public class VideoList extends Fragment {
    @Bind(R.id.rv_movies)
    protected EmptyRecyclerView mRecyclerView;
    @Bind(R.id.tv_empty_list)
    protected TextView mEmptyTextView;

    protected VideosAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        mAdapter = new VideosAdapter();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setEmptyView(mEmptyTextView);

        int spanCount = Application.getInstance().getResources().getInteger(
                R.integer.movies_list_span_count);

        mLayoutManager = new GridLayoutManager(this.getContext(), spanCount);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(mAdapter);
    }

    protected void onEventMainThread(SearchEvent searchEvent) {
        Search search = searchEvent.getSearch();

        if (isAdded()) {
            ApiAdapter apiAdapter = new ApiAdapter(getActivity());

            apiAdapter.searchMovies(search).doOnNext(new Action1<Video>() {
                @Override
                public void call(Video video) {
                    mAdapter.add(video);
                }
            });
        }
    }

    protected class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> {
        private List<Video> mVideos;

        public VideosAdapter() {
            mVideos = new ArrayList();
        }

        public VideosAdapter(List<Video> videos) {
            mVideos = videos;
        }

        @Override
        public VideosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.fragment_video_list_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Video video = mVideos.get(position);
            holder.setVideo(video);
        }

        @Override
        public int getItemCount() {
            return mVideos.size();
        }

        public void add(final Video video) {
            mVideos.add(video);

            if (isAdded()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyItemInserted(mVideos.indexOf(video));
                    }
                });
            }
        }

        protected class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.iv_poster)
            public ImageView mPosterImageView;
            @Bind(R.id.tv_title)
            public TextView mTitleTextView;

            public ViewHolder(View v) {
                super(v);

                ButterKnife.bind(this, v);
            }

            public void setVideo(Video video) {
                mTitleTextView.setText(video.getTitle());

                Picasso.with(getContext()).load(video.getPosterLink()).networkPolicy(
                        NetworkPolicy.OFFLINE).into(mPosterImageView);
            }
        }
    }
}
