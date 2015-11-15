package pl.acieslinski.moviefun.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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
        Type[] types = Type.values();

        TypesAdapter adapter = new TypesAdapter(getContext(),
                android.R.layout.simple_spinner_item, types);
        setAdapter(adapter);
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
    }
}
