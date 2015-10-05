package com.example.thomasthiebaud.android.movie.controller.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.thomasthiebaud.android.movie.R;
import com.example.thomasthiebaud.android.movie.controller.fragment.DetailFragment;
import com.example.thomasthiebaud.android.movie.model.item.MovieItem;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if(savedInstanceState == null) {

            Bundle argument = new Bundle();
            argument.putParcelable(MovieItem.class.getSimpleName(), getIntent().getParcelableExtra(MovieItem.class.getSimpleName()));

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(argument);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }
}
