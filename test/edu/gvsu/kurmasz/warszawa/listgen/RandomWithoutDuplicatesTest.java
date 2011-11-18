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

import org.junit.*;

import java.util.Random;
import java.util.HashMap;

/**
 *
 * Test for {@code RandomWithOutDuplicates}
 *
 * @author Zachary Kurmas
 *
 *         (C) 2007 Grand Valley State University
 *
 */

public class RandomWithoutDuplicatesTest extends RandomWithDuplicatesTest {

   @Before
   public void setup() {
      int seed = r.nextInt();

      // Give both the same seed so they'll produce the same
      // sequence of numbers.
      r1 = new RandomWithoutDuplicates(new Random(seed));
      r2 = new RandomWithoutDuplicates(new Random(seed));
   }


   public int[] testGenerateIntArrayHelper(int min, int max,
                                           int amount) {

      //	System.out.printf("Testing %d %d %d\n",
      //		   min, max, amount);
      int[] answer = super.testGenerateIntArrayHelper(min, max, amount);
      final Integer ONE = new Integer(1);

      // Make sure all values are unique
      HashMap<Integer, Integer> hash = new HashMap<Integer, Integer>();
      for (int value : answer) {
         Assert.assertFalse(value + " appears more than once",
               hash.containsKey(value));
         hash.put(value, ONE);
      }
      return answer;
   }

   @Test
   public void testGenerateIntArrray() {
      int factor = ((RandomWithoutDuplicates) r1).factor;
      testExceptions1();

      // Not enough unique values
      verifyExceptionThrown(1, 3, 5);
      verifyExceptionThrown(-3, -1, 5);

      // These should use the permutation method
      testGenerateIntArrayHelper(1, 10, 10);
      testGenerateIntArrayHelper(-5, 5, 10);
      testGenerateIntArrayHelper(-5, 5, 11);
      testGenerateIntArrayHelper(-50, -40, 10);
      testGenerateIntArrayHelper(1, 10 * factor, 10);

      // These should use the "ignore" method
      testGenerateIntArrayHelper(1, 10 * factor + 5, 10);
      int upperBound = 1000000;
      int amount = 1000;
      if (amount * factor >= upperBound) {
         upperBound = amount * 2 * factor;
      }
      testGenerateIntArrayHelper(0, upperBound, 1000);
      testGenerateIntArrayHelper(-upperBound, upperBound, 1000);

      // beta extremes
      testGenerateIntArrayHelper(1, Integer.MAX_VALUE, 100);
      testGenerateIntArrayHelper(0, Integer.MAX_VALUE - 1, 100);
      testGenerateIntArrayHelper(-1, Integer.MAX_VALUE - 2, 100);
      testGenerateIntArrayHelper(-10, Integer.MAX_VALUE - 11, 100);

      testGenerateIntArrayHelper(Integer.MIN_VALUE / 2,
            Integer.MAX_VALUE / 2 - 1, 100);

      testGenerateIntArrayHelper(Integer.MIN_VALUE, -2, 100);
      testGenerateIntArrayHelper(Integer.MIN_VALUE + 2, 0, 100);
   }


   public void verifyAssert(long a, long b, long c) {
      try {
         r1.generateLongArray(a, b, c);
         Assert.fail(String.format("Parameters %d, %d, %d should have " +
               "caused an exception.", a, b, c));
      } catch (IllegalArgumentException e) {
      }
   }

   @Test
   public void verifyAssert() {
      // min <= max
      verifyAssert(10, 1, 1);
      verifyAssert(10, 1, 2);
      verifyAssert(-10, -20, 1);

      // Number must be >= 1
      verifyAssert(100, 200, 0);
      verifyAssert(100, 200, -1);

      // values must be ints.
      verifyAssert(Integer.MAX_VALUE + 1L, 2L * Integer.MAX_VALUE, 100);
      verifyAssert(Integer.MAX_VALUE / 2, Integer.MAX_VALUE + 1, 100);
      verifyAssert(-100, Integer.MAX_VALUE - 1000, Integer.MAX_VALUE + 1);

      // Range must be integer
      verifyAssert(0, Integer.MAX_VALUE, 100);
      verifyAssert(Integer.MIN_VALUE, -1, 100);
      verifyAssert(Integer.MIN_VALUE / 2, Integer.MAX_VALUE / 2, 100);

      // Range must be big enough
      verifyAssert(10, 20, 30);
   } // end verifyAssert_helper
}


