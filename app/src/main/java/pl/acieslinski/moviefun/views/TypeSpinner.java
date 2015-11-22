package pl.acieslinski.moviefun.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import pl.acieslinski.moviefun.models.Type;

/**
 * @author Arkadiusz Cieśliński 14.11.15.
 *         <acieslinski@gmail.com>
 */
public class TypeSpinner extends Spinner {
    public TypeSpinner(Context context) {
        super(context);

        init();
    }

    public TypeSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Type[] types = Type.values();

            TypesAdapter adapter = new TypesAdapter(getContext(),
                    android.R.layout.simple_spinner_item, types);

            setAdapter(adapter);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }
    }

    public Type getSelectedType() {
        return (Type) getSelectedItem();
    }

    public void setSelection(Type type) {
        TypesAdapter typeAdapter = (TypesAdapter) getAdapter();

        int position = typeAdapter.getPosition(type);
        setSelection(position);
    }

    private class TypesAdapter extends ArrayAdapter<Type> {

        public TypesAdapter(Context context, int resource, Type[] objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            // makes equal padding as for EditText (padding set in styles.xml)
            view.setPadding(0, view.getPaddingTop(), view.getPaddingRight(),
                    view.getPaddingBottom());

            return view;
        }
    }
}
