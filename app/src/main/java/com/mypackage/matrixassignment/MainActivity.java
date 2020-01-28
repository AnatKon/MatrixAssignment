package com.mypackage.matrixassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private static final String urlString = "https://restcountries.eu/rest/v2/all?fields=nativeName;name;alpha3Code;area;borders";
    private static final String AREA_PLUS = "area +";
    private static final String AREA_MINUS = "area -";
    private static final String NAME_PLUS = "name +";
    private static final String NAME_MINUS = "name -";

    private List<Country> countries = new ArrayList<>();
    private List<Country> currentCountries = null;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new DownloadTextTask().execute(urlString);
    }

    private void createListView(List<Country> currentCountries){
        ListView listView = findViewById(R.id.list_view);

        ListAdapter adapter = new CountryArrayAdapter(this, currentCountries.toArray(new Country[currentCountries.size()]));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getCountriesFromBorders(currentCountries.get(position).borders);
            }
        });
    }

    public static String getJsonFromUrl(String urlStr) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            String line;
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            try {
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (Exception e) {
                Log.e("Countries", e.getMessage());
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                urlConnection.disconnect();
            }

            if (stringBuilder != null) {
                return String.valueOf(stringBuilder);
            } else {
                return "";
            }
        } catch (MalformedURLException e) {
            Log.e("Countries", "invalid URL: " + urlString);
        } catch (IOException e) {
            Log.e("Countries", "invalid URL: " + urlString);
        }
        return "";
    }

    private class DownloadTextTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            return downloadText(params[0]);
        }

        private String downloadText(String url) {
            return getJsonFromUrl(url);
        }

        @Override
        protected void onPostExecute(String s) {
            List<Country> countryList = new ArrayList<>();
            Type listType = new TypeToken<ArrayList<Country>>(){}.getType();
            // convert json into a list of Users
            try {
                Gson gson = new Gson();

                countries = gson.fromJson(s, listType);
            } catch (Exception e) {
                // we never know :)
                Log.e("error parsing", e.toString());
            }

            if(countries != null && countries.size() != 0) {

                currentCountries = countries;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createListView(countries);
                    }
                });
            } else {
                Log.e("Countries", "empty countries list");
            }
        }
    }

    private void getCountriesFromBorders(final List<String> borders){
        List<Country> borderCountries = new ArrayList<>();

        for(final String border: borders) {
            Optional<Country> optionalCountry = countries.stream().filter(p -> p.alpha3Code.equals(border)).findFirst();

            if(optionalCountry != null && optionalCountry.isPresent()) {
                borderCountries.add(optionalCountry.get());
            }
        }

        currentCountries = borderCountries;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createListView(borderCountries);
            }
        });
    }

    public void onAreaClick(View view) {
        onClickAreaName((Button)view, true, AREA_PLUS, AREA_MINUS);
    }

    public void onNameClick(View view){
        onClickAreaName((Button)view, true, NAME_PLUS, NAME_MINUS);
    }

    private void onClickAreaName(Button btn, boolean isName, String plus, String minus){
        if(isName) {
            currentCountries.sort(new SortByName());
        } else {
            currentCountries.sort(new SortByArea());
        }

        if(btn.getText().equals(plus)){
            btn.setText(minus);
        } else {
            btn.setText(plus);
            Collections.reverse(currentCountries);
        }
        createListView(currentCountries);
    }

    class SortByArea implements Comparator<Country>
    {
        public int compare(Country a, Country b)
        {
            return Double.compare(a.area, b.area);
        }
    }

    class SortByName implements Comparator<Country>
    {
        public int compare(Country a, Country b)
        {
            return a.name.compareTo(b.name);
        }
    }
}
