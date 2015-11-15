package pl.acieslinski.moviefun.models;

import android.support.annotation.Nullable;
import android.util.Log;

import com.j256.ormlite.field.DatabaseField;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import pl.acieslinski.moviefun.ApplicationContract;

/**
 * @author Arkadiusz Cieśliński 14.11.15.
 *         <acieslinski@gmail.com>
 */
public class Search {
    private static final String TAG = Search.class.getSimpleName();

    @DatabaseField(
            generatedId = true,
            columnName = ApplicationContract.DatabaseContract._ID)
    protected long mId;

    @DatabaseField(
            canBeNull = false,
            columnName = ApplicationContract.DatabaseContract.SEARCH_FIELD_SEARCH)
    private String mSearch;

    @Nullable
    @DatabaseField(
            canBeNull = false,
            columnName = ApplicationContract.DatabaseContract.SEARCH_FIELD_YEAR)
    private String mYear;

    @Nullable
    @DatabaseField(
            canBeNull = false,
            columnName = ApplicationContract.DatabaseContract.SEARCH_FIELD_TYPE)
    private Type mType;

    public Search() {
        mSearch = "";
        mYear = "";
        mType = Type.ALL;
    }

    public String getSearch() {
        return mSearch;
    }

    public String getSearchEncoded() {
        String search = "";
        try {
            search = URLEncoder.encode(mSearch, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return search;
    }

    public void setSearch(String search) {
        mSearch = search;
    }

    @Nullable
    public String getYear() {
        return mYear;
    }

    public void setYear(String year) {
        mYear = year;
    }

    @Nullable
    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        mType = type;
    }
}
