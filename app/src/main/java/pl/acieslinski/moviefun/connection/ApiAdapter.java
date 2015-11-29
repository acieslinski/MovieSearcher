package pl.acieslinski.moviefun.connection;

import android.content.Context;
import android.util.Log;

import com.squareup.picasso.Picasso;

import java.util.concurrent.CountDownLatch;

import pl.acieslinski.moviefun.Application;
import pl.acieslinski.moviefun.BuildConfig;
import pl.acieslinski.moviefun.R;
import pl.acieslinski.moviefun.models.Search;
import pl.acieslinski.moviefun.models.Video;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;
import rx.Subscriber;

/**
 * Provides methods for pull / push data from / to the server.
 *
 * @author Arkadiusz Cieśliński
 *         <acieslinski@gmail.com>
 *         <arkadiusz.cieslinski@partners.mbank.pl>
 *         <ZEW_2_9597>
 */

public class ApiAdapter {
    private static final String TAG = ApiAdapter.class.getSimpleName();

    protected Api mApiAdapter;

    public ApiAdapter(Context context) {
        String serviceUrl = context.getString(R.string.rest_url);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(serviceUrl)
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL :
                        RestAdapter.LogLevel.NONE)
                .build();

        mApiAdapter = restAdapter.create(Api.class);
    }

//    public void searchMovies(Search search, final Callback<List<Video>> callback) {
//        mApiAdapter.searchMovies(
//                search.getSearchEncoded(),
//                search.getYear(),
//                search.getType().getCodeName(),
//                new Callback<Api.Search>() {
//                    @Override
//                    public void success(Api.Search search, Response response) {
//                        List<Video> list = new ArrayList();
//
//                        if (search != null && search.videos != null) {
//                            list = Arrays.asList(search.videos);
//                        }
//
//                        callback.success(list, response);
//                    }
//
//                    @Override
//                    public void failure(RetrofitError error) {
//                        callback.failure(error);
//                    }
//                });
//    }

    /**
     * // TODO add doc to the {@link #searchMovies(Search)}
     * @param search
     * @return
     */
    public Observable<Video> searchMovies(final Search search) {
        return Observable.create(new Observable.OnSubscribe<Video>() {
            @Override
            public void call(Subscriber<? super Video> subscriber) {
                searchMovies(search, subscriber);
            }
        });
    }

    private void searchMovies(Search search, final Subscriber<? super Video> subscriber) {
        mApiAdapter.searchMovies(
                search.getSearchEncoded(),
                search.getYear(),
                search.getType().getCodeName(),
                new Callback<Api.Search>() {
                    @Override
                    public void success(Api.Search search, Response response) {
                        if (search != null && search.videos != null) {
                            int videosCount = search.videos.length;

                            final CountDownLatch videosLatch = new CountDownLatch(videosCount);

                            for (int i = 0; i < videosCount; i++) {
                                final Video video = search.videos[i];

                                // loads posters into the cache directory
                                Picasso
                                        .with(Application.getInstance().getApplicationContext())
                                        .load(video.getPosterLink())
                                        .error(R.drawable.nopreview)
                                        .fetch(new com.squareup.picasso.Callback() {
                                            @Override
                                            public void onSuccess() {
                                                subscriber.onNext(video);
                                                videosLatch.countDown();
                                            }

                                            @Override
                                            public void onError() {
                                                subscriber.onNext(video);
                                                videosLatch.countDown();
                                            }
                                        });
                            }

                            try {
                                videosLatch.await();
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

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, Log.getStackTraceString(error));
                        subscriber.onError(error);
                    }
                });
    }
}
