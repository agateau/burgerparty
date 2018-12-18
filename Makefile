all: apk

apk: compile-po
	./gradlew burgerparty-android:assembleRelease

compile-po:
	scripts/po-compile-all
