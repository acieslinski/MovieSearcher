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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import pl.acieslinski.moviefun.Application;
import pl.acieslinski.moviefun.R;
import pl.acieslinski.moviefun.fragments.SearchList;
import pl.acieslinski.moviefun.fragments.VideoList;
import pl.acieslinski.moviefun.models.SearchEvent;

/**
 * @author Arkadiusz Cieśliński 14.11.15.
 *         <acieslinski@gmail.com>
 */
public class MovieFun extends AppCompatActivity {
    private static final int PAGE_COUNT = 2;
    private static final int PAGE_VIDEOS = 1;
    private static final int PAGE_SEARCHES = 0;
    private static final String VIEW_CONVENIENT = "convenient-view";
    private static final String VIEW_COMPACT = "compact-view";
    private static final String TAG_FRAGMENT_SEARCHES = "search-list";
    private static final String TAG_FRAGMENT_VIDEOS = "video-list";


    @Bind(R.id.fl_container)
    protected FrameLayout mContainer;
    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;
    @Nullable
    @Bind(R.id.viewpager)
    protected ViewPager mViewPager;
    @Nullable
    @Bind(R.id.tablayout)
    protected TabLayout mTabLayout;

    private ViewStrategy mViewStrategy;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_fun);

        ButterKnife.bind(this);

        EventBus.getDefault().register(this);

        setSupportActionBar(mToolbar);

        mFragmentManager = getSupportFragmentManager();

        mViewPager.setAdapter(new MoviePagerAdapter(getSupportFragmentManager()));

        mViewStrategy = instantiateViewStrategy();

        mViewStrategy.onCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }


    public void onEventMainThread(SearchEvent searchEvent) {
        mViewStrategy.handleSearchEvent();
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
                    fragment = new SearchList();
                    break;
                case PAGE_VIDEOS:
                    fragment = new VideoList();
                    break;
                default:
                    fragment = new Fragment();
            }

            return fragment;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();

            String tag = getFragmentTag(position);

            Fragment fragment = mFragmentManager.findFragmentByTag(tag);

            if (fragment != null) {
                transaction.attach(fragment);
            } else {
                fragment = getItem(position);
                transaction.add(container.getId(), fragment, tag);
            }

            transaction.commit();

            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String pageTitle;

            switch (position) {
                case PAGE_SEARCHES:
                    pageTitle = getResources().getString(R.string.page_search_form);
                    break;
                case PAGE_VIDEOS:
                    pageTitle = getResources().getString(R.string.page_movie_list);
                    break;
                default:
                    pageTitle = "";
            }

            return pageTitle;
        }

        private String getFragmentTag(int position) {
            switch (position) {
                case PAGE_SEARCHES:
                    return TAG_FRAGMENT_SEARCHES;
                case PAGE_VIDEOS:
                    return TAG_FRAGMENT_VIDEOS;
                default:
                    throw new UnsupportedOperationException("no fragment for position " + position);
            }
        }
    }

    private ViewStrategy instantiateViewStrategy() {
        ViewStrategy viewStrategy = null;
        String type = (String) mContainer.getTag();

        if (VIEW_CONVENIENT.equals(type)) {
            viewStrategy = new ConvenientView();
        }

        if (VIEW_COMPACT.equals(type)) {
            viewStrategy = new CompactView();
        }

        if (viewStrategy == null) {
            throw new IllegalArgumentException("no tag with view type specified or not supported");
        }

        return viewStrategy;
    }

    protected interface ViewStrategy {
        void onCreate();

        void handleSearchEvent();
    }

    protected class ConvenientView implements ViewStrategy {

        @Override
        public void onCreate() {
            mTabLayout.setupWithViewPager(mViewPager);
        }

        @Override
        public void handleSearchEvent() {
            mViewPager.setCurrentItem(PAGE_VIDEOS);
        }
    }

    protected class CompactView implements ViewStrategy {
        @Bind(R.id.fg_search_list)
        protected FrameLayout mSearchListFrameLayout;
        @Bind(R.id.fg_video_list)
        protected FrameLayout mVideoListFrameLayout;

        @Override
        public void onCreate() {
            ButterKnife.bind(this, mContainer);

            SearchList searchList = (SearchList) mFragmentManager.findFragmentByTag(
                    TAG_FRAGMENT_SEARCHES);
            searchList.setContainer(mSearchListFrameLayout);

            VideoList videoList = (VideoList) mFragmentManager.findFragmentByTag(
                    TAG_FRAGMENT_VIDEOS);
            videoList.setContainer(mVideoListFrameLayout);
        }

        @Override
        public void handleSearchEvent() {
            // do nothing
        }
    }
}
