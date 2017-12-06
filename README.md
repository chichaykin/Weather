# Weather


![Screen 1](/images/screenshot.png)

This sample projects demonstrates how to:

* Show 5 days forecast. It uses openweathermap.org
* Installed predefined cities Dublin, London, New York and Barcelona with ability add/remove locations.
* Show weather for current location - uses GPS to get current location
* Cache network request and pictures of weather icons.

It uses following:
* Material design pattern - CoordinatorLayout, FloatButton, CardView, ToolBar, RecyclerView.
* Retrofit/Okhttp for REST communication with authorization.
* GSON for mapping JSON answer to Java POJO's.
* RxAndroid for subscription to support MVP pattern and easy testing and maintaining.
* DBFlow for easy persisting user locations.
* ButterKnife for easy view binding
* EasyAdapter to avoid writing adapter for recyclerview and use ItemViewHolder pattern.
* Picasso for weather icon downloading, caching and resizing.
* Robolectric for quick testing.

TODO:
* Add MainActivity test (espresso).
* Add layout for landscape mode to show more info for current weather.
* Add support Fahrenheit
* Add settings for temperature and pressure unit of measures
* Add permissions handling.
* Support release - add signing.
