# 1.3.0 - 2018-12-22

- Removed ads! Burger Party is now free software
- Added a goodies button
- Replaced Facebook and Google+ buttons with Mastodon and Twitter buttons

# 1.2.1 - 2015-01-27

- Fix missing translations
- Nicer buttons in the configuration dialog

# 1.2.0 - 2015-01-17

- Added a sumo customer
- Turned the "more" menu into a dialog with links to Facebook, Google+ and Google Play

# 1.1.3 - 2014-11-06

- Fixed bug in score count in Kids difficulty level

# 1.1.2 - 2014-11-03

- Fix "Star Collector" achievement appearing 3 times in achievement screen
- Make parsing of saved data more resilient

# 1.1.1 - 2014-10-30

- Introduced three difficulty levels:
    - kids: no global timer, customers are more patient
    - normal: just like before
    - expert: shorter times, less patient customers, no arrow indicating the next item to add

# 1.1.0 - 2014-10-07

- Made the game slightly harder

- New item: Pineapple replaces salad in pirate world

- Sandbox mode: Use a world map icon for the world selector button

# 1.0 - 2014-09-26

- Slightly increased size of burgers, made super-big burgers 3 stages high

- Improved the game icon

- Made character a bit taller in cut scenes

# 1.0rc2 - 2014-09-10

- Fixed the annoying intermittent "can't add any item after an error" bug

- Adjusted proportions of character in "new world" cut scenes

- Adjusted colors of pickles

- New sounds for soda and sundae, reassigned existing sounds to other
  ingredients

# 1.0rc1 - 2014-09-02

- Made the game a little bit easier

- Big fries appear earlier (level 2-6 instead of 3-6)

- Doubled the number of burgers required for "Apprentice", "King" and "God"
  achievements

- Reworked loading image: it now uses flat shading and is smoothly scaled on
  bigger screens

- The "new item" animation on the level list has been improved

- Redrawn salad one more time

# 0.17 - 2014-08-22 (not released on the Internet)

- Reworked the appearance of many meal items:
  - Cucumbers
  - Meat
  - Cheese
  - Salad
  - Bottom bun (looks nicer when used as a middle bun)

- Three new ingredients have been added: bacon and two sundaes (chocolate and
  strawberry). To accommodate those new ingredients, new levels have been added:
  worlds now come with 15 levels.

- More sound effects:
  - All images of the "new world" animation have sound effects

  - There is now a sound effect when one get a "Perfect" and a short jingle for
    the new item unlocked screen

  - The game over jingle has been redone

- There is now a tutorial when playing level 1.1

- Credits now list the authors of all fonts and sound effects used in the game

- There is now an indicator which appears over the achievement button when a
  new achievement has been unlocked

- Changed layout of "Pause", "Level Finished" and "Game Over" overlays. In
  particular, the "Pause" and "Level Finished" overlays now provides an
  achievement button.

# 0.16 - 2014-07-13

- The game difficulty has been adjusted: levels are shorter and a bit harder,
  making the game more challenging. One now gains back up to 4 seconds of extra
  time when customers are satisfied.
- Burgers are now more "realistic": all stages now have at least one meat or
  fish ingredient, two sauces cannot be stacked on each others
- Pirate world: Background has been redrawn.
- Japan world: Added a boss.
- The "new world" sequences are a bit longer: they show the heroine leaving the
  current world and boarding a plane to the next world. The maps have also been
  improved.

# 0.15 - 2014-05-10

- When two achievements are unlocked at the same time, their notifications do
  not overlap themselves anymore.
- Make sure the content of the customer bubble always fit in the screen.
- Translated texts of achievements added in 0.14 to French.
- Reworked how ads are shown: they now appear between levels, never before
  first level and no more than once every 5 minutes.
- Use linear filtering to shrink images on devices with low resolution screen.

# 0.14 - 2014-04-18

- New achievements have been added:
    - Creative: create 10 different burgers in sandbox mode.
    - Fan: play 40 levels.
    - Burger Apprentice: create 25 burgers.
- Fixed a bug which made it possible to skip a level by pressing the "next
  level" button twice.
- Reworked Japan world to add more variety in customers.
- Back to 12 levels per world.
- Fixed text overflowing out of screen in the "new item screen" when using the
  game in French.
- Reworked the background of achievement notifications.

# 0.13 - 2014-04-06

- New achievements have been added:
    - 3 "All stars" achievements: finish all levels of world 1, 2, 3 with three
      stars.
    - 3 "Perfect" achievements: get a perfect in all levels of world 1, 2, 3.
- A new ingredient has been added: mustard.
- All visible texts are now translatable, and translated in French.
- Added a gameover sound.
- Pirate world: Added a boss.
- Japan world: Created world-specific buttons for the sandbox mode, instead of
  reusing the Pirate world buttons.

# 0.12 - 2014-03-15

