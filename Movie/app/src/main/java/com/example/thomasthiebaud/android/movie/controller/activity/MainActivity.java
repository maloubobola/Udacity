package com.example.thomasthiebaud.android.movie.controller.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.thomasthiebaud.android.movie.R;
import com.example.thomasthiebaud.android.movie.controller.fragment.DetailFragment;
import com.example.thomasthiebaud.android.movie.controller.fragment.MainFragment;
import com.example.thomasthiebaud.android.movie.model.item.MovieItem;

public class MainActivity extends AppCompatActivity implements MainFragment.MovieClickCallback {
    private boolean isTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.movie_detail_container) != null) {
            this.isTwoPane = true;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, new DetailFragment())
                    .commit();
            findViewById(R.id.movie_detail_container).setVisibility(View.GONE);
        }
        else {
            isTwoPane = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieSelected(MovieItem movie) {
        if(isTwoPane) {
            Bundle argument = new Bundle();
            argument.putParcelable(MovieItem.class.getSimpleName(), movie);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(argument);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container,fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class).putExtra(MovieItem.class.getSimpleName(),movie);
            startActivity(intent);
        }
    }
}
