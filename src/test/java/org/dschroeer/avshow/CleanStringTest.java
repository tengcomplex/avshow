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

import static org.junit.Assert.assertEquals;

import org.dschroeer.avshow.AvProducer;
import org.junit.Test;

public class CleanStringTest {

  @Test
  public void test() {
    AvProducer avProducer = new AvProducer(null);
    String[] dirtyElementsRemoved = {"test()", "©test", "test\"", "test()()©"};
    String[] dirtyElementsReplaced = {"test;", "test-", "test.", "test,", "test "};

    for(String s : dirtyElementsRemoved)
      assertEquals("test", avProducer.clean(s));
    for(String s : dirtyElementsReplaced)
      assertEquals("test_", avProducer.clean(s));

  }

}
