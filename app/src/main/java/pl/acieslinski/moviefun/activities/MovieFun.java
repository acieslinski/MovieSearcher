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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import pl.acieslinski.moviefun.R;
import pl.acieslinski.moviefun.fragments.SearchList;
import pl.acieslinski.moviefun.fragments.VideoList;
import pl.acieslinski.moviefun.models.Search;
import pl.acieslinski.moviefun.models.SearchEvent;

import static android.view.ViewGroup.LayoutParams;

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

    @Nullable
    private ViewStrategy mViewStrategy;
    private FragmentManager mFragmentManager;
    private MoviePagerAdapter mMoviePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_fun);

        ButterKnife.bind(this);

        EventBus.getDefault().register(this);

        setSupportActionBar(mToolbar);

        mFragmentManager = getSupportFragmentManager();

        mMoviePagerAdapter = new MoviePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mMoviePagerAdapter);

        mViewStrategy = instantiateViewStrategy();

        mViewStrategy.onCreate(savedInstanceState);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        // strategy is available from creation of the activity
        if (mViewStrategy != null) {
            mViewStrategy.onAttachFragment(fragment);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        mViewStrategy.onBackPressed();
    }

    public void onEventMainThread(SearchEvent searchEvent) {
        mViewStrategy.handleSearchEvent(searchEvent.getSearch());
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
                    fragment = FragmentAdapter.newInstance(new SearchList(), R.id.pageLeft);
                    break;
                case PAGE_VIDEOS:
                    fragment = FragmentAdapter.newInstance(new VideoList(), R.id.pageRight);
                    break;
                default:
                    throw new UnsupportedOperationException("position " + position);
            }

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
                    throw new UnsupportedOperationException("position " + position);
            }

            return pageTitle;
        }

        public void replace(VideoList videoList) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.replace(R.id.pageRight, videoList);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public static class FragmentAdapter extends Fragment {
        private static final String TAG_FRAGMENT_ID = "id";

        @Nullable
        private Fragment mFragment;

        public static FragmentAdapter newInstance(Fragment fragment, int id) {
            FragmentAdapter fragmentAdapter = new FragmentAdapter();
            fragmentAdapter.mFragment = fragment;

            Bundle bundle = new Bundle();
            bundle.putInt(TAG_FRAGMENT_ID, id);

            fragmentAdapter.setArguments(bundle);

            return fragmentAdapter;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            FrameLayout frameLayout = new FrameLayout(getContext());
            frameLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));

            int id = getArguments().getInt(TAG_FRAGMENT_ID);
            frameLayout.setId(id);

            return frameLayout;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            int id = getArguments().getInt(TAG_FRAGMENT_ID);

            if (mFragment != null) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(id, mFragment);
                transaction.commit();
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
        void onCreate(Bundle savedInstanceState);

        void handleSearchEvent(Search search);

        void onAttachFragment(Fragment fragment);

        void onBackPressed();
    }

    protected class ConvenientView implements ViewStrategy {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            mTabLayout.setupWithViewPager(mViewPager);
        }

        @Override
        public void handleSearchEvent(Search search) {
            mViewPager.setCurrentItem(PAGE_VIDEOS);

            mMoviePagerAdapter.replace(VideoList.newInstance(search));
        }

        @Override
        public void onAttachFragment(Fragment fragment) {
            // do nothing
        }

        @Override
        public void onBackPressed() {
            // do nothing
        }
    }

    protected class CompactView implements ViewStrategy {
        @Bind(R.id.fg_search_list)
        protected FrameLayout mSearchListFrameLayout;
        @Bind(R.id.fg_video_list)
        protected FrameLayout mVideoListFrameLayout;

        private boolean mSearchListHasBeenAttached;
        private boolean mVideoListHasBeenAttached;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            ButterKnife.bind(this, mContainer);

            if (savedInstanceState != null) {
                // if configuration changed
                pinFragments();
            }
        }

        @Override
        public void onAttachFragment(Fragment fragment) {
            if (fragment instanceof SearchList) {
                ((SearchList) fragment).setContainer(mSearchListFrameLayout);
                mSearchListHasBeenAttached = true;
            }

            if (fragment instanceof VideoList) {
                ((VideoList) fragment).setContainer(mVideoListFrameLayout);
                mVideoListHasBeenAttached = true;
            }

            if (mSearchListHasBeenAttached && mVideoListHasBeenAttached) {
                // hide the view pager after all needed fragments had been pinned to the containers
                mViewPager.setVisibility(View.GONE);
            }
        }

        @Override
        public void handleSearchEvent(Search search) {
            // after replacing the video list fragment the onAttachFragment will be called
            mMoviePagerAdapter.replace(VideoList.newInstance(search));
        }

        @Override
        public void onBackPressed() {
            pinFragments();
        }

        /**
         * Pins the fragments to the containers.
         */
        private void pinFragments() {
            mSearchListFrameLayout.removeAllViews();
            Fragment pageLeft = getSupportFragmentManager().findFragmentById(R.id.pageLeft);
            onAttachFragment(pageLeft);

            mVideoListFrameLayout.removeAllViews();
            Fragment pageRight = getSupportFragmentManager().findFragmentById(R.id.pageRight);
            onAttachFragment(pageRight);
        }
    }
}
