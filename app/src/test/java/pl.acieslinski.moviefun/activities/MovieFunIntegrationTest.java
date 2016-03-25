package pl.acieslinski.moviefun.activities;

import android.app.Activity;
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
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.httpclient.FakeHttp;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import pl.acieslinski.moviefun.BuildConfig;
import pl.acieslinski.moviefun.R;
import pl.acieslinski.moviefun.fragments.SearchList;
import pl.acieslinski.moviefun.fragments.VideoList;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.internal.schedulers.ScheduledAction;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

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
        attachVideoContainer(mMovieFun, mVideoList);

        RxJavaPlugins.getInstance().registerSchedulersHook(new RobolectricRxJavaSchedulersHook());
    }

    @Test
    public void sendingSearchQueryTest() throws InterruptedException {
        EditText editText = (EditText) mSearchList.getView().findViewById(R.id.et_search);
        Button button = (Button) mSearchList.getView().findViewById(R.id.btn_search);

        editText.setText("test");
        button.callOnClick();

        Robolectric.flushForegroundThreadScheduler();

        HttpRequest httpRequest = FakeHttp.getLatestSentHttpRequest();

        httpRequest.getParams();
    }

    /**
     * Creates special container for the {@link VideoList} for the test purposes
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

    private class RobolectricRxJavaSchedulersHook extends RxJavaSchedulersHook {
        /**
         * Returns {@link Robolectric#getBackgroundThreadScheduler()} instead of the
         * {@link rx.schedulers.Schedulers#newThread()}
         */
        @Override
        public Scheduler getNewThreadScheduler() {
            return new RobolectricRxScheduler();
        }
    }

    private class RobolectricRxScheduler extends Scheduler {
        boolean isUnsubscribed;

        @Override
        public Worker createWorker() {
            return new Worker() {
                @Override
                public Subscription schedule(Action0 action) {
                    ScheduledAction scheduledAction = new ScheduledAction(action);

                    Robolectric.getBackgroundThreadScheduler().post(() -> {
                        scheduledAction.run();
                        scheduledAction.unsubscribe();
                    });

                    return scheduledAction;
                }

                @Override
                public Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
                    return null;
                }

                @Override
                public void unsubscribe() {
                    isUnsubscribed = true;
                }

                @Override
                public boolean isUnsubscribed() {
                    return isUnsubscribed;
                }
            };
        }
    }
}

