#!/bin/sh -e

cd $(dirname $0)/..

EXTRA_DIR=extra
RES_DIR=android/res

NAME=ic_launcher.png

while read size dir ; do
    echo
    echo "# $size => $dir"
    echo
    size_args="--export-width $size --export-height $size"
    inkscape --export-area 0:0:512:512 $size_args --export-png /tmp/ic_launcher-raw.png $EXTRA_DIR/launcher.svg
    inkscape --export-id mask $size_args --export-png /tmp/mask.png $EXTRA_DIR/launcher.svg
    mkdir -p $dir
    convert /tmp/ic_launcher-raw.png /tmp/mask.png -compose DstIn -composite $dir/$NAME
done <<EOF
 36 $RES_DIR/drawable-ldpi
 48 $RES_DIR/drawable-mdpi
 72 $RES_DIR/drawable-hdpi
 96 $RES_DIR/drawable-xhdpi
144 $RES_DIR/drawable-xxhdpi
192 $RES_DIR/drawable-xxxhdpi
512 $EXTRA_DIR
EOF
