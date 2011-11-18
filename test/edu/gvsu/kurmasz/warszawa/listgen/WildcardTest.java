package edu.gvsu.kurmasz.warszawa.listgen;

import org.junit.*;

/**
 *
 * Test for {@code Wildcard}
 *
 * @author Zachary Kurmas
 *
 *         (C) 2007 Grand Valley State University
 */

public class WildcardTest {
   protected Wildcard w = new Wildcard();

   public void testGetWildcardMask_helper(long value, long[] expected) {
      long[] observed = w.getWildcardMasks(value);
      //System.out.println("Observed: " + Arrays.toString(observed));
      Assert.assertArrayEquals(expected, observed);
   }

   @Test
   public void testGetWildcardMask() {
      long[] exp0 = {0};
      testGetWildcardMask_helper(0, exp0);

      long[] exp1 = {0, 1};
      testGetWildcardMask_helper(1, exp1);

      long[] exp2 = {0, 2};
      testGetWildcardMask_helper(2, exp2);

      long[] exp3 = {0, 1, 2, 3};
      testGetWildcardMask_helper(3, exp3);

      long[] exp4 = {0, 4};
      testGetWildcardMask_helper(4, exp4);

      long[] exp5 = {0, 1, 4, 5};
      testGetWildcardMask_helper(5, exp5);

      long[] exp6 = {0, 2, 4, 6};
      testGetWildcardMask_helper(6, exp6);

      long[] exp10 = {0, 2, 8, 10};
      testGetWildcardMask_helper(10, exp10);

      long[] exp11 = {0, 1, 2, 3, 8, 9, 10, 11};
      testGetWildcardMask_helper(11, exp11);

      long[] exp12 = {0, 4, 8, 12};
      testGetWildcardMask_helper(12, exp12);


   }

   public void test_generateLongArray_helper(long base, long wildcard,
                                             long[] expected) {
      //System.out.printf("Testing %d %d\n", base, wildcard);

      long[] observed = w.generateLongArray(base, wildcard, 0);
      //System.out.println("Observed: " + Arrays.toString(observed));
      Assert.assertArrayEquals(expected, observed);
   }

