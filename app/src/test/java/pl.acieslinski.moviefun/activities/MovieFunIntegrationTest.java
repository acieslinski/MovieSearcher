package pl.acieslinski.moviefun.activities;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import org.apache.http.HttpRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowProgressDialog;
import org.robolectric.shadows.httpclient.FakeHttp;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import pl.acieslinski.moviefun.BuildConfig;
import pl.acieslinski.moviefun.R;
import pl.acieslinski.moviefun.fragments.SearchForm;
import pl.acieslinski.moviefun.fragments.SearchList;
import pl.acieslinski.moviefun.fragments.VideoList;
import pl.acieslinski.moviefun.models.SearchEvent;


import static org.junit.Assert.assertNotNull;

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

        SupportFragmentTestUtil.startVisibleFragment(mSearchList);
//        SupportFragmentTestUtil.startVisibleFragment(mVideoList);

        FrameLayout frameLayout = new FrameLayout(mMovieFun);
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        frameLayout.setId(R.id.pageRight);
        mMovieFun.mContainer.addView(frameLayout);

        mMovieFun.getSupportFragmentManager().beginTransaction().add(R.id.pageRight, mVideoList).commit();
        mMovieFun.getSupportFragmentManager().executePendingTransactions();
    }

    @Test
    public void sendingSearchQueryTest() throws InterruptedException {


        EditText editText = (EditText) mSearchList.getView().findViewById(R.id.et_search);
        Button button = (Button) mSearchList.getView().findViewById(R.id.btn_search);

        editText.setText("test");
        button.callOnClick();

        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();


        Thread.sleep(60000);

        HttpRequest httpRequest = FakeHttp.getLatestSentHttpRequest();

        httpRequest.getParams();
    }
}

class MovieFunTest extends MovieFun {


    public void onEventMainThread(SearchEvent searchEvent) {

    }
}
