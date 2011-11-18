package edu.gvsu.kurmasz.warszawa.listgen;

import org.junit.*;

import java.util.Scanner;

/**
 *
 * Test for {@code Range}
 *
 * @author Zachary Kurmas
 *
 *         (C) 2007 Grand Valley State University
 *
 */

public class ExponentialCornerCasesTest {
   protected ExponentialCornerCases ecc = new ExponentialCornerCases();

   public void test(long start, long stop, long step, long[] expected) {
      long[] observed = ecc.generateLongArray(start, stop, step);
      //System.out.println("Observed: " + Arrays.toString(observed));
      Assert.assertArrayEquals(expected, observed);
   }

   /*
   long[] generateEasyAnswer(long start, long stop)
   {
  Assert.true("Programmer error", stop > start + 1);
  Assert.true("Programmer error", (start >=2 || start <= -2));
  Assert.true("Programmer error", (stop >=2 || stop <= -2));

  int size = (stop - start + 1) * 3;

  long[] answer = new long[size];
  int count = 0;
  answer[count++] = 1L << start;
  answer[count++] = 1L << start + 1;
  for (x = start; x <= stop; x++) {
      answer[count++] = (1L << x) - 1;
      answer[count++] = (1L << x);
      answer[count++] = (1L << x) + 1;
  }
  answer[count++] = (1L << x) - 1;
  return answer;
   }
   */


   @Test
   public void test_generateLongArray() {
      //
      // "Normal usage"
      //
      long[] expected = {4, 5, 7, 8, 9, 15};
      test(2, 4, 1, expected);

      //
      // Check groups with overlap
      //

      // 0 and 1
      long[] expected2 = {1};
      test(0, 1, 1, expected2);

      // 1 and 2
      long[] expected2b = {2, 3};
      test(1, 2, 1, expected2b);

      // 0 through 2
      long[] expected2c = {1, 2, 3};
      test(0, 2, 1, expected2c);


      //
      // Steps of more than 1
      //
      long[] expected3 = {4, 5, 15};
      test(2, 4, 2, expected3);

      long[] expected4 = {4, 5, 15, 16, 17, 31};
      test(2, 5, 2, expected4);

      //
      // negative start/stop points
      //

      // -1, 0
      long[] expected5 = {-2, -1, 0};
      test(-1, 0, 1, expected5);

      // -1, 1
      long[] expected6 = {-2, -1, 0, 1};
      test(-1, 1, 1, expected6);

      // -2, 2
      long[] expected7 = {-4, -3, -2, -1, 0, 1, 2, 3};
      test(-2, 2, 1, expected7);

      // -2, 2 step 2
      long[] expected8 = {-4, -3, 0, 1, 2, 3};
      test(-2, 2, 2, expected8);


      //
      // More steps
      //

      // -5, 5 step 1
      long[] expected9 = {-32, -31, -17, -16, -15, -9, -8, -7, -5,
            -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 7, 8,
            9, 15, 16, 17, 31};
      test(-5, 5, 1, expected9);

      // -5, 5 step 2
      long[] expected9b = {-32, -31, -9, -8, -7, -3, -2, -1, 1, 2, 3,
            7, 8, 9, 31};
      test(-5, 5, 2, expected9b);


      // -5, 5 step 3
      long[] expected9c = {-32, -31, -5,
            -4, -3, 1, 2, 3, 15, 16, 17, 31};
      test(-5, 5, 3, expected9c);

      // -5, 5 step 4
      long[] expected9d = {-32, -31, -3, -2, -1, 7, 8,
            9, 31};
      test(-5, 5, 4, expected9d);


      long[] expected10 = {2147483648L, 2147483649L, 4294967295L,
            4294967296L,
            4294967297L, 8589934591L};
      test(31L, 33L, 1L, expected10);

      long[] expected11 = {1152921504606846976L, 1152921504606846977L,
            2305843009213693951L, 2305843009213693952L,
            2305843009213693953L, 4611686018427387903L,
            4611686018427387904L, 4611686018427387905L,
            9223372036854775807L};
      test(60L, 63L, 1L, expected11);


      long[] expected12 = {-8589934592L, -8589934591L, -4294967297L,
            -4294967296L, -4294967295L, -2147483649L};
      test(-33L, -31L, 1L, expected12);

      long[] expected13 = {-9223372036854775808L, -9223372036854775807L,
            -4611686018427387905L, -4611686018427387904L,
            -4611686018427387903L, -2305843009213693953L};
      test(-63L, -61L, 1, expected13);


      long[] expected14 = {32, 33, 255};
      test(5, 8, 4, expected14);

   }


   public void verifyAssert(long a, long b, long c) {
      try {
         ecc.generateLongArray(a, b, c);
         Assert.fail(String.format("Parameters %d, %d, %d should have " +
               "caused an exception.", a, b, c));
      } catch (IllegalArgumentException e) {
      }
   }

   @Test
   public void verifyAssert() {
      verifyAssert(10, 1, 1);
      verifyAssert(10, 1, 2);
      verifyAssert(-10, -20, 1);
      verifyAssert(10, 1, 0);
      verifyAssert(10, 1, -1);
      verifyAssert(10, 10, 1);
      verifyAssert(Long.SIZE, Long.SIZE + 5, 1);
      verifyAssert(-10, Long.SIZE, 1);
      verifyAssert(-Long.SIZE, 3, 1);
   }

   public static void main(String[] args) {
      IntegerListGenerator g = new ExponentialCornerCases();

      Scanner in = new Scanner(System.in);
      long a = in.nextLong();
      long b = in.nextLong();
      long c = in.nextLong();
      System.out.println("Begin:");
      for (long l : g.generateLongArray(a, b, c)) {
         System.out.printf("%d, ", l);
      }
      System.out.println();
   }
}
	
	
    