   @Test
   public void testGenerateLongArray() {
      // no bit changes.
      long exp0[] = {0};
      test_generateLongArray_helper(0, 0, exp0);

      long exp0b[] = {1};
      test_generateLongArray_helper(1, 0, exp0b);

      long exp0c[] = {2};
      test_generateLongArray_helper(2, 0, exp0c);

      long exp0d[] = {3};
      test_generateLongArray_helper(3, 0, exp0d);


      // low-order bit changes only
      long exp1[] = {0, 1};
      test_generateLongArray_helper(0, 1, exp1);
      test_generateLongArray_helper(1, 1, exp1);

      long exp1b[] = {-2, -1};
      test_generateLongArray_helper(-1, 1, exp1b);
      test_generateLongArray_helper(-2, 1, exp1b);

      long exp2[] = {2, 3};
      test_generateLongArray_helper(2, 1, exp2);
      test_generateLongArray_helper(3, 1, exp2);

      long exp3[] = {8, 9};
      test_generateLongArray_helper(8, 1, exp3);
      test_generateLongArray_helper(9, 1, exp3);

      long exp4[] = {6, 7};
      test_generateLongArray_helper(6, 1, exp4);
      test_generateLongArray_helper(7, 1, exp4);


      // two lowest order bits change
      long exp5[] = {0, 1, 2, 3};
      test_generateLongArray_helper(0, 3, exp5);
      test_generateLongArray_helper(1, 3, exp5);
      test_generateLongArray_helper(2, 3, exp5);
      test_generateLongArray_helper(3, 3, exp5);

      long exp5b[] = {-4, -3, -2, -1};
      test_generateLongArray_helper(-1, 3, exp5b);
      test_generateLongArray_helper(-2, 3, exp5b);
      test_generateLongArray_helper(-3, 3, exp5b);
      test_generateLongArray_helper(-4, 3, exp5b);

      long exp6[] = {4, 5, 6, 7};
      test_generateLongArray_helper(4, 3, exp6);
      test_generateLongArray_helper(5, 3, exp6);
      test_generateLongArray_helper(6, 3, exp6);
      test_generateLongArray_helper(7, 3, exp6);

      // 2nd lowest order bit changes
      long exp7[] = {0, 2};
      test_generateLongArray_helper(0, 2, exp7);
      test_generateLongArray_helper(2, 2, exp7);

      long exp8[] = {1, 3};
      test_generateLongArray_helper(1, 2, exp8);
      test_generateLongArray_helper(3, 2, exp8);

      long exp9[] = {4, 6};
      test_generateLongArray_helper(4, 2, exp9);
      test_generateLongArray_helper(6, 2, exp9);

      long exp10[] = {5, 7};
      test_generateLongArray_helper(5, 2, exp10);
      test_generateLongArray_helper(7, 2, exp10);


      // bits 1 and 2
      long exp11[] = {0, 1, 4, 5};
      test_generateLongArray_helper(0, 5, exp11);
      test_generateLongArray_helper(1, 5, exp11);
      test_generateLongArray_helper(4, 5, exp11);
      test_generateLongArray_helper(5, 5, exp11);

      long exp12[] = {2, 3, 6, 7};
      test_generateLongArray_helper(2, 5, exp12);
      test_generateLongArray_helper(3, 5, exp12);
      test_generateLongArray_helper(6, 5, exp12);
      test_generateLongArray_helper(7, 5, exp12);

      long exp13[] = {8, 9, 12, 13};
      test_generateLongArray_helper(8, 5, exp13);
      test_generateLongArray_helper(9, 5, exp13);
      test_generateLongArray_helper(12, 5, exp13);
      test_generateLongArray_helper(13, 5, exp13);

      long exp14[] = {10, 11, 14, 15};
      test_generateLongArray_helper(10, 5, exp14);
      test_generateLongArray_helper(11, 5, exp14);
      test_generateLongArray_helper(14, 5, exp14);
      test_generateLongArray_helper(15, 5, exp14);


      // bits 1 and 2
      long exp15[] = {0, 2, 4, 6};
      test_generateLongArray_helper(0, 6, exp15);
      test_generateLongArray_helper(2, 6, exp15);
      test_generateLongArray_helper(4, 6, exp15);
      test_generateLongArray_helper(6, 6, exp15);

      long exp16[] = {1, 3, 5, 7};
      test_generateLongArray_helper(1, 6, exp16);
      test_generateLongArray_helper(3, 6, exp16);
      test_generateLongArray_helper(5, 6, exp16);
      test_generateLongArray_helper(7, 6, exp16);

      long exp17[] = {8, 10, 12, 14};
      test_generateLongArray_helper(8, 6, exp17);
      test_generateLongArray_helper(10, 6, exp17);
      test_generateLongArray_helper(12, 6, exp17);
      test_generateLongArray_helper(14, 6, exp17);

      long exp18[] = {9, 11, 13, 15};
      test_generateLongArray_helper(9, 6, exp18);
      test_generateLongArray_helper(11, 6, exp18);
      test_generateLongArray_helper(13, 6, exp18);
      test_generateLongArray_helper(15, 6, exp18);


      // bits 0 through 5;
      long[] exp19 = new long[32];
      for (int x = 0; x < 32; x++) {
         exp19[x] = x;
      }
      for (int x = 0; x < 32; x++) {
         test_generateLongArray_helper(x, 31, exp19);
      }

      // all but bit 2
      long[] exp20 = new long[16];
      long[] exp21 = new long[16];
      int count1 = 0;
      int count2 = 0;
      for (int x = 0; x < 32; x++) {
         if ((x & 4) == 0) {
            exp20[count1++] = x;
         } else {
            exp21[count2++] = x;
         }
      } // end for
      for (int x = 0; x < 16; x++) {
         test_generateLongArray_helper(exp20[x], 27, exp20);
         test_generateLongArray_helper(exp21[x], 27, exp21);
      }

      long[] exp22 = {9, 11, 25, 27};
      test_generateLongArray_helper(9, 18, exp22);


      // Test extremes
      long mxv = Long.MAX_VALUE;

      long[] exp23 = {mxv};
      test_generateLongArray_helper(mxv, 0, exp23);

      long[] exp24 = {mxv - 1, mxv};
      test_generateLongArray_helper(mxv, 1, exp24);


      long mnv = Long.MIN_VALUE;

      long[] exp25 = {mnv};
      test_generateLongArray_helper(mnv, 0, exp25);

      long[] exp26 = {mnv, mnv + 1};
      test_generateLongArray_helper(mnv, 1, exp26);


      long[] exp27 = {0, mnv};
      test_generateLongArray_helper(0, mnv, exp27);

      long[] exp28 = {0, 1, mnv, mnv + 1};
      test_generateLongArray_helper(0, mnv + 1, exp28);

   }

