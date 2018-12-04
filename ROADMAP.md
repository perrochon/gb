# Roadmap

#Regression
Population no longer migrates

## Code Quality
### Gating for Mission 2 (don't want to propagate bad code more)
* Unit Test Mission 1
* Import GBController.universe in all files. Use universe. only. Think this through about companion, though.
* Review all unit tests, and add whats missing (GBLocation? GBxy? Destroyed ships)
* Review all TODO and warnings
* Refactor to use resources for strings

### Not Gating
* Passing race around on commands, to make sure actions are access controlled :-)
* Review orders vs. Scheduler. Should use scheduler for orders, too.
* Upgrading thread management, maybe start using a handler
* Remove TileView library and code?
* Get Rid of Feature Module? Having one feature module is kind of pointless, it seems
* Refactor and apply naming conventions for UI elements (btn_do etc.) and other things
* Access and visibility in gblib
* Use Application for global state in Android: https://developer.android.com/reference/android/app/Application
* Debug buffer and Console Buffer. Message buffer per race. These may all be related.
* Persistency!
* Garbage Collect dead ships

## Test Efficiency
* Move makestuff logic to GBController so it uses (and tests) its methods

## Features

### Mission Features for Mission 1 (Gating for Mission 2)
* Revise all strings and fix text related issues of mission 1

### Future missions
* Fly a cruiser to new system: cruisers
* Invade enemy system and eradicate everything. Can do that without much AI, if I prepopulate with scheduler.
* Build infrastructure: planetary effects
* Play different races: Mission n could be getting another race up and running.

#### Frontend
* Run GBTest on phone (text based)
* Detect and ignore "double" clicks (say 100ms, or before doUniverse done)

### General
* Only handle one race (at a time?)
* Visibility
* God Mode Toggle
* Get a picture of the Andromeda Galaxy https://www.spacetelescope.org/images/heic1502a/ Problem is the 40k picture is not high enough, and the fullsize is 69536 x 22230 px (4.3GB, psb file). That could give me a 22,000 square picture, or 484k pixel (right now I have 18k (325k pixel). This is a 50% increase in pixels - and image size from 40MB to say 60M (compressed). Original resolution of the Hubble image (a third of the total) would be ~2GB. Not sure how to compress that.

### Star Map
* Show Races Home Planet for all races (only interesting in God mode)
* Ship icons
* Better fade out on zoom
* can we invalidate map after doUniverse is done? And put a do Button on the map? 
* Some icons need to stay at fixed distance in screen pixels, e.g. star icon, name, and race icon. Right now they are fixed in source coordinates, and thus move closer when you zoom out.

#### Planet
* Move planets around their star
* Resources. Money, other. Probably Money first, as a summary for all else
* multiple races per planet
* Environment and impact on migration and population growth
* Use Population curves to max out population, instead of exponential growth

#### Systems
* Planet locations, render, and movement
* Show clickable ships

#### Universe
* Statistics on population, etc.
* Show zoomable, clickable maps  :tata:

####Races
* Give races home planets
* Show ships of that race from race screen
* Make more distinct races
* Visibility and actions per race (and god mode cleanup)
* "AI" :-). One race can just send one pod per turn to each planet...

#### Ships
* load/unload population into ships
* More planetary units
* Locations of planetary units
* more space units
* destroy ships (required for :tata: Battle :tata:

#### Interactivity
* land battle (can do this for morphs with pods only)
* ship battle

## Android 
* Dealing with killed applications, and destroyed activities... Need persistence first.
https://stackoverflow.com/questions/29701660/can-i-detect-if-android-has-killed-the-application-task-process-from-a-notific
