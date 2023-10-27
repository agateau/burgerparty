# Create .apk

- [ ] Clone source tree

    ```
    cd ~/tmp
    git clone git@github.com:agateau/burgerparty --reference ~/src/burgerparty
    cd burgerparty
    git submodule update --init
    git checkout -b prep-release
    ```

- [ ] Bump version numbers:

    ```
    vi version.properties
    ```

- [ ] Check translations are up to date
    - Run `pot-generate`
    - Translate new strings

- [ ] Run unit tests

    ```
    make check
    ```

- [ ] Generate .apk

    Check android/signing.gradle exists

    ```
    make dist
    ```

- [ ] Smoke test
    - on PC
    - on phone
    - on tablet

- [ ] Update changelogs

    ```
    vi CHANGELOG.md
    vi fastlane/metadata/android/en-US/changelogs/${versionCode}.txt
    ```

- [ ] Commit changes

- [ ] Push changes

    ```
    git push -u origin prep-release
    ```

- [ ] Check CI is happy

- [ ] Merge in master

    ```
    git checkout master
    git pull
    git merge --ff-only dev
    ```

- [ ] Tag:

    ```
    make tagpush
    ```

# Upload .apk

- [ ] Upload on Google Play

    ```
    make fastlane-beta
    ```

- [ ] Upload on agateau.com

# Prepare spread

- [ ] Take screenshots

## agateau.com

- [ ] Update project page

- [ ] Write announcement blog post

- [ ] Publish

## GitHub

- [ ] Upload on GitHub

    ```
    make gh-upload
    ```

## Slidedb

- [ ] Post news

## Google Play

- [ ] Update Google Play

- [ ] Publish

## Amazon

- [ ] Update Amazon Appstore

- [ ] Publish

# Spread

- [ ] Announce on Mastodon

- [ ] Announce on Twitter
