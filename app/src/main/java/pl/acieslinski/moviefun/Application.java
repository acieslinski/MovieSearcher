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

package pl.acieslinski.moviefun;

import android.support.annotation.VisibleForTesting;
import android.util.Log;

import pl.acieslinski.moviefun.managers.ApiManager;
import pl.acieslinski.moviefun.managers.DatabaseManager;

/**
 * @author Arkadiusz Cieśliński 14.11.15.
 *         <acieslinski@gmail.com>
 */
public class Application extends android.app.Application {
    private static final String TAG = Application.class.getSimpleName();

    protected static Application sInstance;

    private DatabaseManager mDatabaseManager;
    protected ApiManager mApiManager;

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate()");
        super.onCreate();

        sInstance = this;

        mDatabaseManager = new DatabaseManager(getApplicationContext());
        mApiManager = new ApiManager(getApplicationContext());
    }

    public static Application getInstance() {
        return sInstance;
    }

    public DatabaseManager getDatabaseManager() {
        return mDatabaseManager;
    }

    public ApiManager getApiManager() {
        return mApiManager;
    }

    @VisibleForTesting
    public void injectApiManager(ApiManager apiManager) {
        mApiManager = apiManager;
    }

    @VisibleForTesting
    public void injectDatabaseManager(DatabaseManager databaseManager) {
        mDatabaseManager = databaseManager;
    }
}
