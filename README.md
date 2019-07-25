:tada: Andromeda Rising: A 4x game for mobile inspired by Galactic BLoodshed and others:tada:

Kotlin on Android based implementation of a game that is inspired by Galactic Bloodshed and others, e.g. Great Big 
Wargame. It's a turn based 4x game. For now, it's single player on device only, but the current long term plan is to 
make it multi-player Internet based.

Find it in the appstore: https://play.google.com/store/apps/details?id=com.zwsi.gb.app&hl=en

### Directories

gblib: contains all the game logic.
feature: (Android) frontend.
app: Not really clear why there is app and feature.

The rest are various Android Studio files. Not sure yet what they all do :-)

In addition to adding a lot more features to the game, there remains lots to do to make this better code, from tests 
to thread safety to just following general Android best practices. There is much more work to make it a better app, 
from accessibility to responsiveness to localization. 

But the initial focus is on learning Android, Kotlin, and adding basic game functionality, so we keep working on all 
fronts.

### Major Dependencies
* Dave Morrisey's awesome Subsampling Scale Image View https://github.com/davemorrissey/subsampling-scale-image-view
* Square's Moshi https://github.com/square/moshi
