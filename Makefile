# Shell to use, stop on errors, stop on undefined variables, report errors
# if a command in a pipe fails (not just the last)
SHELL := bash
.SHELLFLAGS := -euo pipefail -c

# Do not start a new shell for each command of a target
# Makes it possible to have `cd foo` on its own line. Be sure to configure the
# shell to stop on errors though (the -e in .SHELLFLAGS)
.ONESHELL:

MAKEFLAGS += --warn-undefined-variables
MAKEFLAGS += --no-builtin-rules

DESKTOP_JAR=$(CURDIR)/burgerparty-desktop/build/libs/burgerparty-desktop-1.0.jar
ASSETS_DIR=$(CURDIR)/burgerparty-android/assets
GRADLEW=./gradlew
ifdef OFFLINE
	GRADLEW=./gradlew --offline
endif

all: desktop apk

apk: compile-po
	./gradlew burgerparty-android:assembleRelease

compile-po:
	scripts/po-compile-all

desktop: compile-po
	${GRADLEW} burgerparty-desktop:dist

run: desktop
	cd burgerparty-android/assets && java -jar ${DESKTOP_JAR}

check:
	scripts/runtests

packer:
	cd burgerparty-desktop && java -cp ${DESKTOP_JAR} com.agateau.burgerparty.PackerMain
	sleep 1
	touch ${ASSETS_DIR}/*.png ${ASSETS_DIR}/burgerparty.atlas
