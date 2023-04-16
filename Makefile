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

EXECUTABLE=burgerparty
# TODO Centralize version numbers
VERSION=1.3.0

ARCHIVE_DIR=$(CURDIR)/archives

all: build

build: $(DESKTOP_JAR)

$(DESKTOP_JAR): compile-po
	${GRADLEW} burgerparty-desktop:dist

apk: compile-po
	./gradlew burgerparty-android:assembleRelease

run: build
	cd burgerparty-android/assets && java -jar $(DESKTOP_JAR)

# Assets
packer:
	cd burgerparty-desktop && java -cp $(DESKTOP_JAR) com.agateau.burgerparty.PackerMain
	sleep 1
	touch $(ASSETS_DIR)/*.png $(ASSETS_DIR)/burgerparty.atlas

# Dist
desktop-archives: build
	@echo Moving desktop jar
	@mkdir -p $(ARCHIVE_DIR)
	mv -v $(DESKTOP_JAR) $(ARCHIVE_DIR)/$(EXECUTABLE)-$(VERSION).jar

apk-archives: apk
	@echo Moving apk files
	@mkdir -p $(ARCHIVE_DIR)
	@for store in amz gp ; do \
		mv burgerparty-android/build/outputs/apk/$$store/release/burgerparty-android-$$store-release.apk $(ARCHIVE_DIR)/$(EXECUTABLE)-$$store-$(VERSION).apk ; \
	done

dist: check desktop-archives apk-archives

# Tests
check: build
	scripts/runtests

# Translations
compile-po:
	scripts/po-compile-all
