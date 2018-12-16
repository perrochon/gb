# Other Notes

##Log

2018-11-25 Released .193 Mission 1: Fly a pod to another system. Needs major re-factor on location, and will need to 
move all copy to resources, instead of strings inKotlin

2018-11-22 Thanksgiving. Thought of doing missions so game playable much sooner

2018-11-16
Released .76 Bunch of bug fixes and small improvements. Added basic Race View Activity with a picture of an Impi

2018-11-15
Fully Migrating Java to Kotlin. Cleaning up lots of stuff while at it, like moving static data into GBData. Migration
broke badly, and it was not clean with race allocation to sector, so commented out right now.

2018-11-14
Single planet view and colonize (through teleportation! saves implementing ships). Now have minimal "colonize the
universe functionality". Code is in bad shape, so starting to clean up. Don't want to add more functionality this way.
Need to refactor, and planning to switch everything to Kotlin. GDrive installs on all my phones broke, but work on other
phones. Not clear why.

2018-11-13
Android Studio couldn't write settings anymore. Couldn't restart. Windows reboot failed with Bitlock error. Recovery
took only the morning, mostly because it came back and I don't really know why.
Also fixed z coordinates in main, and text colors. More readable and vivid now, but not ideal
Third activity StarsView which required updating the gblib with star coordinates and an algo to distribute stars

2018-11-12
Creating second activity to deal with planets so we can have a ScrollView of planets

2018-11-11
Working on rendering one hard coded planet

2018-11-10
More refactoring. Two races. Buttons!

2018-11-09
Scrollable TextView in App displays output of text. Refactored classes out of a single file into multiple.

2018-11-08
Started this Log. Finally Enabled Hyper-V. It's under "Security" in the BIOS! Emulator is now running faster.


## Sources
//
// Content Sources used
// Notes.txt: http://www.jongware.com/galaxy1.html
// FAQ: http://web.archive.org/web/20060501033212/http://monkeybutts.net:80/games/gb/

// Must include following file: https://github.com/kaladron/galactic-bloodshed/blob/master/LICENSE
// List of Planet Names: https://github.com/kaladron/galactic-bloodshed/blob/master/data/planet.list
// List of Stars: https://github.com/kaladron/galactic-bloodshed/blob/master/data/star.list

// Terrain Assets
https://assetstore.unity.com/packages/2d/environments/painted-2d-terrain-tiles-basic-set-45675

