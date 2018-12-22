# Create .apk

- Check source tree is clean

    git checkout master
    git pull
    git status

- Bump version numbers:
    AndroidManifest.xml versionCode
    AndroidManifest.xml versionName
    Constants

- Check translations are up to date
    - Run `pot-generate`
    - Translate new strings

- Run unit tests

- Generate .apk

    - Check burgerparty-android/signing.gradle exists
    - Run `make`

- Smoke test
    - on PC
    - on phone
    - on tablet

- Update CHANGELOG.md

- Commit

- Tag:

    git tag -a $newv

- Push

    git push
    git push --tags

# Upload .apk

- Upload on Google Play

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
