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
import org.junit.*;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 * Test for {@code Range}
 *
 * @author Zachary Kurmas
 *
 */
// (C) 2007 Zachary Kurmas

@RunWith(PowerMockRunner.class)
@PrepareForTest({IntegerListGenerator.class, RangeTests.class})
@PowerMockIgnore("jdk.internal.reflect.*")
public class CornerCasesTest extends IntegerListGeneratorTest {
   protected CornerCases ecc = new CornerCases();

   public void test(long start, long stop, long step, long[] expected) {
      long[] observed = ecc.generateLongArray(start, stop, step);
      //System.out.println("Observed: " + Arrays.toString(observed));
      Assert.assertArrayEquals(expected, observed);
   }

   @Test
   public void test_generateLongArray() {
      //
      // "Normal usage"
      //
      long[] expected = {4, 5, 7, 8, 9, 15};
      test(4, 15, 1, expected);


      long[] expectedb = {5, 7, 8, 9, 15};
      test(5, 15, 1, expectedb);

      long[] expectedc = {6, 7, 8, 9, 15};
      test(6, 15, 1, expectedc);

      long[] expectedd = {4, 5, 7, 8, 9, 14};
      test(4, 14, 1, expectedd);

      long[] expectede = {4, 5, 7, 8, 9, 13};
      test(4, 13, 1, expectede);

      long[] expectedf = {4, 5, 7, 8, 9, 15, 16};
      test(4, 16, 1, expectedf);

      long[] expectedg = {4, 5, 7, 8, 9, 15, 16, 17};
      test(4, 17, 1, expectedg);

      long[] expectedh = {4, 5, 7, 8, 9, 15, 16, 17, 18};
      test(4, 18, 1, expectedh);

      long[] expectedi = {4, 5, 7, 8, 9, 15, 16, 17, 19};
      test(4, 19, 1, expectedi);

      //
      // Check groups with overlap
      //

      // 0 and 1
      long[] expected2 = {1};
      test(1, 1, 1, expected2);

      // 1 and 2
      long[] expected2b = {2, 3};
      test(2, 3, 1, expected2b);

      // 0 through 2
      long[] expected2c = {1, 2, 3};
      test(1, 3, 1, expected2c);


      //
      // Steps of more than 1
      //
      long[] expected3 = {4, 5, 15};
      test(4, 15, 2, expected3);

      long[] expected3x = {5, 15};
      test(5, 15, 2, expected3x);

      long[] expected3y = {6, 15};
      test(6, 15, 2, expected3y);

      long[] expected3z = {6, 15, 16, 17, 19};
      test(6, 19, 2, expected3z);

      long[] expected3w = {3, 7, 8, 9, 19};
      test(3, 19, 2, expected3w);


      long[] expected3b = {4, 5, 14};
      test(4, 14, 2, expected3b);

      long[] expected3c = {4, 5, 13};
      test(4, 13, 2, expected3c);

      long[] expected4 = {4, 5, 15, 16, 17, 31};
      test(4, 31, 2, expected4);

      long[] expected4b = {4, 5, 15, 16, 17, 32};
      test(4, 32, 2, expected4b);

      long[] expected4c = {4, 5, 15, 16, 17, 33};
      test(4, 33, 2, expected4c);

      //
      // negative start/stop points
      //

      // -1, 0
      long[] expected5 = {-2, -1, 0};
      test(-2, 0, 1, expected5);

      long[] expected5b = {-2, -1};
      test(-2, -1, 1, expected5b);

      long[] expected5c = {-2, -1, 0, 1};
      test(-2, 1, 1, expected5c);

      long[] expected5d = {-2, -1, 0, 1, 2};
      test(-2, 2, 1, expected5d);

      long[] expected5e = {-2, -1, 0, 1, 2, 3};
      test(-2, 3, 1, expected5e);

      long[] expected5f = {-1, 0, 1, 2, 3};
      test(-1, 3, 1, expected5f);

      long[] expected5g = {-1, 0, 1, 2};
      test(-1, 2, 1, expected5g);

      long[] expected5h = {-1, 0, 1};
      test(-1, 1, 1, expected5h);

      long[] expected5i = {-3, -2, -1, 0};
      test(-3, 0, 1, expected5i);

      long[] expected5j = {-4, -3, -2, -1, 0};
      test(-4, 0, 1, expected5j);

      long[] expected5k = {-5, -4, -3, -2, -1, 0};
      test(-5, 0, 1, expected5k);


      // -1, 1
      long[] expected6 = {-2, -1, 0, 1};
      test(-2, 1, 1, expected6);

      // -2, 2
      long[] expected7 = {-4, -3, -2, -1, 0, 1, 2, 3};
      test(-4, 3, 1, expected7);

      long[] expected7b = {-5, -4, -3, -2, -1, 0, 1, 2, 3, 4};
      test(-5, 4, 1, expected7b);

      long[] expected7c = {-6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4};
      test(-6, 4, 1, expected7c);

      long[] expected7d = {-7, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4};
      test(-7, 4, 1, expected7d);

      long[] expected7e = {-7, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 7};
      test(-7, 7, 1, expected7e);


      // -2, 2 step 2
      long[] expected8 = {-4, -3, 0, 1, 2, 3};
      test(-4, 3, 2, expected8);


      //
      // More steps
      //

      // -5, 5 step 1
      long[] expected9 = {-32, -31, -17, -16, -15, -9, -8, -7, -5,
            -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 7, 8,
            9, 15, 16, 17, 31};
      test(-32, 31, 1, expected9);

      long[] expected9w = {-35, -33, -32, -31, -17, -16, -15, -9, -8, -7, -5,
            -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 7, 8,
            9, 15, 16, 17, 31, 32, 33, 35};
      test(-35, 35, 1, expected9w);


      // -5, 5 step 2
      long[] expected9b = {-32, -31, -9, -8, -7, -3, -2, -1, 1, 2, 3,
            7, 8, 9, 31};
      test(-32, 31, 2, expected9b);

      long[] expected9b2 = {-32, -31, -9, -8, -7, -3, -2, -1, 1, 2, 3,
            7, 8, 9, 31, 32, 33, 35};
      test(-32, 35, 2, expected9b2);


      // -5, 5 step 3
      long[] expected9c = {-32, -31, -5,
            -4, -3, 1, 2, 3, 15, 16, 17, 31};
      test(-32, 31, 3, expected9c);

      long[] expected9c2 = {-32, -31, -5,
            -4, -3, 1, 2, 3, 15, 16, 17, 35};
      test(-32, 35, 3, expected9c2);

      long[] expected9c3 = {-35, -33, -32, -31, -5,
            -4, -3, 1, 2, 3, 15, 16, 17, 35};
      test(-35, 35, 3, expected9c3);

      // -5, 5 step 4
      long[] expected9d = {-32, -31, -3, -2, -1, 7, 8,
            9, 31};
      test(-32, 31, 4, expected9d);


      //Big numbers
      long[] expected10 = {2147483648L, 2147483649L, 4294967295L,
            4294967296L,
            4294967297L, 8589934591L};
      test(2147483648L, 8589934591L, 1L, expected10);

      long[] expected10b = {2147483648L, 2147483649L, 4294967295L,
            4294967296L,
            4294967297L, 8589934591L,
            8589934592L};
      test(2147483648L, 8589934592L, 1L, expected10b);


      long[] expected10c = {2147483648L, 2147483649L, 4294967295L,
            4294967296L,
            4294967297L, 8589934591L,
            8589934592L, 8589934593L};
      test(2147483648L, 8589934593L, 1L, expected10c);


      long[] expected10d = {2147483648L, 2147483649L, 4294967295L,
            4294967296L,
            4294967297L, 8589934591L,
            8589934592L, 8589934593L, 8589934596L};
      test(2147483648L, 8589934596L, 1L, expected10d);


      long[] expected11 = {1152921504606846976L, 1152921504606846977L,
            2305843009213693951L, 2305843009213693952L,
            2305843009213693953L, 4611686018427387903L,
            4611686018427387904L, 4611686018427387905L,
            9223372036854775807L};
      test(1152921504606846976L, 9223372036854775807L, 1L, expected11);


      long[] expected11b = {1152921504606846976L, 1152921504606846977L,
            2305843009213693951L, 2305843009213693952L,
            2305843009213693953L, 4611686018427387903L,
            4611686018427387904L, 4611686018427387905L,
            9223372036854775801L};
      test(1152921504606846976L, 9223372036854775801L, 1L, expected11b);


      long[] expected12 = {-8589934592L, -8589934591L, -4294967297L,
            -4294967296L, -4294967295L, -2147483649L};
      test(-8589934592L, -2147483649L, 1L, expected12);


      long[] expected12b = {-8589934591L, -4294967297L,
            -4294967296L, -4294967295L, -2147483649L};
      test(-8589934591L, -2147483649L, 1L, expected12b);

      long[] expected12c = {-8589934581L, -4294967297L,
            -4294967296L, -4294967295L, -2147483649L};
      test(-8589934581L, -2147483649L, 1L, expected12c);


      long[] expected13 = {-9223372036854775808L, -9223372036854775807L,
            -4611686018427387905L, -4611686018427387904L,
            -4611686018427387903L, -2305843009213693953L};
      test(-9223372036854775808L, -2305843009213693953L, 1, expected13);

      long[] expected13b = {-9223372036854775807L,
            -4611686018427387905L, -4611686018427387904L,
            -4611686018427387903L, -2305843009213693953L};
      test(-9223372036854775807L, -2305843009213693953L, 1, expected13b);


      long[] expected13c = {-9223372036854775707L,
            -4611686018427387905L, -4611686018427387904L,
            -4611686018427387903L, -2305843009213693953L};
      test(-9223372036854775707L, -2305843009213693953L, 1, expected13c);

   }


   public void verifyAssert_helper(long a, long b, long c) {
      try {
         ecc.generateLongArray(a, b, c);
         Assert.fail(String.format("Parameters %d, %d, %d should have " +
               "caused an exception.", a, b, c));
      } catch (IllegalArgumentException e) {
      }
   }

   @Test
   public void verifyAssert() {
      verifyAssert_helper(10, 1, 1);
      verifyAssert_helper(10, 1, 2);
      verifyAssert_helper(-10, -20, 1);
      verifyAssert_helper(10, 1, 0);
      verifyAssert_helper(10, 1, -1);
   }
}



