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

package pl.acieslinski.moviefun.connection;

import android.content.Context;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.squareup.picasso.Picasso;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pl.acieslinski.moviefun.Application;
import pl.acieslinski.moviefun.BuildConfig;
import pl.acieslinski.moviefun.R;
import pl.acieslinski.moviefun.models.Search;
import pl.acieslinski.moviefun.models.Video;
import retrofit.RestAdapter;
import rx.Observable;
import rx.Subscriber;

/**
 * Provides methods for pull / push data from / to the server.
 *
 * @author Arkadiusz Cieśliński
 *         <acieslinski@gmail.com>
 */

public class ApiAdapter {
    private static final String TAG = ApiAdapter.class.getSimpleName();
    private static final ExecutorService sExecutor = Executors.newFixedThreadPool(5);

    protected Api mApiAdapter;
    protected Context mContext;

    public ApiAdapter(Context context) {
        String serviceUrl = context.getString(R.string.rest_url);
        mContext = context;

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(serviceUrl)
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL :
                        RestAdapter.LogLevel.NONE)
                .build();

        mApiAdapter = restAdapter.create(Api.class);
    }

    /**
     * Sends query for provided {@link Search} and returns an observer for the results (videos). It
     * also warms up the cache with poster's images. It calls {Subscriber#onNext} when video's
     * data is fetched with the poster image.
     */
    @WorkerThread
    public Observable<Video> searchMovies(final Search search) {
        return Observable
                .create(new Observable.OnSubscribe<Video>() {
                    @Override
                    public void call(Subscriber<? super Video> subscriber) {
                        try {
                            searchMovies(search, subscriber);
                        } catch (Exception ex) {
                            subscriber.onError(ex);
                        }
                    }
                });
    }

    /**
     * Sends query for provided {@link Search}. It also warms up the cache with poster's images.
     * It calls {Subscriber#onNext} when video's data is fetched with the poster image.
     */
    @WorkerThread
    private void searchMovies(Search search, final Subscriber<? super Video> subscriber) {
        Api.SearchResult searchResult = mApiAdapter.searchMovies(
                search.getSearchEncoded(),
                search.getYear(),
                search.getType().getCodeName()
        );

        if (searchResult != null && searchResult.videos != null) {
            int videosCount = searchResult.videos.length;

            final CountDownLatch postersLoaderLatch = new CountDownLatch(videosCount);

            for (int i = 0; i < videosCount; i++) {
                final Video video = searchResult.videos[i];

                loadPoster(video, subscriber, postersLoaderLatch);
            }

            // wait for all posters to indicate that the process has been completed
            try {
                postersLoaderLatch.await();
                subscriber.onCompleted();
            } catch (InterruptedException e) {
                Log.e(TAG, Log.getStackTraceString(e));
                subscriber.onError(e);
            }

        } else {
            // the response is empty
            subscriber.onCompleted();
        }
    }

    /**
     * Loads a poster for provided video into the cache directory {@see Picasso}
     */
    private void loadPoster(final Video video, final Subscriber<? super Video> subscriber,
                            final CountDownLatch loaderLatch) {
        sExecutor.execute(() -> Picasso
                .with(Application.getInstance().getApplicationContext())
                .load(video.getPosterLink())
                .fetch(new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        onTerminated();
                        video.setIsPosterAvailable(true);
                    }

                    @Override
                    public void onError() {
                        // poster for this video is not available - ignore it
                        onTerminated();
                    }

                    public void onTerminated() {
                        subscriber.onNext(video);
                        loaderLatch.countDown();
                    }
                }));
    }
}
