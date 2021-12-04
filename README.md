# ResPackAutoUpdater

## How this works?
Mod checking version from json file from link, then check version from .zip, and if version from json file greater that version in .zip, mod replace resource pack to new pack from link located in json.

## Can author download exploit in your pc with this mod?
Literally no. But she/he can put zip bomb into pack... And this is doesn't under my control, sorry. So, download packs only from places you trust :)

#For Authors
## Pack configuration
####Does mod supports folder packs?
*No.*

####Configuring
To make resource pack updatable, you need to create `update_data.json` file in your pack.

Fields of `update_data.json`:
* `check_url` - Main field. Link to json file with update data.
* `version` - Field, used to check, do you have the latest version of pack.

Content of `update_data.json`:
```json
{
  "check_url": "https://example.com/check_url_file.json",
  "version": 0.1
}
```
Also, you need file to check latest version. You can store that on your repo, or something else.
Fields of `check_url` file:
* `download_url` - URL of .zip file that replace old version of pack;
* `version` - Field, used to check, do you have the latest version of pack.

`check_url` file content:
```json
{
  "download_url": "https://example.com/example_pack.zip",
  "version": 0.2
}
```
Don't forget to update *version* in `update_data.json` of new pack, to avoid endless update on each game start.