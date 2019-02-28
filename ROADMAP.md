# Roadmap

## Guiding Principles
Right now, the focus is on enabling game play, i.e. add features that allow more game play. Features that don't enhance
game play, or prevent game play are second priority. Here are a few examples
* Fog of war: Players will not be able to see other fractions unless they are in range. For now everything 
is visible.
* Limits on money: Players will be resource constraint. For now, enjoy unlimited resources.
* Fleshed out missions: Missions keep changing, so they are bare bones, and only described in the tutorial.

## Short Term
* Bug: Planetary ships bounce around on continues updates and drag
* Bug: Clicks register to targets when too far away for high zoom level.
* Clean up deprecated files... Too much work maintaining them on refactoring :-(
* Better looking spinner - may need to copy more XML
* Destination of star system for ships far away, instead of first planet.
* re-creating missions on compile and copying them into res directory.
* Population on planets (per race, and do correctly). Fix "Make Factory"
* Player mode (not just god mode).
    * Passing race around on commands, to make sure actions are access controlled :-)

## Regression

## Code Quality
* Unit Test Mission 1 (or at least the lib part...)
* Review all unit tests, and add whats missing (GBLocation? GBxy? Destroyed ships)
* Review all TODO and warnings
* Cleanup directory structure (remove deprecated files, move documentation, etc.)
* Refactor to use resources for strings for Missions

### Not Gating
* Refactor firing solution and move logic for firing and taking damage into each ship's class (depends on ship refactor)
* Remove TileView library and code?
* Get Rid of Feature Module? Having one feature module is kind of pointless, it seems
* Refactor and apply naming conventions for UI elements (btn_do etc.) and other things
* Access and visibility in gblib
* Use Application for global state in Android: https://developer.android.com/reference/android/app/Application
* Debug buffer and Console Buffer. Message buffer per race. These may all be related.
* On Animations: "Nowadays, Android documentation clearly recommends not to use resources directly from android.R.*, since every 
release of the platform has changes on them. Even some resources dissapear from one version to another, 
so you shouldn't rely on them. On the other hand, lots of resources are private and not available from a developer's code."
* Revise all strings and fix text related issues. Not clear we care about localization, as it's just too expensive

## Test Efficiency
* Fast and Slow Tests

## Features

### Future missions
* Load Missions as GBSavedData would be nice. Each Mission load a new Universe.
* Invade enemy system and eradicate everything.
* Build infrastructure: planetary effects
* Play different races: Mission n could be getting another race up and running.
* Research

#### Frontend
* Mark "active" object (the one with the detail screen open). Maybe a colored click circle?
* Run Tests on phone (text based)

### General
* Only handle one race (at a time?)
* Visibility
* God Mode Toggle
* Get a picture of the Andromeda Galaxy https://www.spacetelescope.org/images/heic1502a/ Problem is the 40k picture is not high enough, and the fullsize is 69536 x 22230 px (4.3GB, psb file). That could give me a 22,000 square picture, or 484k pixel (right now I have 18k (325k pixel). This is a 50% increase in pixels - and image size from 40MB to say 60M (compressed). Original resolution of the Hubble image (a third of the total) would be ~2GB. Not sure how to compress that.

### Star Map
* Ship icons
* Better fade out on zoom

#### Planet
* Resources: Money, other. Probably money first, as a summary for all else
* Population: multiple races per planet
* Environment and impact on migration and population growth

#### Systems
* Stats for systems

#### Universe
* Statistics on population, etc.
* Show zoomable, clickable maps  :tata:

####Races
* Give races home planets in GBData
* Show ships of that race from race screen (don't have race screen right now, maybe access from planet or ship?)
* Make more distinct races
* Visibility and actions per race (and god mode cleanup)

#### Ships
* load/unload population into ships
* More planetary units
* Locations of planetary units
* more space units: Battleship, defense stations
* ships on system patrol and/or orbit

#### Interactivity
* land battle (can do this for morphs with pods only)
* ship battle. Animated, serialized?

## Android 
* Dealing with killed applications, and destroyed activities... Need persistence first.
https://stackoverflow.com/questions/29701660/can-i-detect-if-android-has-killed-the-application-task-process-from-a-notific
* Download background image separately (so it doesn't have to be downloaded each time)

