
/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.acieslinski.moviefun.fragments;

import android.app.ProgressDialog;
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
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Arkadiusz Cieśliński 14.11.15.
 *         <acieslinski@gmail.com>
 */

public class VideoList extends Fragment {
    @Bind(R.id.rv_movies)
    protected EmptyRecyclerView mRecyclerView;
    @Bind(R.id.tv_empty_list)
    protected TextView mEmptyTextView;
    protected ProgressDialog mProgressDialog;

    private boolean isLoadingState;

    protected VideosAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        mAdapter = new VideosAdapter();
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

        if (isLoadingState) {
            mProgressDialog.show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(getResources().getString(R.string.message_loader));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setIndeterminate(true);

        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mProgressDialog = null;

        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(SearchEvent searchEvent) {
        Search search = searchEvent.getSearch();

        if (isAdded()) {
            ApiAdapter apiAdapter = new ApiAdapter(getActivity());

            isLoadingState = true;
            if (null != mProgressDialog) {
                mProgressDialog.show();
            }

            mAdapter.clear();

            apiAdapter.searchMovies(search)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .filter(new Func1<Video, Boolean>() {
                        @Override
                        public Boolean call(Video video) {
                            return video.isPosterAvailable();
                        }
                    })
                    .doOnNext(new Action1<Video>() {
                        @Override
                        public void call(Video video) {
                            mAdapter.add(video);
                        }
                    })
                    .doOnCompleted(new Action0() {
                        @Override
                        public void call() {
                            if (null != mProgressDialog) {
                                mProgressDialog.hide();
                            }
                            isLoadingState = false;
                        }
                    })
                    .subscribe();
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
                notifyItemInserted(mVideos.indexOf(video));
            }
        }

        public void clear() {
            mVideos.clear();
            notifyDataSetChanged();
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
