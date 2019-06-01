# Synopsis
Avshow is a simple application for an infinite slideshow of pictures accompanied by audio.
The basic idea is to show a random picture for the time a matching audio file is played.

A pool of pictures and audio files must be accessible.

The algorithm works as following:
* 1) Pick a random picture
* 2) Extract text tokens from
    * a) filename
    * b) meta data
* 3) Grep an audio file with the pattern of extracted picture tokens
    * If there are no matches, take a random audio file from audio folder
    * If there are multiple matches take a random audio file from the matches

Avshow is written in Java and is due to its dependencies designed to run on linux.

# License
GPL v3 Copyright (C) 2019  David Schröer <tengcomplexATgmail.com>

# Dependencies
Must be installed:
* Java JRE 8 or higher
* [mplayer](http://www.mplayerhq.hu)
* [grep](https://www.gnu.org/software/grep/)
* [shuf](https://www.gnu.org/software/coreutils/)
* [ls](https://www.gnu.org/software/coreutils/)

Included:
* [Apache Commons IO](https://github.com/apache/commons-io)
* [Drew Noakes metadata-extractor](https://github.com/drewnoakes/metadata-extractor)

# Build
```
mvn clean package
```

# Usage
```
java [PROPERTIES] -jar avshow.jar
```

## Properties
```
org.dschroeer.display_number           display number, default 0
org.dschroeer.display_width            width in pixel, default max
org.dschroeer.display_height           height in pixel, default max
org.dschroeer.display_position_x       position horizontally, default 0
org.dschroeer.display_position_y       position vertically, default 0
org.dschroeer.audio_types              search pattern for types of audio files, 
                                       default ( -iname *.flac -o -iname *.mp3 -o -iname *.ogg -o -iname *.ape )
org.dschroeer.audio_folder             path of audio files, default /tmp
org.dschroeer.picture_folder           path of picture files, default /tmp
org.dschroeer.search_exclude_words     comma separated list of words,
                                       default the,and,from,wallpaper,wallpapers,theme,large,picture,img,dsc,
                                       der,die,das,des,und,oder,fuer,für,ist,auf,vor,bei,ihr,ihre,sein,seine
```

## Example
```
java \
-Dorg.dschroeer.display_number=0 \
-Dorg.dschroeer.display_width=800 \
-Dorg.dschroeer.display_height=600 \
-Dorg.dschroeer.display_position_x=30 \
-Dorg.dschroeer.display_position_y=20 \
-Dorg.dschroeer.audio_folder=/tmp/audio \
-Dorg.dschroeer.picture_folder=/tmp/pics \
-Dcom.drinschinz.search_exclude_words="the,and,from,wallpaper,wallpapers,theme,large,picture" \
-jar avshow-$version.jar
```

## Keyboard commands
```
n                                      next picture
v                                      toggle on-screen display
q, esc                                 quit program
```
