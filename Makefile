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

# Override this with `make run LANG=some_other_lang` to test translations
LANG ?= en

DESKTOP_JAR=$(CURDIR)/desktop/build/libs/desktop-1.0.jar
TOOLS_JAR=$(CURDIR)/tools/build/libs/tools-1.0.jar
ASSETS_DIR=$(CURDIR)/android/assets
GRADLEW=./gradlew
ifdef OFFLINE
	GRADLEW=./gradlew --offline
endif

GAME_CP=com.agateau.burgerparty
EXECUTABLE=burgerparty

include version.properties

ANDROID_GP_RUN_DIST_NAME=$(EXECUTABLE)-gp-$(VERSION)

ARCHIVE_DIR=$(CURDIR)/archives

ANDROID_PACKAGE_NAME=$(GAME_CP)

FLAVORS=agc gp amz

all: build

clean:
	rm -rf $(TOOLS_JAR) $(DESKTOP_JAR) android/build/outputs

build: $(DESKTOP_JAR)

$(DESKTOP_JAR): compile-po
	${GRADLEW} desktop:dist

apk: compile-po
	./gradlew android:assembleRelease

run: build
	cd android/assets && java -Duser.language=$$LANG -jar $(DESKTOP_JAR)

$(TOOLS_JAR):
	$(GRADLEW) tools:dist

# Assets
packer: $(TOOLS_JAR)
	cd desktop && java -cp $(TOOLS_JAR) $(GAME_CP).PackerMain
	sleep 1
	touch $(ASSETS_DIR)/*.png $(ASSETS_DIR)/burgerparty.atlas

customer-editor: $(TOOLS_JAR)
	cd $(ASSETS_DIR) && java -cp $(TOOLS_JAR) $(GAME_CP).tools.CustomerEditorMain $(ASSETS_DIR)/customerparts.xml

# Dist
desktop-archives: build
	@echo Copying desktop jar
	@mkdir -p $(ARCHIVE_DIR)
	cp -v $(DESKTOP_JAR) $(ARCHIVE_DIR)/$(EXECUTABLE)-$(VERSION).jar

apk-archives: apk
	@echo Copying apk files
	@mkdir -p $(ARCHIVE_DIR)
	@for store in $(FLAVORS) ; do \
		cp android/build/outputs/apk/$$store/release/android-$$store-release.apk $(ARCHIVE_DIR)/$(EXECUTABLE)-$$store-$(VERSION).apk ; \
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

# Tag
tag:
	git tag -f -m "Burger Party $(VERSION)" $(VERSION)

tagpush: tag
	git push
	git push --tags

# Uploading
fastlane-beta:
	fastlane supply --track beta --apk $(ARCHIVE_DIR)/$(ANDROID_GP_RUN_DIST_NAME).apk
