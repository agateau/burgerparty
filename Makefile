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

ANDROID_GP_RUN_DIST_NAME=$(EXECUTABLE)-$(VERSION)-gp

ARCHIVE_DIR=$(CURDIR)/archives

ANDROID_PACKAGE_NAME=$(GAME_CP)

FLAVORS=agc gp amz

HIERO_JAR ?= ""
HIERO_CMD = java -jar "$(HIERO_JAR)"

HIERO_DIR = core/assets/fonts
FONT_PNG_DIR = core/assets
FONT_FNT_DIR = android/assets/ui


HIERO_FILES := $(wildcard $(HIERO_DIR)/*.hiero)
FONT_PNGS := $(subst $(HIERO_DIR), $(FONT_PNG_DIR), $(patsubst %.hiero, %.png, $(HIERO_FILES)))

# Update VERSION variable for snapshots
ifdef SNAPSHOT
	BRANCH:=$(shell git rev-parse --abbrev-ref HEAD | sed s,/,-,g)
	VERSION:=$(VERSION)+$(BRANCH)-$(shell git show --no-patch --format="%cd-%h" --date=format:%Y%m%dT%H%M%S)
endif

all: build

clean:
	rm -rf $(TOOLS_JAR) $(DESKTOP_JAR) android/build/outputs

build: $(DESKTOP_JAR)

$(DESKTOP_JAR): compile-po
	$(GRADLEW) desktop:dist

apk: compile-po
	$(GRADLEW) android:assembleRelease

run: build
	cd android/assets && java -Duser.language=$$LANG -jar $(DESKTOP_JAR)

$(TOOLS_JAR):
	$(GRADLEW) tools:dist

# Assets
packer: $(TOOLS_JAR)
	cd desktop && java -cp $(TOOLS_JAR) $(GAME_CP).PackerMain
	sleep 1
	touch $(ASSETS_DIR)/*.png $(ASSETS_DIR)/burgerparty.atlas

# Fonts
clean-fonts:
	rm -f $(FONT_PNGS)

fonts: update-hiero-glyph-text $(FONT_PNGS)

update-hiero-glyph-text:
	list-po-chars po/*.po --ascii | scripts/update-hiero-glyph-text core/assets/fonts/*.hiero

$(FONT_PNG_DIR)/%.png: $(HIERO_DIR)/%.hiero $(HIERO_DIR)/sanitechtro/Sanitechtro.ttf
	@echo "$< -> $@"
	@if [ -z "$(HIERO_JAR)" ] ; then
		@echo "Error: can't find the hiero tool."
		@echo ""
		@echo "Please set the path to hiero.jar in the HIERO_JAR environment variable."
		@echo "Use a version with support for relative font paths. You can find one here:"
		@echo "https://github.com/agateau/libgdx/releases/tag/hiero-20230518"
		exit 1
	fi
	@name_without_ext=$(patsubst %.png,%,$@)
	$(HIERO_CMD) --input $< --output $$name_without_ext --batch
	mv $$name_without_ext.fnt $(FONT_FNT_DIR)


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
	@for flavor in $(FLAVORS) ; do \
		cp android/build/outputs/apk/$$flavor/release/android-$$flavor-release.apk $(ARCHIVE_DIR)/$(EXECUTABLE)-$(VERSION)-$$flavor.apk ; \
	done

dist: check desktop-archives apk-archives

clean-dist: clean dist

desktop-dist: check desktop-archives

clean-desktop-dist: clean desktop-dist

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

gh-upload:
	gh release create --draft ${VERSION} \
		$(ARCHIVE_DIR)/$(EXECUTABLE)-$(VERSION).jar \
		$(ARCHIVE_DIR)/$(EXECUTABLE)-$(VERSION)-agc.apk
