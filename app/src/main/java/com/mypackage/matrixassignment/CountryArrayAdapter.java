package com.mypackage.matrixassignment;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CountryArrayAdapter extends ArrayAdapter<Country> {

    private Activity context;
    private Country[] countries;

    public CountryArrayAdapter(Activity context, Country[] countries){
        super(context, R.layout.layout_country, R.id.txtNativeName, countries);
        this.context = context;
        this.countries = countries;
    }

    public static class ViewContainer {
        private TextView txtNativeName;
        private  TextView txtEnglishName;

        public ViewContainer() {
        }

        public TextView getTxtNativeName() {
            return txtNativeName;
        }

        public void setTxtNativeName(TextView txtNativeName) {
            this.txtNativeName = txtNativeName;
        }

        public TextView getTxtEnglishName() {
            return txtEnglishName;
        }

        public void setTxtEnglishName(TextView txtEnglishName) {
            this.txtEnglishName = txtEnglishName;
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewContainer viewContainer;
        View rowView = convertView;

        if(rowView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.layout_country, null, true);
            viewContainer = new ViewContainer();
            viewContainer.setTxtNativeName((TextView)rowView.findViewById(R.id.txtNativeName));
            viewContainer.setTxtEnglishName((TextView)rowView.findViewById(R.id.txtEnglishName));
            rowView.setTag(viewContainer);
        } else {
            viewContainer = (ViewContainer) rowView.getTag();
        }

        viewContainer.getTxtNativeName().setText(countries[position].nativeName);
        viewContainer.getTxtEnglishName().setText(countries[position].name);

        return rowView;
    }
}
