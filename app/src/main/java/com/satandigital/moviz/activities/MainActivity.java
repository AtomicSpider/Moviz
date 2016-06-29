package com.satandigital.moviz.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.satandigital.moviz.MovizApp;
import com.satandigital.moviz.R;
import com.satandigital.moviz.common.AppCodes;
import com.satandigital.moviz.fragments.MoviesFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/14/2016.
 */
public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    public static MainActivityCallback mCallback;

    //Views
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public static Spinner spinner_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setUpSpinner();
        setUpToolbar();
    }

    private void setUpSpinner() {
        spinner_toolbar = (Spinner) findViewById(R.id.spinner_toolbar);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_items, R.layout.spinner_item_actionbar);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_toolbar.setAdapter(adapter);

        spinner_toolbar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        mCallback.MainActivityCallbackRequest(AppCodes.PREF_MOVIE_LIST_POPULAR);
                        break;
                    case 1:
                        mCallback.MainActivityCallbackRequest(AppCodes.PREF_MOVIE_LIST_TOP_RATED);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (MovizApp.movieListType.equals(AppCodes.CODE_TITLE_POPULAR))
            spinner_toolbar.setSelection(0);
        else if (MovizApp.movieListType.equals(AppCodes.CODE_TITLE_TOP_RATED))
            spinner_toolbar.setSelection(1);
    }

    public static void disableSpinner() {
        if (spinner_toolbar != null)
            spinner_toolbar.setEnabled(false);
    }

    public static void enableSpinner() {
        if (spinner_toolbar != null)
            spinner_toolbar.setEnabled(true);
    }

    public interface MainActivityCallback {
        void MainActivityCallbackRequest(String type);
    }

    public static void setCallback(MainActivityCallback callback) {
        mCallback = callback;
    }
}
