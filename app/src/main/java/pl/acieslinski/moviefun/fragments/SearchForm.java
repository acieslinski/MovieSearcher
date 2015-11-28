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

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import pl.acieslinski.moviefun.R;
import pl.acieslinski.moviefun.models.Search;
import pl.acieslinski.moviefun.models.SearchEvent;
import pl.acieslinski.moviefun.views.TypeSpinner;

/**
 * @author Arkadiusz Cieśliński 14.11.15.
 *         <acieslinski@gmail.com>
 */
public class SearchForm extends Fragment {
    private static final String TAG = SearchForm.class.getSimpleName();

    @Bind(R.id.et_search)
    protected EditText mSearchEditText;
    @Bind(R.id.et_year)
    protected EditText mYearEditText;
    @Bind(R.id.sp_type)
    protected TypeSpinner mTypeSpinner;
    @Bind(R.id.btn_search)
    protected Button mSearchButton;

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

    public void setOnSearchButtonClickListener(View.OnClickListener listener) {
        mSearchButton.setOnClickListener(listener);
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

    public boolean validateForm() {
        Search search = getSearch();
        boolean valid = true;

        if (search.getSearch().isEmpty()) {
            if (isAdded()) {
                YoYo.with(Techniques.Shake).delay(300).playOn(mSearchEditText);
            }

            valid = false;
        }

        if (!search.getYear().isEmpty()) {
            if (!(search.getYear().matches("\\d{4}"))) {
                if (isAdded()) {
                    YoYo.with(Techniques.Shake).delay(300).playOn(mYearEditText);
                }

                valid = false;
            }
        }

        return valid;
    }
}
