package pl.acieslinski.moviefun.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.acieslinski.moviefun.R;
import pl.acieslinski.moviefun.models.Search;
import pl.acieslinski.moviefun.views.TypeSpinner;

/**
 * @author Arkadiusz Cieśliński 14.11.15.
 *         <acieslinski@gmail.com>
 */
public class SearchForm extends Fragment {
    private static final String TAG = SearchForm.class.getSimpleName();
    private static final int YEAR_MIN = 1900;

    @Bind(R.id.et_search)
    protected EditText mSearchEditText;
    @Bind(R.id.et_year)
    protected EditText mYearEditText;
    @Bind(R.id.sp_type)
    protected TypeSpinner mTypeSpinner;
    @Bind(R.id.btn_search)
    protected Button mSearchButton;

    public interface OnSearchButtonClickListener {
        void onClick(View view, Search search, boolean validate);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_search_form, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    public void setOnSearchButtonClickListener(final OnSearchButtonClickListener listener) {
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(view, getSearch(), validateForm());
            }
        });
    }

    public Search getSearch() {
        Search search = new Search();

        search.setSearch(mSearchEditText.getText().toString());
        search.setYear(mYearEditText.getText().toString());
        search.setType(mTypeSpinner.getSelectedType());

        return search;
    }

    public void setSearch(Search search) {
        mSearchEditText.setText(search.getSearch());
        mYearEditText.setText(search.getYear());
        mTypeSpinner.setSelection(search.getType());
    }

    public void setReadOnlyMode(boolean readOnly) {
        boolean enabled = !readOnly;

        mSearchEditText.setEnabled(enabled);
        mYearEditText.setEnabled(enabled);
        mTypeSpinner.setEnabled(enabled);
    }

    private boolean validateForm() {
        Search search = getSearch();

        if (search.getSearch().isEmpty()) {
            if (isAdded()) {
                Toast.makeText(getContext(), R.string.message_validate_search_form_search,
                        Toast.LENGTH_LONG).show();
            }
            return false;
        }

        if (!search.getYear().isEmpty()) {
            // TODO make the constrain on the input

            if (!(search.getYear().matches("\\d{4}"))) {
                if (isAdded()) {
                    Toast.makeText(getContext(), R.string.message_validate_search_form_year,
                            Toast.LENGTH_LONG).show();
                }

                return false;
            }
        }

        return true;
    }
}
