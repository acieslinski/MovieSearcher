package pl.acieslinski.moviefun;

import android.util.Log;

import pl.acieslinski.moviefun.managers.DatabaseManager;

/**
 * @author Arkadiusz Cieśliński 14.11.15.
 *         <acieslinski@gmail.com>
 */
public class Application extends android.app.Application {
    private static final String TAG = Application.class.getSimpleName();

    protected static Application sInstance;

    private DatabaseManager mDatabaseManager;

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate()");
        super.onCreate();

        sInstance = this;

        mDatabaseManager = new DatabaseManager(getApplicationContext());
    }

    public static Application getInstance() {
        return sInstance;
    }

    public DatabaseManager getDatabaseManager() {
        return mDatabaseManager;
    }
}
