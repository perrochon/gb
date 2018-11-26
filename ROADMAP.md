
# Roadmap

## Code Quality
* Refactor to use GBLocation for ship creation, ship movement, and everywhere else, really...
* Make pod movement in Mission 1 take off, fly, land
* Refactor and apply Naming conventions for UI elements (btn_do etc.)
* Unit Test Mission 1
* Make Location Object. Should handle stars, too.
* Access and visibility in gblib
* Use Application for global state in Android: https://developer.android.com/reference/android/app/Application
* Debug buffer and Console Buffer
* Persistency of the lib

## Test Efficience
* Make God level button that takes me to a setup where I can test what I am working on in the UI


## Features

### Mission Features for Mission 1
* fly and land pods

### General
* Send intents to sliders with which list to show. Display that on slider
* Visibility and God Mode Switch

#### Planet
* multiple races per planet
* Environment and impact on breeding
* Resources. Money, other.

#### Systems
* Planet locations, render, and movement

#### Universe
* Statistics on population, etc.

####Races

#### Ships
* Pods
* Planetary units
* Ships

#### Interactivity
* land battle (can do this for morphs with pods only)
* build ships
* move ships
* ship battle

#### Frontend
* Cut more star background images, and rotate them through the views
* Handle killed process. Need to persist Universe?
* Run GBTest on phone (text based)

## Android 

* Dealing with Killed applications, and destroyed activities... Need persistence first.
https://stackoverflow.com/questions/29701660/can-i-detect-if-android-has-killed-the-application-task-process-from-a-notific

* TileView crash on Map. Why?