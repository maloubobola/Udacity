package com.udacity.gradle.builditbigger.paid;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.udacity.gradle.builditbigger.EndPointTask;
import com.udacity.gradle.builditbigger.IntentContract;
import com.udacity.gradle.builditbigger.R;

import thiebaudthomas.display.DisplayActivity;

public class MainActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) return true;
        return super.onOptionsItemSelected(item);
    }

    public void tellJoke(View view){
        new EndPointTask() {
            private LinearLayout progressBarLayout;

            @Override
            protected void onPreExecute() {
                progressBarLayout = (LinearLayout) findViewById(R.id.progress_bar_layout);
                progressBarLayout.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String result) {
                Intent intent = new Intent(getBaseContext(), DisplayActivity.class);
                intent.putExtra(IntentContract.JOKE, result);
                startActivity(intent);
                progressBarLayout.setVisibility(View.GONE);
            }
        }.execute();
    }
}
