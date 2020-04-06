# Synopsis
Avshow is a simple application for an infinite slideshow of pictures accompanied by audio.
The basic idea is to show a random picture for the time a matching audio file is played.

A pool of pictures and audio files must be accessible.

The default algorithm `RANDOM_PICTURE_MATCHING_AUDIO_TRACK` works as following:
* 1) Pick a random picture
* 2) Extract text tokens from
    * a) filename
    * b) meta data
* 3) Grep an audio file with the pattern of extracted picture tokens
    * If there are no matches, take a random audio file from audio folder
    * If there are multiple matches, take a random audio file from the matches

Alternatively it's possible to configure an external service for obtaining audio and picture tasks by mode `SERVICE_LOCAL`.
If configured, such a service can pass multiple pictures for one audio track.
Depending on the length of the audio track the given pictures are shown for the according amount of time.
Example, an external service might return:

```
{ "picturePath":["/tmp/pics/pic1.jpg", "/tmp/pics/pic2.jpg"], "audioPath":"/tmp/audio/song.flac", "audioDurationInSeconds":"180" }
```

We have 2 pictures, each is shown for 180 / 2 = 90 seconds during the play of `song.flac`.

Avshow is written in Java and is due to its dependencies designed to run on linux.

# License
GPL v3 Copyright (C) 2019-2020  David Schröer <tengcomplexATgmail.com>

# Dependencies
Must be installed:
* Java JRE 8 or higher
* [find](https://www.gnu.org/software/findutils/)
* [grep](https://www.gnu.org/software/grep/)
* [shuf](https://www.gnu.org/software/coreutils/)
* [ls](https://www.gnu.org/software/coreutils/)
* A media player capable of playing audio files, e.g. vlc or mplayer.

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
org.dschroeer.display_number               display number, default 0
org.dschroeer.display_width                width in pixel, default max
org.dschroeer.display_height               height in pixel, default max
org.dschroeer.display_position_x           position horizontally, default 0
org.dschroeer.display_position_y           position vertically, default 0
org.dschroeer.audio_types                  search pattern for types of audio files, 
                                           default ( -iname *.flac -o -iname *.mp3 -o -iname *.ogg -o -iname *.ape )
org.dschroeer.audio_folder                 path of audio files, default /tmp
org.dschroeer.picture_folder               path of picture files, default /tmp
org.dschroeer.search_exclude_words         comma separated list of words,
                                           default the,and,from,wallpaper,wallpapers,theme,large,picture,img,dsc,
                                           der,die,das,des,und,oder,fuer,für,ist,auf,vor,bei,ihr,ihre,sein,seine
org.dschroeer.audio_command                comma separated audio command tokens, default cvlc,--play-and-exit
org.dschroeer.task_producer_mode           possible values RANDOM_PICTURE_MATCHING_AUDIO_TRACK, SERVICE_LOCAL, default RANDOM_PICTURE_MATCHING_AUDIO_TRACK
org.dschroeer.local_producer_command       external service for obtaining task data, used in task_producer_mode SERVICE_LOCAL
org.dschroeer.image_change_mode            possible values, SIMPLE_CUT, FADE, default SIMPLE_CUT
org.dschroeer.image_fade_running_time      when in mode FADE, runtime of a picture fade in milliseconds, default 2500
org.dschroeer.image_fade_repaint_interval  when in mode FADE, repaint interval in milliseconds, default, 20
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
-Dcom.dschroeer.search_exclude_words="the,and,from,wallpaper,wallpapers,theme,large,picture" \
-Dorg.dschroeer.audio_command="cvlc,--play-and-exit" \
-jar avshow-$version.jar
```

## Keyboard commands
```
n                                      next picture
v                                      toggle on-screen display
q, esc                                 quit program
```
