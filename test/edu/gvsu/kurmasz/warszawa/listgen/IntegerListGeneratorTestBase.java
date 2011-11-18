/**
 * Copyright (c) Zachary Kurmas 2007
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package edu.gvsu.kurmasz.warszawa.listgen;

import edu.gvsu.kurmasz.warszawa.util.RangeTests;
import org.junit.Assert;

/**
 * @author Zachary Kurmas
 */
// Created  11/11/11 at 12:34 PM
// (C) Zachary Kurmas 2011

public class IntegerListGeneratorTestBase {

   ///////////////////////////////////////////////////////////////////
   //
   // helper methods
   //
   /////////////////////////////////////////////////////////////////

   /**
    * verify that the values in {@code array} are in ascending order
    *
    * @param array the array to check.
    */
   public static void assertInOrder(long[] array) {
      for (int x = 1; x < array.length; x++) {
         Assert.assertTrue("Out of order at: " + x,
               array[x - 1] < array[x]);
      }
   }

   /**
    * find the next occurrence of {@code value} at or after index {@code start}
    *
    * @param value the value to find
    * @param start the first index to examine
    * @param array the array to search
    * @return the next index at which {@code value} occurs.
    */
   public static int findUp(long value, int start, long[] array) {
      while (start < array.length) {
         if (array[start] == value) {
            return start;
         }
         start++;
      }
      Assert.fail(value + "  not found in array!");
      return -1;
   }

   /**
    * Find the previous occurrence of {@code value} at or before index {@code start}
    *
    * @param value the value to find.
    * @param start the first index to examine
    * @param array the array to search.
    * @return the previous index at which {@code value} occurs.
    */
   public static int findDown(long value, int start, long[] array) {
      while (start >= 0) {
         if (array[start] == value) {
            return start;
         }
         start--;
      }
      Assert.fail(value + "  not found in array!");
      return -1;
   }

   /////////////////////////////////////////////////////////////////
   //
   // Test conversion methods
   //
   /////////////////////////////////////////////////////////////////

   public static void testConversions(IntegerListGenerator gen,
                                      long start, long stop, long step,
                                      long[] expected) {

      // Now make sure the other methods behave as expected.
      int[] ianswer = gen.generateIntArray(RangeTests.toInt(start),
            RangeTests.toInt(stop),
            RangeTests.toInt(step));

      java.util.List<Long> llanswer =
            gen.generateLongList(start, stop, step);

      java.util.List<Integer> ilanswer =
            gen.generateIntList(RangeTests.toInt(start),
                  RangeTests.toInt(stop),
                  RangeTests.toInt(step));


      for (int x = 0; x < expected.length; x++) {
         Assert.assertEquals(RangeTests.toInt(expected[x]),
               ianswer[x]);
         Assert.assertEquals(expected[x], llanswer.get(x).longValue());
         Assert.assertEquals(RangeTests.toInt(expected[x]),
               ilanswer.get(x).intValue());
      }
   }

}
