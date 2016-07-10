package com.udacity.sandarumk.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.SimpleTimeZone;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private static final String TAG = ForecastFragment.class.getSimpleName();
    ArrayAdapter<String> mForecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Adding dummy data for the list view
        List<String> data = new ArrayList<String>();
        data.add("Mon 6/23 - Sunny - 31/17");
        data.add("Tue 6/24 - Foggy - 21/8");
        data.add("Wed 6/25 - Cloudy - 22/17");
        data.add("Thurs 6/26 - Rainy - 18/11");
        data.add("Fri 6/27 - Foggy - 21/10");
        data.add("Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18");
        data.add("Sun 6/29 - Sunny - 20/7");

        mForecastAdapter = new ArrayAdapter(getActivity(), R.layout.list_item_forcast,R.id.list_item_forecast_textview,data);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.list_view_forecast);
        listView.setAdapter(mForecastAdapter);


        return rootView;

    }

    public class FetchWeatherClass extends AsyncTask<String, Void, String[]>{

        private final String LOG_TAG = FetchWeatherClass.class.getSimpleName();


        @Override
        protected void onPostExecute(String[] result) {
            if (result !=null){
                mForecastAdapter.clear();
                for (String weatherDataforDay:result){
                    mForecastAdapter.add(weatherDataforDay);
                }

            }

        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            return roundedHigh + "/" + roundedLow ;
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            SimpleTimeZone simpleTimeZone = new SimpleTimeZone(SimpleTimeZone.UTC_TIME,"London");

            Calendar calendar = new GregorianCalendar(simpleTimeZone);

            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");


            String[] resultStrs = new String[numDays];
            for(int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);


                calendar.add(Calendar.DATE,i);
                day = shortenedDateFormat.format(calendar.getTime());


                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            return resultStrs;

        }

        @Override
        protected String[] doInBackground(String... params) {


            String postalCode = (String) params[0];
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String forecastJsonString = null;

            String json = "json";
            String metric = "metric";
            int noOfDays = 7;

            try{

                // construct the URL
                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAMETER = "q";
                final String QUERY_MODE = "mode";
                final String QUERY_UNITS = "units";
                final String QUERY_DURATION = "cnt";
                final String APP_ID = "APPID";

                Uri buildUri = Uri.parse(FORECAST_BASE_URL).buildUpon().appendQueryParameter(QUERY_PARAMETER,postalCode)
                        .appendQueryParameter(QUERY_MODE,json)
                        .appendQueryParameter(QUERY_UNITS,metric)
                        .appendQueryParameter(QUERY_DURATION,Integer.toString(noOfDays))
                        .appendQueryParameter(APP_ID,BuildConfig.OPEN_WEATHER_MAP_API_KEY).build();
                //String baseurl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7";
                //String apiKey = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;
                //URL url = new URL(baseurl.concat(apiKey));
                URL url = new URL(buildUri.toString());

               // Log.v(LOG_TAG, "Built URI " + buildUri.toString());

                // create the HTTP connection and connect
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                // input stream from the connection should be assigned to the forcast Json string
                InputStream inputStream = httpURLConnection.getInputStream();
                if(inputStream == null){
                    forecastJsonString = null;
                }

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuffer stringBuffer = new StringBuffer("");

                String line;
                while ((line = bufferedReader.readLine())!= null){
                    stringBuffer.append(line + "\n");
                }

                if (stringBuffer.length()==0){
                    forecastJsonString = null;
                }

                forecastJsonString = stringBuffer.toString();

               // Log.v(LOG_TAG,"Forecast JSON String "+ forecastJsonString);

            }catch (ProtocolException protocolex){
                protocolex.printStackTrace();
                Log.e(LOG_TAG, "onCreateView: Protocol Exception");
            }catch (IOException ioex){
                ioex.printStackTrace();
                Log.e(LOG_TAG, "onCreateView: IO Exception");
            }finally {
                if (httpURLConnection != null){
                    httpURLConnection.disconnect();
                }
                if (bufferedReader != null){
                    try{
                        bufferedReader.close();
                    }catch (IOException ioex){
                        ioex.printStackTrace();
                        Log.e(LOG_TAG, "onCreateView: io Exception");
                    }
                }
            }
            try {
                return getWeatherDataFromJson(forecastJsonString,noOfDays);
            } catch (JSONException e) {
                Log.e(LOG_TAG,e.getMessage(),e);
                e.printStackTrace();
            }

            return null;
        }


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast_fragment, menu);
        return;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_refresh){
            new FetchWeatherClass().execute("94043");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
