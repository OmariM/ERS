# Egyptian Ratscrew for Android
Egyptian Ratscrew Android Application for Mobile Software Development (AIT Spring 2020)

Your (my) favorite speed slapping card came has come to Android! Title is pretty self explanitory and so is the app so if you're looking here for instructions on how to play, you're out of luck. Go find a friend and ask them how to play or something. 

Please have mercy on me. I've only been doing Android dev in Kotlin for about 4 months. Do __NOT__ look into the game class ~~horrendous code awaits~~.

## Current Features
- you can play a solo game against 6 computers. they're pretty slow but from time to time they'll catch you slippin
- two people can play together by holding the phone at each end

## Upcoming Features
(in order of likelyhood/time)
- when creating a single player game, user can choose how many bots they want and the difficulty
- user can have a profile to save stats like slap time or something
- have settings where the user can enable ~~stupid~~ extra game rules like slapping on marriages, 10s, and sums of 10
- add a splash screen
- add animations to make things more realistic
- persistant storage / pausing local games
- implement local multiplayer via UDP server?
- implement online multiplayer via UDP server???? massive stretch goal. if anyone has ideas i'd love to hear them
- ???
- profit 💰
- iOS version???????

## Technical Stuff / Bug Fixes
- convert to coroutines because threads are a pain in the butt and inefficient
- there's a small bug with computers dealing way too fast after a slap on the final card of a king streak. pretty sure this is due to a lack of a live check in between the computerSlap and the computerDeal call but who knows
- convert to fragments. I have a feeling this will also decrease clutter in the activity level
- clean up button handling so it all happens in one place (preferably the game class, just not the activities 😪)
- clean up player/computer objects in general cause they're kinda gross
- make unit tests for game logic
- clean game logic so that it is ***both*** correct __and__ efficient. as it stands right now I refactored huge blocks of code into functions but let's be honest that's basically cheating by moving unreadable code to somewhere else
- find a way to kill the threads when you leave a game. the computer threads still run in the background so yeah that one is gonna have to stop lol
- make my models serializable / look into the Parcelable librabry so I can pass those objects through intents and store them online

I would like to thank Péter Ekler for being a fantastic teacher and fueling me through each class with his jokes :)
