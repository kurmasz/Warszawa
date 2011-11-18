package edu.gvsu.kurmasz.warszawa.util;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test for {@code RangeTests}
 *
 * @author Zachary Kurmas
 */
// (C) 2007 Grand Valley State University

public class RangeTestsTest {


   //
   // Long range tests
   //
   BigInteger minLongValue = new BigInteger(Long.MIN_VALUE + "");
   BigInteger maxLongValue = new BigInteger(Long.MAX_VALUE + "");
   BigInteger[] longs = {minLongValue, minLongValue.add(BigInteger.ONE),
         BigInteger.ONE.negate(),
         BigInteger.ZERO,
         BigInteger.ONE,
         maxLongValue.subtract(BigInteger.ONE),
         maxLongValue,
   };

   BigInteger[] notLong = {minLongValue.subtract(BigInteger.ONE),
         minLongValue.multiply(BigInteger.TEN),
         maxLongValue.add(BigInteger.ONE),
         maxLongValue.multiply(BigInteger.TEN)};

   @Test
   public void inLongRangeReturnsTrue() throws Throwable {
      for (BigInteger bi : longs) {
         Assert.assertTrue(bi + " is a long",
               RangeTests.inLongRange(bi));
      }
   }

   @Test
   public void inLongRangeReturnsFalse() throws Throwable {
      for (BigInteger bi : notLong) {
         Assert.assertFalse(bi + " is not a long",
               RangeTests.inLongRange(bi));
      }
   }

   @Test
   public void assertLongRangeDoesNotThrowExceptionWhenGivenLong() throws Throwable {
      for (BigInteger bi : longs) {
         RangeTests.assertLongRange(bi);
      }
   }

   private void assertLongRangeThrowsExceptionWhenNotLong(BigInteger bi) {
      try {
         RangeTests.assertLongRange(bi);
         fail(bi + " should throw an exception.");
      } catch (RangeTests.OutOfRangeException e) {
         // should end up here
      }
   }

   @Test
   public void assertLongRangeThrowsExceptoinWhenNotLong() throws Throwable {
      for (BigInteger bi : notLong) {
         assertLongRangeThrowsExceptionWhenNotLong(bi);
      }
   }

   @Test
   public void toLongReturnsLongIfInRange() throws Throwable {
      for (BigInteger bi : longs) {
         assertEquals(bi.longValue(), RangeTests.toLong(bi));
      }
   }

   @Test(expected = RangeTests.OutOfRangeException.class)
   public void toLongThrowsExceptionIfOutOfRange() throws Throwable {
      RangeTests.toLong(notLong[0]);
   }

   //
   // Integer range tests
   //

   long[] ints = {Integer.MIN_VALUE, Integer.MIN_VALUE + 1, -1, 0, 1,
         Integer.MAX_VALUE - 1, Integer.MAX_VALUE};
   long[] notInts = {Long.MIN_VALUE, Integer.MIN_VALUE - 1L,
         Integer.MAX_VALUE + 1L, Long.MAX_VALUE};

   @Test
   public void testIsIntegerRange_long_returnsTrue() {
      for (long x : ints) {
         Assert.assertTrue(x + " is an integer",
               RangeTests.inIntegerRange(x));
      }
   }

   @Test
   public void testIsIntegerRange_long_returnsFalse() throws Throwable {
      for (long x : notInts) {
         Assert.assertFalse(x + " is not an integer",
               RangeTests.inIntegerRange(x));
      }
   }

   @Test
   public void testIsIntegerRange_BigInt_returnsTrue() throws Throwable {
      for (long x : ints) {
         BigInteger bx = new BigInteger(x + "");
         Assert.assertTrue(x + " is an integer",
               RangeTests.inIntegerRange(bx));
      }
   }

   @Test
   public void testIsIntegerRange_BigInt_returnsFalse() throws Throwable {
      for (long x : notInts) {
         BigInteger bx = new BigInteger(x + "");
         Assert.assertFalse(x + " is not an integer",
               RangeTests.inIntegerRange(bx));
      }

      for (BigInteger bi : notLong) {
         Assert.assertFalse(bi + " is not an integer",
               RangeTests.inIntegerRange(bi));
      }
   }

   @Test
   public void assertIntegerRange_long_DoesNotThrowExceptionForInts() throws Throwable {
      for (long x : ints) {
         RangeTests.assertIntegerRange(x);
      }
   }

