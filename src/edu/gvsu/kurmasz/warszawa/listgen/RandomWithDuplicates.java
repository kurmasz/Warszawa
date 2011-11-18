package edu.gvsu.kurmasz.warszawa.listgen;

import edu.gvsu.kurmasz.warszawa.util.RangeTests;

import java.util.Arrays;

/**
 * Generate lists of random integers within a specified range. Note:
 * {@code generateLongArray} is not yet implemented for values outside of an
 * integer range.
 *
 * @author Zachary Kurmas
 */
// (C) 2007 Grand Valley State University
public class RandomWithDuplicates extends IntegerListGenerator {

   protected java.util.Random r;
   protected boolean doSort = true;

   /**
    * Constructor allowing user to specify which {@code java.util.Random}
    * object to use.
    *
    * @param rin the desired {@code java.util.Random} object.
    */
   public RandomWithDuplicates(java.util.Random rin) {
      r = rin;
   }

   /**
    * Default constructor. Generates a new {@link java.util.Random} object.
    */
   public RandomWithDuplicates() {
      r = new java.util.Random();
   }

   protected static boolean isPowerOfTwo(long value) {
      return false;
   }

   /**
    * Generate an array of random {@code long} integers containing the values
    * between {@code start} and {@code stop} (inclusive) ---
    * <em>parameters must be in an </em>integer<em> range</em>.
    *
    * @param min    the minimum value that may appear. For now,
    *               {@code Integer.MIN_VALUE} &le; {@code min} &le;
    *               {@code Integer.MAX_VALUE}.
    * @param max    the maximum value that may appear. For now,
    *               {@code Integer.MIN_VALUE} &le; {@code min} &le;
    *               {@code Integer.MAX_VALUE}. Also,
    *               {@code max <= min + Integer.MAX_VALUE}.
    * @param amount the number of integers to generate. Must be {@code >= 1} and
    *               {@code < Integer.MAX_VALUE}.
    * @return an array of {@code long} integers.
    * @throws IllegalArgumentException if the parameters specify an array that is too large, or if
    *                                  any of the parameters is out of range.
    */
   public long[] generateLongArray(long min, long max, long amount) {
      validateParameters(min, max, amount);

      int iamount = RangeTests.toInt(amount); // range validate by validateParameters

      // If the values are all in the integer range, use that method
      // and convert to long array.
      if (RangeTests.inIntegerRange(min) && RangeTests.inIntegerRange(max)
            && RangeTests.inIntegerRange(max - min)) {

         long[] answer = new long[iamount];
         int[] tempAnswer = generateIntArray((int) min, (int) max, iamount);
         for (int x = 0; x < iamount; x++) {
            answer[x] = tempAnswer[x];
         }
         return answer;
      }

      // Otherwise, for now, we require that min be a power of 2,
      // and max be one less than a power of two.

      throw new IllegalArgumentException("Generating long values is not "
            + "yet implemented.");

      /*
         * BigInteger bmin = new BigInteger(min + ""); BigInteger bmax = new
         * BigInteger(max + ""); BigInteger range =
         * bmax.subtract(bmin).add(BigInteger.ONE);
         *
         * assertIntegerRange(amount); int size = (int)amount; if (mirror) {
         * amount *= 2; } // If the range is larger than 2^64, then we'll just
         * chose // from all long values and throw away the ones we don't like.
         * if (range.compareTo(new BigInteger(Long.MAX_VALUE + "")) > 0) {
         *
         * long[] values = new long[amount]; }
         *
         *
         * int bitWidth = range.bitLength() + 1;
         *
         *
         * for (int x = 0; x < amount; x++) { ; }
         */

   }

   /**
    * Generate an array of {@code amount} random integers containing the values between
    * {@code min} and {@code max} (inclusive).
    *
    * @param min    the minimum value that may appear
    * @param max    the maximum value that may appear. Must be {@code >= min} and
    *               {@code <= (min + Integer.MAX_VALUE)}
    * @param amount the number of integers to generate. Must be {@code >= 1}.
    * @return an array of integers.
    * @throws IllegalArgumentException if the parameters specify an array that is too large.
    */

   // This class is specifically implemented because it maps well
   // onto the nextint(int) method provided by java.util.Rand. There
   // is no comparable method for long.
   public int[] generateIntArray(int min, int max, int amount) {
      validateParameters(min, max, amount);
      int[] values = new int[amount];

      int range = max - min + 1;

      for (int x = 0; x < amount; x++) {
         values[x] = r.nextInt(range) + min;
      }

      Arrays.sort(values);
      return values;
   } // end generateIntArray

   /**
    * Make sure the parameters are reasonable
    */
   protected void validateParameters(long min, long max, long amount) {
      // max *may* be == to min.
      if (max < min) {
         throw new IllegalArgumentException("Start must be <= stop");
      }
      if (amount < 1) {
         throw new IllegalArgumentException("amount must be >= 1");
      }

      // We really want to know if max - min + 1 is an integer.
      // However, max + 1 may possibly overflow. Hence, the need
      // for two tests. The first makes sure the calculation "max -
      // min + 1" won't overflow.
      if (!RangeTests.isIntegerDifference(min, max)
            || !RangeTests.inIntegerRange(max - min + 1L)) {
         throw new IllegalArgumentException("Range is too large."
               + "Must be an integer.");
      }

      if (!RangeTests.inIntegerRange(amount)) {
         throw new IllegalArgumentException("Amount must be <= "
               + Integer.MAX_VALUE);
      }

   }

   /**
    * Make sure the parameters are reasonable
    */
   protected void validateParameters(int min, int max, int amount) {
      // max *may* be == to min.
      if (max < min) {
         throw new IllegalArgumentException("Start must be <= stop");
      }
      if (amount < 1) {
         throw new IllegalArgumentException("amount must be >= 1");
      }

      // We really want to know if max - min + 1 is an integer.
      // However, max + 1 may possibly overflow. Hence, the need
      // for two tests. The first makes sure the calculation "max -
      // min + 1" won't overflow.
      if (!RangeTests.isIntegerDifference(min, max)
            || !RangeTests.inIntegerRange(max - min + 1L)) {
         throw new IllegalArgumentException("Range is too large."
               + " Must be an integer");
      }
   } // end validateParameters

}
