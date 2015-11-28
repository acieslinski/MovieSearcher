package pl.acieslinski.moviefun.fragments;

import android.app.Service;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import pl.acieslinski.moviefun.Application;
import pl.acieslinski.moviefun.R;
import pl.acieslinski.moviefun.models.Search;
import pl.acieslinski.moviefun.models.SearchEvent;
import pl.acieslinski.moviefun.utilities.SoftKeyboard;

/**
 * @author Arkadiusz Cieśliński 14.11.15.
 *         <acieslinski@gmail.com>
 */

public class SearchList extends Fragment {
    private static final int ANIMATION_INSERT_DURATION = 1000; // 1 second

    protected SearchForm mSearchForm;
    @Bind(R.id.ll_container)
    protected LinearLayout mContainerLinearLayout;
    @Bind(R.id.rv_searches)
    protected RecyclerView mSearchesRecyclerView;
    protected SearchesAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected SoftKeyboard mSoftKeyboard;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO execute in background thread
        List<Search> searches = Application.getInstance().getDatabaseManager().getSearches();
        mAdapter = new SearchesAdapter(searches);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        mSearchForm = (SearchForm) getChildFragmentManager().findFragmentById(R.id.fg_search_form);
        mSearchForm.setOnSearchButtonClickListener(mSearchButtonClickListener);

        mLayoutManager = new LinearLayoutManager(view.getContext());
        mSearchesRecyclerView.setLayoutManager(mLayoutManager);
        mSearchesRecyclerView.setAdapter(mAdapter);
        mSearchesRecyclerView.getItemAnimator().setAddDuration(ANIMATION_INSERT_DURATION);

        InputMethodManager inputMethodManager = (InputMethodManager) Application.getInstance().
                getApplicationContext().getSystemService(Service.INPUT_METHOD_SERVICE);
        mSoftKeyboard = new SoftKeyboard(mContainerLinearLayout, inputMethodManager);
    }

    private final View.OnClickListener mSearchButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mSearchForm.validateForm()) {
                mSoftKeyboard.closeSoftKeyboard(new SoftKeyboard.SoftKeyboardHideCallback() {
                    @Override
                    public void onSoftKeyboardHide() {
                        final Search search = mSearchForm.getSearch();

                        // TODO execute in background thread
                        if (Application.getInstance().getDatabaseManager().saveSearch(search)) {
                            mLayoutManager.smoothScrollToPosition(mSearchesRecyclerView, null, 0);
                            mAdapter.insert(search, 0);

                            mSearchesRecyclerView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    EventBus.getDefault().post(new SearchEvent(search));
                                }
                            }, ANIMATION_INSERT_DURATION);
                        } else {
                            EventBus.getDefault().post(new SearchEvent(search));
                        }
                    }
                });
            }
        }
    };

    protected class SearchesAdapter extends RecyclerView.Adapter<SearchesAdapter.ViewHolder> {
        private List<Search> mSearches;

        public SearchesAdapter() {
            mSearches = new ArrayList();
        }

        public SearchesAdapter(List<Search> searches) {
            mSearches = searches;
        }

        @Override
        public SearchesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.fragment_search_form_research, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Search search = mSearches.get(position);
            holder.setSearch(search);
        }

        @Override
        public int getItemCount() {
            return mSearches.size();
        }

        public void insert(Search search, final int position) {
            mSearches.add(position, search);

            if (isAdded()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyItemInserted(position);
                    }
                });
            }
        }

        protected class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.tv_title)
            protected TextView mTitleTextView;
            @Bind(R.id.tv_details)
            protected TextView mDetailsTextView;
            @Bind(R.id.tv_date)
            protected TextView mDateTextView;
            @Bind(R.id.btn_search)
            protected ImageButton mSearchButton;

            public ViewHolder(View view) {
                super(view);

                ButterKnife.bind(this, view);
            }

            public void setSearch(final Search search) {
                mTitleTextView.setText(search.getSearch());
                mDetailsTextView.setText(search.getType().toString());

                if (!search.getYear().isEmpty()) {
                    String template = getResources().getString(R.string.details_with_year);
                    String details = mDetailsTextView.getText().toString();
                    mDetailsTextView.setText(String.format(template, details, search.getYear()));
                }

                Date date = search.getDate();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                String[] months = getResources().getStringArray(R.array.months);

                mDateTextView.setText(day + " " + capitalize(months[month]).substring(0, 3));

                mSearchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new SearchEvent(search));
                    }
                });
            }

            private String capitalize(final String line) {
                return Character.toUpperCase(line.charAt(0)) + line.substring(1);
            }
        }
    }
}
