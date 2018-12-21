![Banner](extra/google-play-feature-graphic.png)

# Burger Party

Burger Party is a time management game for Android where you play a fast-food
owner who must put together the burgers ordered by her customers before time
runs out.

Are you fast enough to keep all your customers happy, unlock all worlds, all
ingredients and all the achievements?

![Screenshot](extra/screenshot.png)

## Where do I get it?

You can find binaries on the [game page][gp].

[gp]: http://agateau.com/projects/burgerparty

## What about other platforms?

Burger Party is primarily designed for mobile. It has only been tested on
Android, but since it has been built using [libgdx][] it should be doable to
make it work on iOS and on the web. Pull requests are welcome!

[libgdx]: https://libgdx.badlogicgames.com

You can also build and run it on Linux, macOS and Windows. It's just not much
fun unless you have a touch screen (or you are really fast and precise with
your mouse!)

## License

- All the code is licensed under GPL 3.0 or later, except for the more reusable
  code of the [com.agateau.burgerparty.utils][utils] package, which is licensed
  under Apache 2.0.
- Licenses for assets are detailed in [doc/assets.md](doc/assets.md).

The rational behind this is to:

- allow reuse of all the code and assets by free software projects.
- allow reuse of utility code in proprietary projects.
- prevent ad-based, proprietary clones of the game.

Put another way, if you are a game developer and find some of the code
interesting, feel free to use it to build your *own* original project. If you
are interested in some of the GPL code, get in touch: I am open to relicensing.

On the other hand, if your plan is to take the game, slap some ads on it, and
release it without releasing the sources of your changes: the license forbids
you to do so, go find another prey.

[utils]: burgerparty/src/com/agateau/burgerparty/utils
