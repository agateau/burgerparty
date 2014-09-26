#!/bin/sh -e

cd $(dirname $0)/..

EXTRA_DIR=extra
RES_DIR=burgerparty-android/res

NAME=ic_launcher.png
SRC=$RES_DIR/drawable-hdpi/$NAME

convert $EXTRA_DIR/ic_launcher-raw.png $EXTRA_DIR/mask.png -compose DstOut -composite $SRC

convert -resize 48x48 $SRC $RES_DIR/drawable-mdpi/$NAME
convert -resize 36x36 $SRC $RES_DIR/drawable-ldpi/$NAME
