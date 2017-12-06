package com.mich.weather.ui;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import butterknife.BindView;
import com.mich.weather.R;
import com.mich.weather.data.WeatherResponse;
import com.mich.weather.presenters.MainPresenter;
import com.mich.weather.presenters.MainView;
import com.mich.weather.repositories.WeatherLocationPojo;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;


@SuppressWarnings({"WeakerAccess", "unused"})
public class MainActivity extends BaseActivity implements AddDialog.AddDialogListener, MainView {
    private SpinnerAdapter mSpinnerAdapter;

    @BindView(R.id.spinner)
    Spinner mSpinner;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Inject
    MainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        activityComponent().inject(this); //init all injected fields here

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Setup spinner
        mSpinnerAdapter = new SpinnerAdapter(toolbar.getContext());
        mSpinner.setAdapter(mSpinnerAdapter);

        mSpinner.setOnItemSelectedListener(new SpinnerClickListener());

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
             @Override
             public void onRefresh() {
                 mPresenter.onRefresh();
             }
         });

        mPresenter.onActivityCreate();
    }

    @Override
    public WeatherLocationPojo getCurrentLocation() {
        int pos = mSpinner.getSelectedItemPosition();
        return mSpinnerAdapter.getItem(pos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_add_location:
                DialogFragment dialog = new AddDialog();
                dialog.show(getSupportFragmentManager(), "ADD");
                return true;

            case R.id.action_remove_location:
                // remove current location
                WeatherLocationPojo location = (WeatherLocationPojo) mSpinner.getSelectedItem();
                mPresenter.removeLocation(location);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onDialogPositiveClick(String city, String country) {
        mPresenter.addLocation(city, country);
    }

    @Override
    public void showProgress(boolean visible) {
        getSupportFragmentManager().beginTransaction().replace(
                R.id.container, visible ? ProgressFragment.newInstance() : CurrentWeatherFragment.newInstance())
                .commitAllowingStateLoss();
        mSwipeRefreshLayout.setRefreshing(visible);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    public WeatherResponse getCurrentWeatherData() {
        return mPresenter.getCurrentWeatherData();
    }

    @Override
    public void updateSpinnerData(List<WeatherLocationPojo> list) {
        // init spinner
        mSpinnerAdapter.addAll(list);
        mSpinnerAdapter.notifyDataSetChanged();
    }

    @Override
    public void replaceWeatherFragment() {
        getSupportFragmentManager().beginTransaction().replace(
                R.id.container, CurrentWeatherFragment.newInstance())
                .commit();
    }

    @Override
    public void spinnerAddLocation(WeatherLocationPojo location) {
        mSpinnerAdapter.add(location);
        mSpinnerAdapter.notifyDataSetChanged();
    }

    @Override
    public void spinnerSelectLocation(WeatherLocationPojo location) {
        int pos = mSpinnerAdapter.getPosition(location);
        mSpinner.setSelection(pos);
    }

    @Override
    public void spinnerRemoveLocation(WeatherLocationPojo location) {
        mSpinnerAdapter.remove(location);
        mSpinnerAdapter.notifyDataSetChanged();
    }

    @Override
    public void snackMessage(@StringRes int message) {
        Snackbar.make(mSwipeRefreshLayout, getString(message), Snackbar.LENGTH_LONG).show();
    }

    private class SpinnerClickListener implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // When the given dropdown item is selected, show its contents in the
            // container view.
            mPresenter.onSpinnerClick(mSpinnerAdapter.getItem(position));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    }

}