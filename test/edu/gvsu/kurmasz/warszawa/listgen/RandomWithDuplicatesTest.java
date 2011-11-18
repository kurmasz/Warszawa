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

/**

 * Test for {@code RandomWithDuplicates}
 *
 * @author Zachary Kurmas
 *
 *         (C) 2007 Grand Valley State University
 *
 */

public class RandomWithDuplicatesTest {
   protected Random r = new Random();
   protected RandomWithDuplicates r1, r2;

   @Before
   public void setup() {
      int seed = r.nextInt();

      // Give both the same seed so they'll produce the same
      // sequence of numbers.
      r1 = new RandomWithDuplicates(new Random(seed));
      r2 = new RandomWithDuplicates(new Random(seed));
   }

   public int[] testGenerateIntArrayHelper(int min, int max,
                                           int amount) {
      int[] answer = r1.generateIntArray(min, max, amount);

      Assert.assertEquals(amount, answer.length);

      for (long value : answer) {
         Assert.assertTrue(value >= min && value <= max);
      }

      long[] answer2 = r2.generateLongArray(min, max, amount);
      Assert.assertEquals(answer.length, answer2.length);
      for (int x = 0; x < answer.length; x++) {
         Assert.assertEquals(answer[x], (int) answer2[x]);
      }
      return answer;
   }

   public void verifyExceptionThrown(int min, int max, int amount) {
      try {
         r1.generateIntArray(min, max, amount);
         Assert.fail("Should have thrown exception");
      } catch (IllegalArgumentException e) {
         ;
      }
   }

   public void testExceptions1() {
      // Invalid amount
      verifyExceptionThrown(0, 0, 0);
      verifyExceptionThrown(10, 20, 0);
      verifyExceptionThrown(10, 20, -5);

      // max > min
      verifyExceptionThrown(20, 10, 1);

      // Range too large
      verifyExceptionThrown(0, Integer.MAX_VALUE, 100);
      verifyExceptionThrown(Integer.MIN_VALUE, -1, 100);
      verifyExceptionThrown(Integer.MIN_VALUE / 2, Integer.MAX_VALUE / 2, 100);
   }

   @Test
   public void testGenerateIntArrray() {
      testExceptions1();

      try {
         testGenerateIntArrayHelper(0, 0, 10);
         testGenerateIntArrayHelper(1, 1, 10);
         testGenerateIntArrayHelper(-5, -5, 10);

         testGenerateIntArrayHelper(1, 10, 10);
         testGenerateIntArrayHelper(-10, 10, 10);
         testGenerateIntArrayHelper(-10, -5, 10);


         testGenerateIntArrayHelper(1, 10, 10);
         testGenerateIntArrayHelper(-10, 10, 10);
         testGenerateIntArrayHelper(-10, -5, 10);

         // beta extremes
         testGenerateIntArrayHelper(1, Integer.MAX_VALUE, 100);
         testGenerateIntArrayHelper(0, Integer.MAX_VALUE - 1, 100);
         testGenerateIntArrayHelper(-1, Integer.MAX_VALUE - 2, 100);
         testGenerateIntArrayHelper(-10, Integer.MAX_VALUE - 11, 100);

         testGenerateIntArrayHelper(Integer.MIN_VALUE / 2,
               Integer.MAX_VALUE / 2 - 1, 100);

         testGenerateIntArrayHelper(Integer.MIN_VALUE, -2, 100);
         testGenerateIntArrayHelper(Integer.MIN_VALUE + 2, 0, 100);


      } catch (IllegalArgumentException e) {
         Assert.fail("No exception should have been thrown.  " + e);
      }
   }
}


	

	

