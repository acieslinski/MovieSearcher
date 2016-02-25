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

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import pl.acieslinski.moviefun.models.Video;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Defines methods for communication with the server.
 *
 * @author Arkadiusz Cieśliński
 *         <acieslinski@gmail.com>
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
    SearchResult searchMovies(@Query(QUERY_SEARCH) String search, @Query(QUERY_YEAR) String year,
                        @Query(QUERY_TYPE) String type);

    class SearchResult {
        @Nullable
        @SerializedName(value = Api.FIELD_SEARCH)
        public Video[] videos;
    }
}