- An achievement system has been added.
- Mini-games have been removed.
- Start screen has been rearranged to make room for the achievement button:
  about and audio buttons are now grouped in a configuration button.

# 0.11 - 2014-02-13

- A cut-scene has been added when the player completes world 1 and world 2
- An ad is now displayed when you click on the Start button. The ad is
  game-oriented. It currently starts to appear after 8 plays, and no more than
  one every 12 minutes. Those values may change in the future: my goal with
  introducing ads early is to be able to adjust their frequency.
- Logging support: to help with debugging, Burger Party now logs information on
  the sdcard, in the "burgerparty" folder. This is why it now requests
  authorization to write to the sdcard.
- Burger Party can now be installed on the sdcard.

# 0.10 - 2014-01-23

- Fix broken "Next" button on level finished screen when user made a "perfect".

# 0.9 - 2014-01-22

- New way to gain stars: depending on his satisfaction, a customer will pay you
  between 1 and 3 coins. When you have enough coins you get a star. Stars cost
  more and more as you progress through levels.
- If all customers of a level are satisfied then you get a "perfect" ribbon in
  addition to the 3 stars.
- In advanced levels, burger size varies more from one customer to another.
- Big burgers may now have a middle bun.
- It is now possible to mute music and sound effects.
- Japan world:
    - Replaced place-holder "new item" cook with a real version.
- Air Burger:
    - Nicer level design
    - Collisions are now more visible
    - The "bad cheese" has been replaced with spikes
    - A bug which caused the ground to become temporarily translucent has been
      fixed
- Burger Vaders:
    - A new enemy type has been added: a burger which looses one ingredient each
      time you hit it
    - Fire sound has changed, a sound is now emitted when an enemy is hit
    - Extra guns now have a time limit, and will go away unless you hit another
      present box
- Burger Crush:
    - Destroying 3 items now gives a one-second bonus
    - Sound has been added when items are destroyed
- Overlay screens: Fixed blurry borders (pause, game over, level finished).
- Loading screen: Make sure Green Yeti Lab logo stretches as expected.

# 0.8 - 2014-01-12

- Mini-games! Each world features a mini game, which can be unlocked with stars
- The sandbox mode is now locked until the player has collected some stars, to
  avoid confusion when the game is started for the first time
- The background of the new item screen is now animated
- Added an about screen
- Star count is now visible in world list screen
- US world:
    - The counter color has changed, making the whole screen more colorful
- Japan world:
    - The ninja customer has been redrawn to use the same drawing style as the
      other customers
    - New customer: Japanese girl in traditional costume
- Low level changes:
    - libgdx has been updated to 0.9.9
    - Images are more compact, the game should use less space now

# 0.7 - 2013-12-09

- New world: Japan. Still a work in progress
- New burger item: fish
- Adjusted game levels: each world has 9 levels now
- Customers are now stacked behind each others like a real queue
- Some meal items are now world-specific versions of other meal items, for
  example the coconut-drink is the pirate version of the soda, the rice-toast
  is the Japanese version of the toast
- Updated music
- Started to balance the game levels. Still some work to do
- Do not forget levels which have been unlocked when the game is restarted
- Fix gradients in bubble not adjusting to the bubble width
- World and level selection screens: show disabled elements in gray-scale

# 0.6 - 2013-11-11

- When customers order a large burger, they now wait longer before getting angry
- The animation announcing a new items is not played again when retrying the
  level
- Fries and soda are now available from the beginning
- The "meal done" sound has been changed
- Texts are now more readable, thanks to a thin border around the letters
- US world:
    - Boss for last level is now a wrestler
    - New city-like background
- A few changes on the screen to select world or sandbox mode:
    - Number of unlocked stars and total stars are now shown on world buttons
    - One can no longer select worlds until they have been unlocked
    - A thin separator has been added between the world buttons and the sandbox
      button
- Sandbox mode:
    - Just like in the world screen, one can no longer select worlds until they
      have been unlocked
    - The "done" button now uses the "meal done" sound

# 0.5 - 2013-10-27

- Reworked burger scrolling: target burgers scroll in their bubble now instead
  of scrolling the whole view
- Sandbox mode: use proper icons and world-specific buttons
- Hopefully fixed black screen on resume
- Added (early version of) music in menu screens
- Reworked menu screens: only one play button, then player picks either a world
  or the sand box mode

# 0.4 - 2013-10-04

- Added 'create your own burger' mode
- Added tick sound when reaching end of timeout
- Scroll up burgers when reaching the top of the screen
- Visual improvements

# 0.3 - 2013-09-14

- Flatter appearance for world 1 customers
- Meal now may or may not include drinks or fries
- Pirate customers for all levels of world 2
- Improved head-up-display, does not overlap with order bubble anymore
- Added cut-scenes when unlocking meal items
- Fixed bug causing burger arrow to sometimes disappear
- Fixed bug which allowed adding multiple copies of the same drink or fries
