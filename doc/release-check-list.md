# Create .apk

- Check current branch is master

- Check working tree is clean

- Bump version numbers:
    AndroidManifest.xml versionCode
    AndroidManifest.xml versionName
    Constants

- Check translations are up to date

    pot-generate
    # translate
    po-compile-all

- Run unit tests

- Generate .apk

- Smoke test
    - on PC
    - on phone
    - on tablet

- Update news.md

- Commit

- Tag:

    git tag -a $newv

- Push

    git push
    git push --tags

# Upload .apk

- Upload on Google Play

- Upload on greenyetilab.com

# Prepare spread

- Take screenshots

## Greenyetilab.com

- Update greenyetilab.com/burgerparty

- Publish

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

- Notify G+ community

- Announce on G+
    - GYL
    - Self
- Announce on FB
    - GYL
    - Self
- Announce on Twitter
    - GYL
    - Self
