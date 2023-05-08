# Create .apk

- Check source tree is clean

    git checkout master
    git pull
    git status

- Bump version numbers:

    vi version.properties

- Check translations are up to date
    - Run `pot-generate`
    - Translate new strings

- Run unit tests

- Generate .apk

    - Check android/signing.gradle exists
    - Run `make dist`

- Smoke test
    - on PC
    - on phone
    - on tablet

- Update changelogs

    vi CHANGELOG.md
    vi fastlane/metadata/android/en-US/changelogs/${versionCode}.txt

- Commit

- Tag:

    make tagpush

# Upload .apk

- Upload on Google Play

    make fastlane-beta

- Upload on agateau.com

# Prepare spread

- Take screenshots

## agateau.com

- Update project page

- Write announcement blog post

- Publish

## Slidedb

- Post news

## Google Play

- Update Google Play

- Publish

## Amazon

- Update Amazon Appstore

- Publish

# Spread

- Notify Google Group

- Announce on FB

- Announce on Twitter
