package com.udacity.sandarumk.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this,SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }else if (id == R.id.action_view_location){
            viewLocation();
        }


        return super.onOptionsItemSelected(item);
    }

    public void viewLocation(){
        SharedPreferences locationPreference = PreferenceManager.getDefaultSharedPreferences(this);
        String location = locationPreference.getString(getString(R.string.perf_general_edit_text_key), getString(R.string.perf_general_location_default_value));
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();
        Intent viewLocationIntent = new Intent(Intent.ACTION_VIEW);
        viewLocationIntent.setData(geoLocation);
        if (viewLocationIntent.resolveActivity(getPackageManager())!=null){
            startActivity(viewLocationIntent);
        }
    }
}
