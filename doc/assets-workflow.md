# Burger Party assets workflow

This document describes the (convoluted) process to produce final assets for the game.

## Final place

The game loads all assets from the `android/assets` directory.

Inside this directory, most images are stored in "atlas" PNGs called `burgerpartyNN.png`. The game find the images through the `burgerparty.atlas` file.

The only exception is the `loading.png` image used for the splash screen.

## In-game SVG to PNG files

SVG files are in `core/assets` and its sub-directories. They are manually exported from Inkscape to PNG files in `core/assets` and its sub-directories.

## Fonts

Fonts are stored in `core/assets/fonts`. This directory contains the .ttf files and .hiero files for each font style used by the game. .hiero files are edited using [Hiero][].

[Hiero]: https://libgdx.com/wiki/tools/hiero

.hiero files are work files. The `fonts` Makefile target turns .hiero files into png containing the glyphs and a .fnt file. The PNG files are generated in `core/assets` (they can't be generated somewhere else, because this is where libGDX Skin system looks for them in the atlas). The .fnt files are generated in `android/assets/ui`.

For the `fonts` target to work, the `HIERO_JAR` environment variable must contain the path to Hiero jar file.

At the moment, you must use a version of Hiero with support for relative font paths. You can find one here: <https://github.com/agateau/libgdx/releases/tag/hiero-20230518>.

## individual PNG to atlas PNGs

The `packer` target takes all PNGs from `core/assets` and its sub-directories and generates atlas PNGs in `android/assets` as well as the `burgerparty.atlas` file.

## Sounds & musics

Sounds and musics are stored in their final form in `android/assets/sounds` and in `android/assets/music`.
