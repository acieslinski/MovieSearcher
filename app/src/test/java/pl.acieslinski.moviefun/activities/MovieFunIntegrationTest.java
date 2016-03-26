package pl.acieslinski.moviefun.activities;

import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import pl.acieslinski.moviefun.Application;
import pl.acieslinski.moviefun.BuildConfig;
import pl.acieslinski.moviefun.R;
import pl.acieslinski.moviefun.fragments.SearchList;
import pl.acieslinski.moviefun.fragments.VideoList;
import pl.acieslinski.moviefun.managers.ApiManager;
import pl.acieslinski.moviefun.managers.DatabaseManager;
import pl.acieslinski.moviefun.models.Search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Arkadiusz Cieśliński 24.03.16.
 *         <acieslinski@gmail.com>
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk=21, packageName = "pl.acieslinski.moviefun")
public class MovieFunIntegrationTest  {
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
        assertEquals(searchTestPhrase, searchForApi.getValue().getSearch());
        assertEquals(yearTestPhrase, searchForApi.getValue().getYear());

        // assert if save for search called properly
        ArgumentCaptor<Search> searchForDatabase = ArgumentCaptor.forClass(Search.class);
        verify(databaseManager).saveSearch(searchForDatabase.capture());

        assertNotNull(searchForDatabase.getValue());
        assertEquals(searchTestPhrase, searchForDatabase.getValue().getSearch());
        assertEquals(yearTestPhrase, searchForDatabase.getValue().getYear());
    }

    // TODO variations of the sendingSearchQueryTest

    /**
     * Attaches the {@link VideoList} to the provided activity for the test purposes.
     *
     * Since the {@link Robolectric} has problems with attaching the {@link VideoList}
     * by the {@link ViewPager} we have to do it "manually". The {@link VideoList} will be
     * added to the main container of the {@link MovieFun}
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