   public void verifyException(long a, long b) {
      try {
         w.generateLongArray(a, b, 0);
         Assert.fail("Should throw an exception");
      } catch (IllegalArgumentException e) {
      }
   }

   @Test
   public void verifyException() {

      verifyException(0, -1);
      verifyException(5, -1);
      verifyException(-1, -1);
      verifyException(-1, -2);
      verifyException(-1, -8);
      verifyException(-1, -16);
      verifyException(Long.MAX_VALUE, -1);

      // 24 is the "Magic Number" used by Wildcard. If it changes,
      // this beta will break.
      int mrs_bits = 24;

      Assert.assertEquals("Constant in Wildcard changed.  Update beta",
            1 << mrs_bits, w.getMaxReturnSize());

      verifyException(0, (2 << mrs_bits) - 1);
      verifyException(1, (2 << mrs_bits) - 1);
      verifyException(7, (2 << mrs_bits) - 1);
      verifyException(-1, (2 << mrs_bits) - 1);

      w.setMaxReturnSize(1 << (mrs_bits - 1));
      verifyException(0, (1 << mrs_bits) - 1);
      verifyException(1, (1 << mrs_bits) - 1);
      verifyException(7, (1 << mrs_bits) - 1);
      verifyException(-1, (1 << mrs_bits) - 1);
   }


   public void testStringToPair(String str, long exp1, long exp2) {
      Wildcard.Pair<Long, Long> p;
      p = Wildcard.wildcardStringToLongPair(str);
      Assert.assertEquals("Error in base of " + str, exp1, (long) p.base);
      Assert.assertEquals("Error in wildcard of " + str, exp2, (long) p.wildcards);
   }

   public void testBadStringToPair(String str) {
      try {
         Wildcard.wildcardStringToLongPair(str);
         Assert.fail("String ->" + str + "<- should cause an error.");
      } catch (IllegalArgumentException e) {
         ; // good.
      }
   }


   @Test
   public void testStringToPair() {

      testStringToPair("0", 0, 0);
      testStringToPair("1", 1, 0);
      testStringToPair("10", 2, 0);
      testStringToPair("11", 3, 0);
      testStringToPair("100", 4, 0);
      testStringToPair("101", 5, 0);
      testStringToPair("110", 6, 0);
      testStringToPair("111", 7, 0);


      testStringToPair("X", 0, 1);
      testStringToPair("X0", 0, 2);
      testStringToPair("X1", 1, 2);
      testStringToPair("XX", 0, 3);
      testStringToPair("1XX", 4, 3);
      testStringToPair("10X", 4, 1);
      testStringToPair("11X", 6, 1);
      testStringToPair("1X0", 4, 2);
      testStringToPair("1X1", 5, 2);
      testStringToPair("1x1", 5, 2);
      testStringToPair("1*1", 5, 2);
      testStringToPair("1?1", 5, 2);

      testStringToPair("X0X", 0, 5);
      testStringToPair("X1X", 2, 5);
      testStringToPair("    X1X", 2, 5);
      testStringToPair("    X1X        ", 2, 5);

      testBadStringToPair(null);
      testBadStringToPair("");
      testBadStringToPair(" ");
      testBadStringToPair("1o100010");
      testBadStringToPair("g");
      testBadStringToPair("100 01098");
   }

   public void testPairToString(String answer, long base, long wildcards) {
      String observed = Wildcard.longPairToWildcardString(base, wildcards);
      Assert.assertEquals("Failed for " + base + " " + wildcards, answer, observed);
   }

   @Test
   public void testPairToString() {
      testStringToPair("0", 0, 0);
      testPairToString("1", 1, 0);
      testPairToString("10", 2, 0);
      testPairToString("11", 3, 0);
      testPairToString("100", 4, 0);
      testPairToString("101", 5, 0);
      testPairToString("110", 6, 0);
      testPairToString("111", 7, 0);

      testPairToString("*", 0, 1);
      testPairToString("*0", 0, 2);
      testPairToString("*1", 1, 2);
      testPairToString("**", 0, 3);
      testPairToString("1**", 4, 3);
      testPairToString("10*", 4, 1);
      testPairToString("11*", 6, 1);
      testPairToString("1*0", 4, 2);
      testPairToString("1*1", 5, 2);

      testPairToString("*0*", 0, 5);
      testPairToString("*1*", 2, 5);
   }
}

    

	