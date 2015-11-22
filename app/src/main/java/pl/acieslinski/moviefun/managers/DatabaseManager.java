package pl.acieslinski.moviefun.managers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import pl.acieslinski.moviefun.models.Search;

/**
 * @author Arkadiusz Cieśliński
 *         <acieslinski@gmail.com>
 *         <arkadiusz.cieslinski@partners.mbank.pl>
 *         <ZEW_2_9597>
 */
public class DatabaseManager extends OrmLiteSqliteOpenHelper {
    private static final String TAG = DatabaseManager.class.getSimpleName();
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "movies.db";

    private Dao<Search, String> mSearchDao;

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        try {
            mSearchDao = getDao(Search.class);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Search.class);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        if (oldVersion <= 4 && newVersion >= 4) {
            try {
                TableUtils.dropTable(connectionSource, Search.class, true);
                TableUtils.createTable(connectionSource, Search.class);
            } catch (SQLException e) {
                Log.d(TAG, Log.getStackTraceString(e));
            }
        }
    }

    @Override
    public void close() {
        super.close();

        mSearchDao = null;
    }

    @Nullable
    public List<Search> getSearches() {
        List<Search> searches = new ArrayList();

        try {
            PreparedQuery<Search> query = mSearchDao.queryBuilder()
                    .orderBy(DatabaseContract._ID, false)
                    .prepare();

            searches = mSearchDao.query(query);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return searches;
    }

    public boolean saveSearch(Search search) {
        boolean saved = false;

        try {
            mSearchDao.createOrUpdate(search);
            saved = true;
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return saved;
    }
}
