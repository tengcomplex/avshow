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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Config {
  static public final int DISPLAY_NUMBER = Integer.getInteger("org.dschroeer.display_number", 0).intValue();
  static public final int DISPLAY_WIDTH = Integer.getInteger("org.dschroeer.display_width", -1).intValue();
  static public final int DISPLAY_HEIGHT = Integer.getInteger("org.dschroeer.display_height", -1).intValue();
  static public final int DISPLAY_POSITION_X = Integer.getInteger("org.dschroeer.display_position_x", 0).intValue();
  static public final int DISPLAY_POSITION_Y = Integer.getInteger("org.dschroeer.display_position_y", 0).intValue();
  static public final int CACHE_SIZE = Integer.getInteger("org.dschroeer.cache_size", 5).intValue();
  static public final AvTaskProducerMode TASK_PRODUCER_MODE = AvTaskProducerMode.valueOf(System.getProperty("org.dschroeer.task_producer_mode", AvTaskProducerMode.DEFAULT.toString()));
  static public final String AUDIO_TYPES = System.getProperty("org.dschroeer.audio_types", "\\( -iname *.flac -o -iname *.mp3 -o -iname *.ogg -o -iname *.ape \\)");
  static public final String AUDIO_FOLDER = System.getProperty("org.dschroeer.audio_folder", "/tmp");
  static public final String PICTURE_FOLDER = System.getProperty("org.dschroeer.picture_folder", "/tmp");
  static public final Set<String> SEARCH_EXCLUDE_WORDS = new HashSet<>(Arrays.asList(System.getProperty("org.dschroeer.search_exclude_words", AvTaskDefaultProducer.DEFAULT_SEARCH_EXCLUDE_WORDS).split(",")));
  static public final List<String> AUDIO_COMMAND = new ArrayList<>(Arrays.asList(System.getProperty("org.dschroeer.audio_command", "cvlc,--play-and-exit").split(",")));
}