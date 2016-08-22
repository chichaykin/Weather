package com.mich.weather.ui;

import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.mich.weather.di.components.ActivityComponent;
import com.mich.weather.di.components.DaggerActivityComponent;
import com.mich.weather.di.modules.ActivityModule;


public class BaseActivity extends AppCompatActivity {

    private ActivityComponent mActivityComponent;

    public ActivityComponent activityComponent() {
        if (mActivityComponent == null) {
            mActivityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    //.applicationComponent(AndroidBoilerplateApplication.get(this).getComponent())
                    .build();
        }
        return mActivityComponent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentManager fm = getFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } else {
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
