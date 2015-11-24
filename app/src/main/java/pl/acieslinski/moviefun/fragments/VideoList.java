package pl.acieslinski.moviefun.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.acieslinski.moviefun.Application;
import pl.acieslinski.moviefun.R;
import pl.acieslinski.moviefun.models.Video;
import pl.acieslinski.moviefun.views.EmptyRecyclerView;

/**
 * @author Arkadiusz Cieśliński 14.11.15.
 *         <acieslinski@gmail.com>
 */

public class VideoList extends Fragment {
    @Bind(R.id.rv_movies)
    protected EmptyRecyclerView mRecyclerView;
    @Bind(R.id.tv_empty_list)
    protected TextView mEmptyTextView;

    protected RecyclerView.Adapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        mAdapter = new MoviesAdapter();
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

    public void setVideos(List<Video> videos) {
        mAdapter = new MoviesAdapter(videos);
        mRecyclerView.setAdapter(mAdapter);
    }

    protected class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
        private List<Video> mVideos;

        public MoviesAdapter() {
            mVideos = new ArrayList();
        }

        public MoviesAdapter(List<Video> videos) {
            mVideos = videos;
        }

        @Override
        public MoviesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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

                Picasso.with(getContext()).load(video.getPosterLink()).into(mPosterImageView,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                // do nothing
                            }

                            @Override
                            public void onError() {
                                mPosterImageView.setImageResource(R.drawable.nopreview);
                            }
                        });
            }
        }
    }
}
