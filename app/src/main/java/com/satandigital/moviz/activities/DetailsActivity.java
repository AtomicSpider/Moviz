package com.satandigital.moviz.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.satandigital.moviz.R;
import com.satandigital.moviz.fragments.DetailsFragment;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/15/2016.
 */
public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        attachFragment(savedInstanceState);
    }

    private void attachFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_details_container, new DetailsFragment())
                    .commit();
        }
    }
}
