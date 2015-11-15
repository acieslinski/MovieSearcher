package pl.acieslinski.moviefun.managers;

import android.provider.BaseColumns;

/**
 * Created by acieslinski on 07.11.15.
 */
public interface DatabaseContract extends BaseColumns {
    String SEARCH_FIELD_PHRASE = "search";
    String SEARCH_FIELD_YEAR = "year";
    String SEARCH_FIELD_PLOT = "plot";
}
