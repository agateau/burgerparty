GRADLEW=./gradlew --offline
DESKTOP_JAR=$(CURDIR)/burgerparty-desktop/build/libs/burgerparty-desktop-1.0.jar
ASSETS_DIR=$(CURDIR)/burgerparty-android/assets

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
