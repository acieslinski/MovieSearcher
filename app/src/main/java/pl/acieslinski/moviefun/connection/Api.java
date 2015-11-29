package pl.acieslinski.moviefun.connection;

import android.support.annotation.Nullable;
import android.webkit.GeolocationPermissions;

import com.google.gson.annotations.SerializedName;

import pl.acieslinski.moviefun.models.Search;
import pl.acieslinski.moviefun.models.Video;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Defines methods for communication with the server.
 *
 * @author Arkadiusz Cieśliński
 *         <acieslinski@gmail.com>
 *         <arkadiusz.cieslinski@partners.mbank.pl>
 *         <ZEW_2_9597>
 */

public interface Api {
    String FIELD_TITLE = "Title";
    String FIELD_RUNTIME = "Runtime";
    String FIELD_GENRE = "Genre";
    String FIELD_PLOT = "Plot";
    String FIELD_YEAR = "Year";
    String FIELD_POSTER = "Poster";
    String FIELD_SEARCH = "Search";

    String QUERY_SEARCH = "s";
    String QUERY_YEAR = "y";
    String QUERY_TYPE = "type";
    String QUERY_GET_MOVIES = "/?r=json";


    @GET(QUERY_GET_MOVIES)
    void searchMovies(@Query(QUERY_SEARCH) String search, @Query(QUERY_YEAR) String year,
                        @Query(QUERY_TYPE) String type, Callback<Search> callback);

    class Search {
        @Nullable
        @SerializedName(value = Api.FIELD_SEARCH)
        public Video[] videos;
    }
}
