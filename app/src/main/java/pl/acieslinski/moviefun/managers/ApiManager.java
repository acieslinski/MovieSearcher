package pl.acieslinski.moviefun.managers;

import android.content.Context;

import pl.acieslinski.moviefun.connection.ApiAdapter;
import pl.acieslinski.moviefun.models.Search;
import pl.acieslinski.moviefun.models.Video;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Arkadiusz Cieśliński 26.03.16.
 *         <acieslinski@gmail.com>
 */

public class ApiManager {
    private ApiAdapter mApiAdapter;

    public interface MoviesCallback {
        void onVideo(Video video);
        void onCompleted();
        void onError(Throwable error);
    }

    public ApiManager(Context context) {
        mApiAdapter = new ApiAdapter(context);
    }

    public void fetchMovies(Search search, MoviesCallback callback) {
        mApiAdapter.searchMovies(search)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .filter(video -> video.isPosterAvailable())
                // must be this way - https://github.com/square/retrofit/issues/1214
                .subscribe(new Subscriber<Video>() {
                    @Override
                    public void onCompleted() {
                        callback.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(e);
                    }

                    @Override
                    public void onNext(Video video) {
                        callback.onVideo(video);
                    }
                });
    }
}
