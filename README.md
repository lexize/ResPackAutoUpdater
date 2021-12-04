# ResPackAutoUpdater

## How this works?
Mod checking version from json file from link, then check version from .zip, and if version from json file greater that version in .zip, mod replace resource pack to new pack from link located in json.

## Can author download exploit in your pc with this mod?
Literally no. But she/he can put zip bomb into pack... And this is doesn't under my control, sorry. So, download packs only from places you trust :)

#For Authors
## Configuring *pack.mcmeta*
To make resource pack updatable, you need to add some fields to **"pack.mcmeta"**

* `check_url` - Main field. Link to json file with update data.
* `version` - Field, used to check, do you have the latest version of pack.

`check_url` file also requires some field:
* `download_url` - Link to get new resource pack .zip.
* `version` - This field used to comparison with `pack.mcmeta` **version**, to check, does user have last version of pack
###Example of *pack.mcmeta* and *check_url* file
`pack.mcmeta`:
```json
{
  "pack": {
    "pack_format": 7,
    "description": "Example description"
  },
  "check_url": "https://example.com/check_url_file.json",
  "version": 0.1
}
```
`"check_url" file`:
```json
{
  "download_url": "https://example.com/example_pack.zip",
  "version": 0.2
}
```
Don't forget to update *version* in `pack.mcmeta` of new pack, to avoid endless update on each game start.