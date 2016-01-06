package com.udacity.gradle.builditbigger;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import thiebaudthomas.display.DisplayActivity;

public class MainActivity extends ActionBarActivity {

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
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
            protected void onPostExecute(final String result) {
                final Intent intent = new Intent(getBaseContext(), DisplayActivity.class);
                mInterstitialAd.show();
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        intent.putExtra(IntentContract.JOKE, result);
                        startActivity(intent);
                        progressBarLayout.setVisibility(View.GONE);
                    }
                });
            }
        }.execute();
    }
}
