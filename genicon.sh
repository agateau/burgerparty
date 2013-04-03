cd $(dirname $0)

RES_DIR=burgerparty-android/res

NAME=ic_launcher.png
SRC=$RES_DIR/drawable-hdpi/$NAME

convert -resize 48x48 $SRC $RES_DIR/drawable-mdpi/$NAME
convert -resize 36x36 $SRC $RES_DIR/drawable-ldpi/$NAME
