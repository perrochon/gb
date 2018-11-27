
# Roadmap

## Code Quality
* Review all warnings
* Review all TODO
* Get Rid of Feature Module. Having one feature module is kind of pointless, it seems
* Refactor to use GBLocation for ship creation, ship movement, and everywhere else
* Refactor to use resources for strings
* Refactor and apply Naming conventions for UI elements (btn_do etc.) and other things
* Unit Test Mission 1
* Access and visibility in gblib
* Use Application for global state in Android: https://developer.android.com/reference/android/app/Application
* Debug buffer and Console Buffer. Message buffer per race. These may all be related.
* Persistency!

## Test Efficience
* Make God level button that takes me to a setup where I can test what I am working on in the UI


## Features

### Mission Features for Mission 1
* Make pod movement in Mission 1 multistep with take off, fly, land, instead of telepor to new planet (depends on GBLocation)
* Revise all strings and fix text related isues of mission 1

### Future missions
* Fly a cruiser to new system: cruisers, flying in deep space
* Build infrastructure: planetary effects
* Play different races: Mission two could be getting another race up and running.

### General
* Visibility and God Mode Switch

#### Planet
* multiple races per planet
* Environment and impact on migration and population growth
* Resources. Money, other.

#### Systems
* Planet locations, render, and movement
* Show clickable ships

#### Universe
* Statistics on population, etc.
* Show zoomable, clickable maps  :tata:

####Races
* Show ships of race
* Make more distinct races
* Visibility per race (and god mode cleanup)

#### Ships
* More planetary units
* Locations of planetary units
* other ship

#### Interactivity
* land battle (can do this for morphs with pods only)
* ship battle

#### Frontend
* Cut more star background images, and rotate them through the views
* Handle killed process. Need to persist Universe?
* Run GBTest on phone (text based)

## Android 
* Dealing with killed applications, and destroyed activities... Need persistence first.
https://stackoverflow.com/questions/29701660/can-i-detect-if-android-has-killed-the-application-task-process-from-a-notific
* TileView crash on Map. Why? :tata: