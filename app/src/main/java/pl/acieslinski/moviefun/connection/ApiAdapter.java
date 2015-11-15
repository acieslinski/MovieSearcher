package pl.acieslinski.moviefun.connection;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.telecom.Call;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.acieslinski.moviefun.BuildConfig;
import pl.acieslinski.moviefun.R;
import pl.acieslinski.moviefun.models.Search;
import pl.acieslinski.moviefun.models.Video;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

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

    public void searchMovies(Search search, final Callback<List<Video>> callback) {
        mApiAdapter.searchMovies(search.getSearchEncoded(), search.getYear(),
                search.getType().getCodeName(), new Callback<Api.Search>() {
                    @Override
                    public void success(Api.Search search, Response response) {
                        List<Video> list = new ArrayList();

                        if (search != null && search.movies != null) {
                            list = Arrays.asList(search.movies);
                        }

                        callback.success(list, response);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        callback.failure(error);
                    }
                });
    }
}
