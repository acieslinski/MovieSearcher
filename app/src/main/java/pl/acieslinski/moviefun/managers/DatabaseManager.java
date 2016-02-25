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
 */
public class DatabaseManager extends OrmLiteSqliteOpenHelper {
    private static final String TAG = DatabaseManager.class.getSimpleName();
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "videos.db";

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
            saved = mSearchDao.create(search) > 0;
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return saved;
    }
}
