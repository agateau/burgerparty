#!/bin/sh -e

cd $(dirname $0)/..

EXTRA_DIR=extra
RES_DIR=burgerparty-android/res

NAME=ic_launcher.png
SRC=$RES_DIR/drawable-hdpi/$NAME

while read size suffix ; do
    full_dir=$RES_DIR/drawable-$suffix
    echo
    echo "# $size => $full_dir"
    echo
    size_args="--export-width $size --export-height $size"
    inkscape --export-area 0:0:512:512 $size_args --export-png $EXTRA_DIR/ic_launcher-raw.png $EXTRA_DIR/launcher.svg
    inkscape --export-id mask $size_args --export-png $EXTRA_DIR/mask.png $EXTRA_DIR/launcher.svg
    mkdir -p $full_dir
    convert $EXTRA_DIR/ic_launcher-raw.png $EXTRA_DIR/mask.png -compose DstIn -composite $full_dir/$NAME
done <<EOF
36 ldpi
48 mdpi
72 hdpi
96 xhdpi
144 xxhdpi
192 xxxhdpi
EOF
