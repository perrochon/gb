:tada: GB: A Galactic Bloodshed (and others) inspired 4x game for mobile :tada:

Steps for release, because I always forget some. // TODO Automate this

### Release
- Turn off stats display
- Increase versionCode and versionName number in 3 gradle files
- Turn of experimental persistence
- Build and test
- Commit and get commit #
- Increase version number in GBMainActivity to last commit #
- Commit again
- Create AAB
- Upload that to Android Play
- Write release notes in e-mail. Paste those into Play
- Release and send e-mail

https://stackoverflow.com/questions/17197636/is-it-possible-to-declare-a-variable-in-gradle-usable-in-java

### Release Notes

next release
- System size doubled. It's not like the game is "to scale" anyway. Larger system makes better use of screen real estate
- Double click on a planet pins the planet in a "geo-centric" view. Everything will now move around the planet.  Double click on a star, or zoom out to go back to regular heliocentric mode.
- News now updates again each turn on the main screen (and is more concise)
- #Before release Verify that all ships always visible (requires fixes)

477
-Online Tutorial https://github.com/perrochon/gb/blob/master/TUTORIAL.md
-The installed app will say .407, even if it's .477
-All ships visible even when fully zoomed out
-AI races are playing and can't be turned off
-Certain older devices are no longer supported (I am pretty sure that the app wouldn't have worked anyway). It now enforces minimum Nougat/Android 7.0 (2016). Let me know if this causes actual trouble.
 
366
-Cruisers shoot at cruisers. This means all other races are now your enemy, and will shoot  at you, and your factories. Missions are broken, but Mission 2 is now informally "Destroy the Beetle Factory"

361
- New Name: Andromeda Rising (which puts it very high up in Android's alphabetical list of apps :-)
- Play the game from the map. No more clumsy lists.
-Cruisers, that (automatically) shoot on Pods of other races. Just fly a cruiser to the Rainbow Beetle home system and wait and see. Or wait until they find you. Better have a cruiser in orbit around your planets.
-A lot of stability improvements, e.g. device rotation works better now. 
-Gameplay video: https://photos.app.goo.gl/TgM8vpACFLwv82FH6

.228
- Zoomable, pannable map of the stars (not yet restricted to the areas you explored)
- Pods can now fly to other systems, as well
- A new God mode butten called "Make". Press it once, then look at the Starmap. It's God mode, and if you don't understand what it does exactly, that would not be unexpected.

.193
- Missions! To be more accurate: Mission. The game now includes our most exciting mission ever: Tata! Mission 1. 
- Ships. They can go places.
- Lots of additional exploration: View planets of a star, view ships in a system, etc.
- Lots of bugs fixed. More bugs and bad code introduced, too.