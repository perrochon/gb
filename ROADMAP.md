# Roadmap


## Guiding Principles
Right now, the focus is on enabling game play, i.e. add features that allow more game play. Features that don't enhance
game play, or prevent game play are second priority. Here are a few examples
* Limits on money: Players will be resource constraint. For now, enjoy unlimited resources.
* Fleshed out missions: Missions keep changing, so they are bare bones, and only described in the tutorial.
* Placeholder graphics: Things are very much in flux, so there are no final creative assets anywhere.

## Short Term Small Items
* Money should come next...really
* Play Impis in Map (generally fix who plays)
* Fix action counts
* Track mission wins
* Deploy remaining ships
* DO skips frames (on emulator) - make sure we minimize work on UI thread
* Moving Dot animation on squares follow the square, instead of a circle. Drive by a parameter of the ship (extension)
* same ssytem patrol flies to inside of circle, instead of outside...
* Settings: Clean code smell
* Fog of War: per race, 3 states (unknown, seen, visible). Once you saw a system, you remember planets...
* Re-creating missions on each build and copy them into res directory. Or create missions in the app, not tests...

## Short Term Larger Items
* Population on planets (per race, and do correctly). Fix "Make Factory" etc.
* Money as primary resource
* Settings! stats, click targets, fog of war/advanced sensing. AI support (off, low (build factories), full (like Impi))
* Different speeds on auto, or until contact/battle
* Fog of war (gating for player mode)
    * Passing race around on commands, to make sure actions are access controlled :-)
* Player mode (not just god mode).
* Manual Shooting?

## Regression

## Code Quality
* Turn colors to proper resource strings. Then parse them once.
* Tests for patrol points
* Review all TODO and warnings
* Unit Test Missions (or at least the lib part...)
* Review all unit tests, and add whats missing (GBLocation? GBxy? )
* Refactor to use resources for strings for Missions
* Strings to resources. What to do about library strings?
* Clean up logging. Get rid of GBDebug?
* Get Rid of Feature Module? Or move image into app feature? Then we may not need to upload/download the image each time?

### Not Gating
* Different layouts for landscape/tablet
* Big: Problem with GBLib relying on u.() when it should use vm.x() for view model. Workaround for ship location in place.
* Refactor firing solution and move logic for firing and taking damage into each ship's class (depends on ship refactor)
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

## Design
* Make "DO" a FloatingActionButton? It's the "primary activity" in the game...

## Features

### Future missions
* Invade enemy system and eradicate everything.
* Build infrastructure: planetary effects
* Play different races: Mission n could be getting another race up and running.
* Research

#### Frontend
* Mark "active" object (the one with the detail screen open). Maybe a colored click circle?
* Run Tests on phone (text based?)

### General
* Visibility
* God Mode Toggle
* Get a picture of the Andromeda Galaxy https://www.spacetelescope.org/images/heic1502a/ Problem is the 40k picture is not high enough, and the fullsize is 69536 x 22230 px (4.3GB, psb file). That could give me a 22,000 square picture, or 484k pixel (right now I have 18k (325k pixel). This is a 50% increase in pixels - and image size from 40MB to say 60M (compressed). Original resolution of the Hubble image (a third of the total) would be ~2GB. Not sure how to compress that.

### Star Map

#### Planet
* Resources: Money, other. Probably money first, as a summary for all else
* Population: multiple races per planet
* Environment and impact on migration and population growth

#### Systems
* Stats for systems

#### Universe
* Statistics on population, etc.

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
* Download background image separately (so it doesn't have to be downloaded each time)

