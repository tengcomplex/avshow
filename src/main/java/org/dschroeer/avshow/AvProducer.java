/*
 *  Avshow is a slideshow program.
 *  Copyright (C) 2019  David Schröer <tengcomplexATgmail.com>
 *
 *
 *  This file is part of Avshow.
 *
 *  Avshow is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Avshow is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Avshow.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.dschroeer.avshow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.dschroeer.avshow.AvTaskCache.AvTask;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class AvProducer implements Runnable {
  private static final Logger L = Logger.getLogger(AvProducer.class.getName());

  private static final String SEARCH_EXCLUDE_PATTERN_PICTURE_DIMENSION = "\\d{3,4}x\\d{3,4}";
  public static final String DEFAULT_SEARCH_EXCLUDE_WORDS = ""
    + "the,and,from,wallpaper,wallpapers,theme,large,picture,img,dsc,"
    + "der,die,das,des,und,oder,fuer,für,ist,auf,vor,bei,ihr,ihre,sein,seine";

  private AvTaskCache cache;
  private AvConsumer consumer;

  public AvProducer(AvTaskCache cache) {
    this.cache = cache;
  }

  public void setConsumer(AvConsumer consumer) {
    this.consumer = consumer;
  }

  @Override
  public void run() {
    while (true) {
      try {
        if (cache.isFull()) {
          synchronized (this) {
            wait();
          }
        }
        String picturePath = getRandomPicturePath();
        cache.add(new AvTask(picturePath, getAudioPath(picturePath)));
      } catch (Exception e) {
        e.printStackTrace();
        cache.add(new AvTask("", "Error: " + e.getMessage()));
      } finally {
        synchronized (consumer) {
          consumer.notify();
        }
      }
    }
  }

  private String getRandomPicturePath() throws Exception {
    return getFirstLine(new String[] { "/bin/sh", "-c", "ls " + Config.PICTURE_FOLDER + "/*.* | shuf -n1" });
  }

  private Map<String, String> getMetaTagMap(File file)
      throws ImageProcessingException, FileNotFoundException, IOException {
    Map<String, String> ret = new HashMap<>();
    try (FileInputStream fis = new FileInputStream(file)) {
      Metadata metadata = ImageMetadataReader.readMetadata(fis);
      for (Directory directory : metadata.getDirectories()) {
        Collection<Tag> tags = directory.getTags();
        L.info(file.getName() + " tags: " + Arrays.toString(tags.toArray(new Tag[tags.size()])));
        for (Tag tag : tags) {
          ret.put("[" + tag.getDirectoryName() + "] " + tag.getTagName(), tag.getDescription());
        }
      }
    }
    catch (Exception e) {
      throw e;
    }
    return ret;
  }

  private void addIfExists(Map<String, String> tagMap, String tagName, List<String> list) {
    String value = tagMap.get(tagName);
    if (value != null) {
      L.info("found " + tagName + "->" + value);
      list.addAll(Arrays.asList(clean(value).split("_")));
    }
  }

  /**
   * Remove ()"©
   * Replace -;\\.:,\\|  with _
   * @param dirty
   * @return
   */
  String clean(String dirty) {
    return dirty.replaceAll("[\\(\\)\"©]", "")
        .replaceAll("[-;\\.:,\\| ]", "_")
        .toLowerCase();
  }

  private Predicate<String> isEligibleForSearch() {
    return p -> p.length() > 2 && !Config.SEARCH_EXCLUDE_WORDS.contains(p) && !p.matches(SEARCH_EXCLUDE_PATTERN_PICTURE_DIMENSION);
  }

  private String getAudioPath(String picturePath) throws Exception {
    L.info("Getting audio path for " + picturePath);
    File file = new File(picturePath);
    Map<String, String> tagMap = getMetaTagMap(file);
    List<String> metaValues = new ArrayList<>();
    addIfExists(tagMap, "[IPTC] Caption/Abstract", metaValues);
    addIfExists(tagMap, "[IPTC] Keywords", metaValues);
    String cleanFilename = clean(FilenameUtils.removeExtension(file.getName()));
    String pattern = Stream
      .concat(Arrays.stream(cleanFilename.split("_")), metaValues.stream())
      .distinct()
      .filter(isEligibleForSearch())
      .collect(Collectors.joining("|"));
    L.info(file.getName() + ", pattern: " + pattern);

    String line;
    String[] cmd = { "/bin/sh", "-c",
        "find " + Config.AUDIO_FOLDER + "/ -type f " + Config.AUDIO_TYPES + " -regextype posix-egrep -iregex \".*(" + pattern + ").*\" | shuf -n1" };
    line = getFirstLine(cmd);

    if (line == null) {
      L.info("no audio track found, pick random");
      cmd = new String[] { "/bin/sh", "-c", "find " + Config.AUDIO_FOLDER + "/ -type f " + Config.AUDIO_TYPES + " | shuf -n1" };
      line = getFirstLine(cmd);
    }
    return line;
  }

  static String getFirstLine(String[] cmd) throws IOException, InterruptedException {
    Process p = Runtime.getRuntime().exec(cmd);
    p.waitFor();
    try (BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
      String line;
      if ((line = in.readLine()) != null) {
        return line;
      }
    }
    return null;
  }

}
