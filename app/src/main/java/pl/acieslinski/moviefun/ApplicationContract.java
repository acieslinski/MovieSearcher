package pl.acieslinski.moviefun;

import android.provider.BaseColumns;

/**
 * @author Arkadiusz Cieśliński 14.11.15.
 *         <acieslinski@gmail.com>
 */
public interface ApplicationContract {
    interface DatabaseContract extends BaseColumns {
        String SEARCH_FIELD_SEARCH = "search";
        String SEARCH_FIELD_YEAR = "year";
        String SEARCH_FIELD_TYPE = "type";
    }
}
