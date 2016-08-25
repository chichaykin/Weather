package com.mich.weather.di.components;


import com.mich.weather.di.modules.ActivityModule;
import com.mich.weather.di.scopes.PerActivity;
import com.mich.weather.ui.MainActivity;

import dagger.Component;

@SuppressWarnings("ALL")
@PerActivity
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(MainActivity activity);
}
