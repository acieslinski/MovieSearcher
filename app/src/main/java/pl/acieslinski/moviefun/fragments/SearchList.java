package pl.acieslinski.moviefun.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
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
            @Bind(R.id.tv_search)
            protected TextView mSearchTextView;
            @Bind(R.id.tv_year)
            protected TextView mYearTextView;
            @Bind(R.id.tv_type)
            protected TextView mTypeTextView;
            @Bind(R.id.btn_search)
            protected Button mReSearchButton;

            public ViewHolder(View view) {
                super(view);

                ButterKnife.bind(this, view);
            }

            public void setSearch(final Search search) {
                mSearchTextView.setText(search.getSearch());
                mYearTextView.setText(search.getYear());
                mTypeTextView.setText(search.getType().toString());

                mReSearchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new SearchEvent(search));
                    }
                });
            }
        }
    }
}
