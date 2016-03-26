package pl.acieslinski.moviefun.activities;

import android.app.Activity;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.tools.ant.taskdefs.condition.Http;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.httpclient.FakeHttp;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockWebServer;
import pl.acieslinski.moviefun.Application;
import pl.acieslinski.moviefun.BuildConfig;
import pl.acieslinski.moviefun.R;
import pl.acieslinski.moviefun.connection.ApiAdapter;
import pl.acieslinski.moviefun.fragments.SearchList;
import pl.acieslinski.moviefun.fragments.VideoList;
import pl.acieslinski.moviefun.managers.ApiManager;
import pl.acieslinski.moviefun.managers.DatabaseManager;
import pl.acieslinski.moviefun.models.Search;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.internal.schedulers.ScheduledAction;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Arkadiusz Cieśliński 24.03.16.
 *         <acieslinski@gmail.com>
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk=21, packageName = "pl.acieslinski.moviefun")
public class MovieFunIntegrationTest  {
    private static final ExecutorService sExecutor = Executors.newSingleThreadExecutor();
    MovieFun mMovieFun;
    SearchList mSearchList;
    VideoList mVideoList;

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        ShadowLog.stream = System.out;

        mMovieFun = Robolectric.buildActivity(MovieFun.class).create().start().resume().get();
        mSearchList = new SearchList();
        mVideoList = new VideoList();

        SupportFragmentTestUtil.startFragment(mSearchList);

        // Robolectric ViewPager issue with attaching fragments
        // http://stackoverflow.com/questions/27417148/how-to-swipe-a-frgaments-inside-viewpager-using-robolectric-in-android
        // http://stackoverflow.com/questions/11333354/how-can-i-test-fragments-with-robolectric
        attachVideoContainer(mMovieFun, mVideoList);
    }

    @Test
    public void sendingSearchQueryTest() throws Exception {
        String searchTestPhrase = "test";
        String yearTestPhrase = "1999";

        ApiManager apiManager = mock(ApiManager.class);
        DatabaseManager databaseManager = mock(DatabaseManager.class);

        Application.getInstance().injectApiManager(apiManager);
        Application.getInstance().injectDatabaseManager(databaseManager);

        EditText searchEditText = (EditText) mSearchList.getView().findViewById(R.id.et_search);
        EditText yearEditText = (EditText) mSearchList.getView().findViewById(R.id.et_year);
        Button button = (Button) mSearchList.getView().findViewById(R.id.btn_search);

        // set fields in the form
        searchEditText.setText(searchTestPhrase);
        yearEditText.setText(yearTestPhrase);
        button.callOnClick();

        Robolectric.flushForegroundThreadScheduler();

        // assert if fetch for movies called properly
        ArgumentCaptor<Search> searchForApi = ArgumentCaptor.forClass(Search.class);
        verify(apiManager).fetchMovies(searchForApi.capture(), anyObject());

        assertNotNull(searchForApi.getValue());
        assertEquals(searchForApi.getValue().getSearch(), searchTestPhrase);
        assertEquals(searchForApi.getValue().getYear(), yearTestPhrase);

        // assert if save for search called properly
        ArgumentCaptor<Search> searchForDatabase = ArgumentCaptor.forClass(Search.class);
        verify(databaseManager).saveSearch(searchForDatabase.capture());

        assertNotNull(searchForDatabase.getValue());
        assertEquals(searchForDatabase.getValue().getSearch(), searchTestPhrase);
        assertEquals(searchForDatabase.getValue().getYear(), yearTestPhrase);
    }

    /**
     * Attaches the {@link VideoList} to the provided activity for the test purposes.
     *
     * Since the fragments and the activity can work independently we can add them anywhere, where
     * the activity could find them.
     */
    private void attachVideoContainer(MovieFun movieFun, VideoList videoList) {
        FrameLayout frameLayout = new FrameLayout(mMovieFun);
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        frameLayout.setId(R.id.pageRight);
        movieFun.mContainer.addView(frameLayout);

        movieFun.getSupportFragmentManager().beginTransaction().add(R.id.pageRight, videoList).
                commit();
        movieFun.getSupportFragmentManager().executePendingTransactions();
    }
}

