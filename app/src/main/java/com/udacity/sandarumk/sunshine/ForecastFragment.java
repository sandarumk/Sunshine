package com.udacity.sandarumk.sunshine;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.os.AsyncTaskCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private static final String TAG = ForecastFragment.class.getSimpleName();

    public ForecastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Adding dummy data for the list view
        List<String> data = new ArrayList();
        data.add("Mon 6/23 - Sunny - 31/17");
        data.add("Tue 6/24 - Foggy - 21/8");
        data.add("Wed 6/25 - Cloudy - 22/17");
        data.add("Thurs 6/26 - Rainy - 18/11");
        data.add("Fri 6/27 - Foggy - 21/10");
        data.add("Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18");
        data.add("Sun 6/29 - Sunny - 20/7");

        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.list_item_forcast,R.id.list_item_forecast_textview,data);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.list_view_forecast);
        listView.setAdapter(arrayAdapter);


        return rootView;
    }

    public class FetchWeatherClass extends AsyncTask{

        private final String LOG_TAG = FetchWeatherClass.class.getSimpleName();

        @Override
        protected Object doInBackground(Object[] params) {
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String forecastJsonString = null;

            try{

                // construct the URL
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

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

                StringBuffer stringBuffer = null;

                String line;
                while ((line = bufferedReader.readLine())!= null){
                    stringBuffer.append(line + "\n");
                }

                if (stringBuffer.length()==0){
                    forecastJsonString = null;
                }

                forecastJsonString = stringBuffer.toString();

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
            return forecastJsonString;
        }

    }
}
