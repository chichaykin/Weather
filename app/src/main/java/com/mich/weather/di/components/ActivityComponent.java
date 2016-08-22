package com.mich.weather.di.components;


import com.mich.weather.di.modules.ActivityModule;
import com.mich.weather.di.scopes.UserScope;
import com.mich.weather.ui.MainActivity;

import dagger.Component;

@UserScope
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(MainActivity activity);
}
