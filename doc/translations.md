# Translations

## Testing a translation

You can run the game using the language you want using `make run LANG=<the_lang_to_test>`.

## Fixing missing characters

Characters for the texts are kept as PNG: the game does not directly use the font files. When adding a new language or modifying an existing translation, some characters may be missing, even if the font contain them. To fix this, run `make fonts` then `make packer`.
