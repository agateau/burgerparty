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

- [ ] Merge in master

    ```
    gh pr create --fill
    gh pr merge -dm --auto
    ```

- [ ] Generate .apk (after the merge to fix #24)

    Check android/signing.gradle exists

    ```
    make dist
    ```

- [ ] Smoke test
    - on PC
    - on phone
    - on tablet

- [ ] Tag:

    ```
    git checkout master
    git pull
    make tagpush
    ```

# Upload .apk

- [ ] Upload on Google Play

    Check fastlane/google-play-api.json exists

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

# Post release

- [ ] Update release check list
