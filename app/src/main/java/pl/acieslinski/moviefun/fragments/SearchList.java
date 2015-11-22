package pl.acieslinski.moviefun.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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

/**
 * @author Arkadiusz Cieśliński 14.11.15.
 *         <acieslinski@gmail.com>
 */

public class SearchList extends Fragment {
    protected SearchForm mSearchForm;
    @Bind(R.id.rv_searches)
    protected RecyclerView mSearchesRecyclerView;
    protected SearchesAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO execute in background thread
        List<Search> searches = Application.getInstance().getDatabaseManager().getSearches();
        mAdapter = new SearchesAdapter(searches);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        EventBus.getDefault().unregister(this);
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

        mLayoutManager = new LinearLayoutManager(view.getContext());
        mSearchesRecyclerView.setLayoutManager(mLayoutManager);

        mSearchesRecyclerView.setAdapter(mAdapter);
    }

    public void onEventBackgroundThread(SearchEvent searchEvent) {
        Search search = searchEvent.getSearch();

        if (Application.getInstance().getDatabaseManager().saveSearch(search)) {
            mAdapter.insert(search, 0);
        }
    }

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

        public void insert(Search search, int position) {
            mSearches.add(position, search);

            if (isAdded()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
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

                mDateTextView.setText(day + " " + capitalize(months[month]).substring(0,3));

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
