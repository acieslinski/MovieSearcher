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
