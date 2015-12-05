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

package pl.acieslinski.moviefun.activities;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import pl.acieslinski.moviefun.Application;
import pl.acieslinski.moviefun.R;
import pl.acieslinski.moviefun.connection.ApiAdapter;
import pl.acieslinski.moviefun.fragments.VideoList;
import pl.acieslinski.moviefun.fragments.SearchForm;
import pl.acieslinski.moviefun.fragments.SearchList;
import pl.acieslinski.moviefun.models.Search;
import pl.acieslinski.moviefun.models.SearchEvent;
import pl.acieslinski.moviefun.models.Video;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author Arkadiusz Cieśliński 14.11.15.
 *         <acieslinski@gmail.com>
 */
public class MovieFun extends AppCompatActivity {
    private static final String TAG = MovieFun.class.getSimpleName();
    private static final int PAGE_COUNT = 2;
    private static final int PAGE_SEARCHES = 0;
    private static final int PAGE_VIDEOS = 1;

    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;
    @Nullable
    @Bind(R.id.viewpager)
    protected ViewPager mViewPager;
    @Nullable
    @Bind(R.id.tablayout)
    protected TabLayout mTabLayout;

    private VideoList mVideoList;
    private SearchList mSearchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_fun);

        ButterKnife.bind(this);

        EventBus.getDefault().register(this);

        setSupportActionBar(mToolbar);

        // TODO strategies
        if (mViewPager != null) {
            // activity for smartphones
            mVideoList = new VideoList();
            mSearchList = new SearchList();

            mViewPager.setAdapter(new MoviePagerAdapter(getSupportFragmentManager()));

            mTabLayout.setupWithViewPager(mViewPager);
        } else {
            // activity for tablets
            mVideoList = (VideoList) getSupportFragmentManager().
                    findFragmentById(R.id.fg_video_list);
            mSearchList = (SearchList) getSupportFragmentManager().
                    findFragmentById(R.id.fg_search_list);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(SearchEvent searchEvent) {
        // TODO use proper strategy
        mViewPager.setCurrentItem(PAGE_VIDEOS);
    }

    private class MoviePagerAdapter extends FragmentPagerAdapter {

        public MoviePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;

            switch (position) {
                case PAGE_SEARCHES:
                    fragment = mSearchList;
                    break;
                case PAGE_VIDEOS:
                    fragment = mVideoList;
                    break;
                default:
                    fragment = new Fragment();
            }

            return fragment;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);

            // retrive the instantiated fragments (after configuration changes)
            switch (position) {
                case PAGE_SEARCHES:
                    mSearchList = (SearchList) fragment;
                    break;
                case PAGE_VIDEOS:
                    mVideoList = (VideoList) fragment;
                    break;
                default:
                    // do nothing
            }

            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String pageTitle;

            switch (position) {
                case PAGE_SEARCHES:
                    pageTitle = Application.getInstance().getString(R.string.page_search_form);
                    break;
                case PAGE_VIDEOS:
                    pageTitle = Application.getInstance().getString(R.string.page_movie_list);
                    break;
                default:
                    pageTitle = "";
            }

            return pageTitle;
        }
    }
}
