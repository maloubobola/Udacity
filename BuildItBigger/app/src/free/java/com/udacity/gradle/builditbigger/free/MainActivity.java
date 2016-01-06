package com.udacity.gradle.builditbigger.free;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.udacity.gradle.builditbigger.EndPointTask;
import com.udacity.gradle.builditbigger.IntentContract;
import com.udacity.gradle.builditbigger.R;

import thiebaudthomas.display.DisplayActivity;

public class MainActivity extends ActionBarActivity {

    private InterstitialAd mInterstitialAd;
    private String joke = "";
    private LinearLayout progressBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                progressBarLayout.setVisibility(View.GONE);
                requestNewInterstitial();
                displayJoke();
            }
        });

        requestNewInterstitial();
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

            @Override
            protected void onPreExecute() {
                progressBarLayout = (LinearLayout) findViewById(R.id.progress_bar_layout);
                progressBarLayout.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(final String result) {
                if(mInterstitialAd.isLoaded())
                    mInterstitialAd.show();

                joke = result;
            }
        }.execute();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void displayJoke() {
        if(!joke.isEmpty()) {
            Intent intent = new Intent(getBaseContext(), DisplayActivity.class);
            intent.putExtra(IntentContract.JOKE, joke);
            startActivity(intent);
        }
    }

}
