package pl.acieslinski.moviefun.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.acieslinski.moviefun.Application;
import pl.acieslinski.moviefun.R;
import pl.acieslinski.moviefun.managers.DatabaseManager;
import pl.acieslinski.moviefun.models.Search;

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

    private final SearchForm.OnSearchButtonClickListener mEmptySearchButtonClickListener =
            new SearchForm.OnSearchButtonClickListener() {
        @Override
        public void onClick(View view, Search search, boolean validated) {
            // do nothing
        }
    };

    private SearchForm.OnSearchButtonClickListener mOnSearchButtonClickListener =
            mEmptySearchButtonClickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

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
        mSearchForm.setOnSearchButtonClickListener(new SearchForm.OnSearchButtonClickListener() {
            @Override
            public void onClick(View view, Search search, boolean validated) {
                // TODO execute in background thread
                if (validated) {
                    Application.getInstance().getDatabaseManager().saveSearch(search);

                    mAdapter.insert(search, 0);
                }

                mOnSearchButtonClickListener.onClick(view, search, validated);
            }
        });

        mLayoutManager = new LinearLayoutManager(view.getContext());
        mSearchesRecyclerView.setLayoutManager(mLayoutManager);

        mSearchesRecyclerView.setAdapter(mAdapter);
    }

    public void setOnSearchButtonClickListener(SearchForm.OnSearchButtonClickListener listener) {
        mOnSearchButtonClickListener = listener;
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
                    R.layout.fragment_search_form_compact, parent, false);

            SearchForm searchForm = new SearchForm();
            searchForm.onViewCreated(view, null);

            return new ViewHolder(view, searchForm);
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
            notifyDataSetChanged();
        }

        protected class ViewHolder extends RecyclerView.ViewHolder {
            private SearchForm mSearchForm;

            public ViewHolder(View view, SearchForm searchForm) {
                super(view);

                mSearchForm = searchForm;
                mSearchForm.setReadOnlyMode(true);
                mSearchForm.setOnSearchButtonClickListener(mOnSearchButtonClickListener);
            }

            public void setSearch(Search search) {
                mSearchForm.setSearch(search);
            }
        }
    }
}
