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
TOOLS_JAR=$(CURDIR)/tools/build/libs/tools-1.0.jar
ASSETS_DIR=$(CURDIR)/burgerparty-android/assets
GRADLEW=./gradlew
ifdef OFFLINE
	GRADLEW=./gradlew --offline
endif

GAME_CP=com.agateau.burgerparty
EXECUTABLE=burgerparty
# TODO Centralize version numbers
VERSION=1.3.0

ANDROID_GP_RUN_DIST_NAME=$(EXECUTABLE)-gp-$(VERSION)

ARCHIVE_DIR=$(CURDIR)/archives

ANDROID_PACKAGE_NAME=$(GAME_CP)

all: build

build: $(DESKTOP_JAR)

$(DESKTOP_JAR): compile-po
	${GRADLEW} burgerparty-desktop:dist

apk: compile-po
	./gradlew burgerparty-android:assembleRelease

run: build
	cd burgerparty-android/assets && java -jar $(DESKTOP_JAR)

$(TOOLS_JAR):
	$(GRADLEW) tools:dist

# Assets
packer: $(TOOLS_JAR)
	cd burgerparty-desktop && java -cp $(TOOLS_JAR) $(GAME_CP).PackerMain
	sleep 1
	touch $(ASSETS_DIR)/*.png $(ASSETS_DIR)/burgerparty.atlas

customer-editor: $(TOOLS_JAR)
	cd burgerparty-desktop && java -cp $(TOOLS_JAR) $(GAME_CP).tools.CustomerEditorMain $(ASSETS_DIR)/customerparts.xml

# Dist
desktop-archives: build
	@echo Copying desktop jar
	@mkdir -p $(ARCHIVE_DIR)
	cp -v $(DESKTOP_JAR) $(ARCHIVE_DIR)/$(EXECUTABLE)-$(VERSION).jar

apk-archives: apk
	@echo Copying apk files
	@mkdir -p $(ARCHIVE_DIR)
	@for store in amz gp ; do \
		cp burgerparty-android/build/outputs/apk/$$store/release/burgerparty-android-$$store-release.apk $(ARCHIVE_DIR)/$(EXECUTABLE)-$$store-$(VERSION).apk ; \
	done

dist: check desktop-archives apk-archives

# Tests
check: build
	$(GRADLEW) test

android-run-from-dist:
	# uninstall any existing version in case we have an unsigned version installed
	adb uninstall $(ANDROID_PACKAGE_NAME) || true
	adb install -f $(ARCHIVE_DIR)/$(ANDROID_GP_RUN_DIST_NAME).apk
	adb shell am start -n $(ANDROID_PACKAGE_NAME)/$(GAME_CP).MainActivity

# Translations
compile-po:
	scripts/po-compile-all
