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