   private void assertIntegerInRange_long_throwsException(long x) {
      try {
         RangeTests.assertIntegerRange(x);
         fail("assertIntegerRange did not throw exception for " + x);
      } catch (RangeTests.OutOfRangeException e) {
         // we had better see this
      }
   }

   @Test
   public void testAssertIntegerInRange_long_throwsException() throws Throwable {
      for (long x : notInts) {
         assertIntegerInRange_long_throwsException(x);
      }
   }

   @Test
   public void assertIntegerRange_BigInteger_DoesNotThrowExceptionForInts() throws Throwable {
      for (long x : ints) {
         RangeTests.assertIntegerRange(new BigInteger(x + ""));
      }
   }

   private void assertIntegerInRange_BigInteger_throwsException(BigInteger bi) {
      try {
         RangeTests.assertIntegerRange(bi);
         fail("assertIntegerRange did not throw exception for " + bi);
      } catch (RangeTests.OutOfRangeException e) {
         // we had better see this
      }
   }

   @Test
   public void testAssertIntegerInRange_BigInteger_throwsException() throws Throwable {
      for (long x : notInts) {
         assertIntegerInRange_BigInteger_throwsException(new BigInteger(x + ""));
      }

      for (BigInteger bi : notLong) {
         assertIntegerInRange_BigInteger_throwsException(bi);
      }
   }

   @Test
   public void toInt_long_ReturnIntIfInRange() throws Throwable {
      for (long x : ints) {
         assertEquals((int) x, RangeTests.toInt(x));
      }
   }

   @Test(expected = RangeTests.OutOfRangeException.class)
   public void toInt_long_ThrowsExceptionIfOutOfRange() throws Throwable {
      RangeTests.toInt(notInts[0]);
   }

   @Test
   public void toInt_bi_ReturnIntIfInRange() throws Throwable {
      for (long x : ints) {
         BigInteger bi = new BigInteger(x + "");
         assertEquals(bi.intValue(), RangeTests.toInt(x));
      }
   }

   @Test(expected = RangeTests.OutOfRangeException.class)
   public void toInt_bi_ThrowsExceptionIfOutOfRange() throws Throwable {
      RangeTests.toInt(new BigInteger(notInts[0] + ""));
   }

   //
   //   difference methods
   //

   @Test
   public void testIsIntegerDifference_int() {
      int mv = Integer.MIN_VALUE;
      int xv = Integer.MAX_VALUE;
      //System.out.println(mv + " to " + xv);

      int[] vals = {mv, mv + 1, xv - 2, mv / 2 + 2, mv / 2 + 1, mv / 2,
            mv / 2 + 1, mv / 2 + 2, -10, -2, -1, 0, 1, 2, 10,
            xv / 2 - 2, xv / 2 - 1, xv / 2, xv / 2 + 1, xv / 2 + 2,
            xv - 2, xv - 1, xv};

      for (int a : vals) {
         for (int b : vals) {
            long la = (long) a;
            long lb = (long) b;
            boolean expected = ((lb - la >= (long) mv) &&
                  (lb - la <= (long) xv));

            boolean observed = RangeTests.isIntegerDifference(a, b);
            Assert.assertEquals("Testing " + a + ", " + b,
                  expected, observed);
         } // end inner for
      } // end outer for
   } // end testIsIntegerDifference_int

   @Test
   public void testIs_X_Difference_intAndlong() {
      long mv = Long.MIN_VALUE;
      long xv = Long.MAX_VALUE;
      //System.out.println(mv + " to " + xv);

      long[] vals = {mv, mv + 1, xv - 2, mv / 2 + 2, mv / 2 + 1, mv / 2,
            mv / 2 + 1, mv / 2 + 2, -10, -2, -1, 0, 1, 2, 10,
            xv / 2 - 2, xv / 2 - 1, xv / 2, xv / 2 + 1, xv / 2 + 2,
            xv - 2, xv - 1, xv};


      for (long a : vals) {
         for (long b : vals) {
            BigInteger ba = new BigInteger(a + "");
            BigInteger bb = new BigInteger(b + "");


            boolean expected = RangeTests.inIntegerRange(bb.subtract(ba));
            boolean observed = RangeTests.isIntegerDifference(a, b);
            Assert.assertEquals("Testing for int" + a + ", " + b,
                  expected, observed);


            boolean expected2 = RangeTests.inLongRange(bb.subtract(ba));
            boolean observed2 = RangeTests.isLongDifference(a, b);
            Assert.assertEquals("Testing for long " + a + ", " + b,
                  expected2, observed2);

         }
      } // outer for
   } // end testIsIntegerDifference_long
}
