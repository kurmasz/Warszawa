package edu.gvsu.kurmasz.warszawa.util;

import java.math.BigInteger;

/**
 * Methods to determine whether the value of a "wide" data type falls within the
 * range of a "narrower" data type.
 */
public class RangeTests {

   /**
    * Thrown when the value of a wide data type doesn't fit in the narrower data type.
    */
   public static class OutOfRangeException extends RuntimeException {
      public OutOfRangeException(String value, String type) {
         super(value + " is not within the range of " + type);
      }
   }


   /**
    * return whether a {@code BigInteger} value is within the range of an
    * {@code Long}.
    *
    * @param num the value to check
    * @return {@code true} if the value is within the range of an {@code Long},
    *         {@code false} otherwise.
    */
   public static boolean inLongRange(BigInteger num) {
      return (num.compareTo(new BigInteger(Long.MIN_VALUE + "")) >= 0 && num
            .compareTo(new BigInteger(Long.MAX_VALUE + "")) <= 0);
   }

   /**
    * assert that a {@code BigInteger} value is within the range of an {@code
    * Long}. If not, throw an exception.
    *
    * @param value the value to check
    * @throws OutOfRangeException if {@code value} is not within range
    *                             of a long.
    */
   public static void assertLongRange(BigInteger value) {
      if (!inLongRange(value)) {
         throw new OutOfRangeException(value + "", "long");
      }
   }

   /**
    * "safely" narrows a {@code BigInteger} to a long(or throws an exception if
    * the value is out of range).
    *
    * @param value the value to convert
    * @return {@code value} as an long.
    * @throws OutOfRangeException if {@code value} is not within range
    *                             of a long.
    */
   public static long toLong(BigInteger value) {
      assertLongRange(value);
      return value.longValue();
   }

   /**
    * return whether a {@code long} value is within the range of an {@code
    * Integer}.
    *
    * @param value the value to check
    * @return {@code true} if the value is within the range of an {@code
    *         Integer}, {@code false} otherwise.
    */
   public static boolean inIntegerRange(long value) {
      return (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE);
   }

   /**
    * return whether a {@code BigInteger} value is within the range of an
    * {@code Integer}.
    *
    * @param value the value to check
    * @return {@code true} if the value is within the range of an {@code
    *         Integer}, {@code false} otherwise.
    */
   public static boolean inIntegerRange(BigInteger value) {
      return inLongRange(value) && inIntegerRange(toLong(value));
   }

   /**
    * assert that a {@code long} value is within the range of an {@code Integer}.
    * If not, throw an exception.
    *
    * @param value the value to check
    * @throws OutOfRangeException if {@code value} is not within range
    *                             of an integer.
    */
   public static void assertIntegerRange(long value) {
      if (!inIntegerRange(value)) {
         throw new OutOfRangeException(value + "", "long");
      }
   }

   /**
    * check that a {@code BigInteger} value is within the range of an {@code Integer}
    * . If not, throw an exception.
    *
    * @param value the value to check
    * @throws OutOfRangeException if {@code value} is not within range
    *                             of an integer.
    */
   public static void assertIntegerRange(BigInteger value) {
      if (!inIntegerRange(value)) {
         throw new OutOfRangeException(value + "", "long");
      }
   }

   /**
    * "safely" narrows a long to an integer (or throws an exception if the
    * value is out of range).
    *
    * @param value the value to convert
    * @return {@code value} as an integer.
    * @throws OutOfRangeException if {@code value} is not within range
    *                             of an integer.
    */
   public static int toInt(long value) {
      assertIntegerRange(value);
      return (int) value;
   }

    /**
    * "safely" narrows a {@code BigInteger} to an integer (or throws an exception if the
    * value is out of range).
    *
    * @param value the value to convert
    * @return {@code value} as an integer.
    * @throws OutOfRangeException if {@code value} is not within range
    *                             of an integer.
    */
   public static int toInt(BigInteger value) {
      assertIntegerRange(value);
      return value.intValue();
   }

   /**
    * returns {@code true} if {@code b - a} is an integer. (This is not a
    * given: Consider {@code 2^31 - -2^31}.)
    *
    * @param a one value
    * @param b the other value
    * @return {@code true} if {@code high - low} is an integer, {@code false}
    *         otherwise.
    */
   public static boolean isIntegerDifference(int a, int b) {
      // if (a <= b), then b - a will be positive. There can be an
      // overflow of b - a only if a < 0 and b >= 0. This code
      // relies on short-circuiting:
      // The computation a + Integer.MAX_VALUE is only safe if a < 0.
      if (a <= b) {
         return (a >= 0 || b < 0 || a + Integer.MAX_VALUE >= b);
      }

      // Because Integer.MIN_VALUE is not necessarily the negative of
      // Integer.MAX_VALUE, we must perform a similar, but distinct
      // set of computations if b < a.

      // if b < a, then (b - a) can overflow only if b < 0 and a > 0
      return (b >= 0 || a < 0 || a + Integer.MIN_VALUE <= b);
   }

   /**
    * returns {@code true} if {@code b - a} is a long integer. (This is not a
    * given: Consider {@code 2^63 - -2^63}.)
    *
    * @param a one value
    * @param b the other value
    * @return {@code true} if {@code high - low} is a long integer, {@code
    *         false} otherwise.
    */
   public static boolean isLongDifference(long a, long b) {
      // See comments in isIntegerDifference
      if (a <= b) {
         return (a >= 0 || b < 0 || a + Long.MAX_VALUE >= b);
      }
      return (b >= 0 || a < 0 || a + Long.MIN_VALUE <= b);
   }

   /**
    * returns {@code true} if {@code b - a} is an integer. Such a check is
    * important if one is specifying an array size based on long integer
    * bounds. (Array sizes and indices must be integers.)
    *
    * @param a one value
    * @param b the other value
    * @return {@code true} if {@code high - low} is an integer, {@code false}
    *         otherwise.
    */
   public static boolean isIntegerDifference(long a, long b) {
      // If the difference isn't in the long range, it certainly
      // isn't in the integer range.
      if (!isLongDifference(a, b)) {
         return false;
      }

      // Once we know we can safely subtract b from a, we just make
      // sure the difference is small enough.
      return inIntegerRange(b - a);
   }

} // end class

