package edu.gvsu.kurmasz.warszawa.listgen;


import edu.gvsu.kurmasz.warszawa.util.ArrayUtils;
import edu.gvsu.kurmasz.warszawa.util.RangeTests;

/**
 * Generate lists of integers that represent values surrounding powers of 2
 * (using exponents for parameters). (Such values are often "corner cases" when testing
 * circuits.) This class is similar to {@link CornerCases}, except the
 * parameters refer to exponents instead of actual values. For example,
 * {@code ExponentailCornerCases.getLongArray(5, 7, 1)} produces the same list
 * as {@code CornerCases.getLongArray(32, 127, 1)}.
 *
 * @author Zachary Kurmas
 */
// (C) 2007 Grand Valley State University
public class ExponentialCornerCases extends IntegerListGenerator {

   /**
    * Inner class to simplify the adding of values in a desired range to an
    * array. (Values outside the range are ignored.)
    */
   protected class ArrayWrapper {
      protected long[] array;
      protected int count;
      protected long min, max;

      /**
       * Constructor
       *
       * @param size   the size of the array
       * @param min_in the minimum value to be placed in the array.
       * @param max_in the maximum value to be placed in the array.
       *
       */
      public ArrayWrapper(int size, long min_in, long max_in) {
         array = new long[size];
         count = 0;
         min = min_in;
         max = max_in;
         array[count++] = min;
      }

      /**
       * add a group of three values to the array. (Values outside the
       * specified range are not added.)
       *
       * @param value the center of the group to add
       *
       */
      public void addGroup(long value) {
         addOne(value - 1);
         addOne(value);
         addOne(value + 1);
      }

      protected boolean checkPrevTwo(long value) {
         if (count >= 1 && array[count - 1] == value) {
            return true;
         }
         if (count >= 2 && array[count - 2] == value) {
            return true;
         }
         return false;
      }

      /**
       * add a single value to the array. (Values outside the specified range
       * are not added.)
       *
       * @param value the value to add.
       */
      public void addOne(long value) {
         if (count == 1 || value == max || value == -3
               || (value >= 1 && value <= 3)) {
            if (checkPrevTwo(value)) {
               return;
            }
         }

         if (value >= min && value <= max) {
            array[count++] = value;
         }
      }

      public int getLength() {
         return count;
      }

      /**
       * returns the array
       *
       * @return the array.
       */
      public long[] getArray() {
         return ArrayUtils.shrinkToFit(array, count);
      }
   } // end class

   /**
    * Calculate the integer represented by the "exponent". A negative
    * {@code place} specifies {@code -(2^|place|)}, <em>not {@code
    * 2^(-|place|)}.
    *
    * @param place a modified exponent.
    * @return {@code 2^place} if {@code place >= 0} and {@code
    *         -(2^|place|)} if {@code place < 0}.
    *
    */
   protected long calculateBase(long place) {
      if (place == 0) {
         return 1;
      } else if (place < 0) {
         return -(1L << -place);
      } else {
         return (1L << place);
      }
   }

   /**
    * Generate an array of {@code long} integers containing the values
    * surrounding powers of two. For this class, the user uses exponents to
    * specify the values at which the sequence is to begin and end. For each
    * exponent {@code i} specified (except the first and last), the values
    * {@code (2^i)-1}, {@code 2^i}, and {@code (2^i)+1} are included in the
    * output.
    *
    * <p>
    *
    * The first and last exponents behave differently: {@code (2^{start})-1}
    * is not included. Similarly The last value in the list will be
    * {@code (2^stop)-1}. (In other words, {@code 2^stop}, and
    * {@code (2^stop)+1} are not included.) These rules allow the user to use the
    * minimum and maximum widths of the desired values as the {@code start} and
    * {@code stop} parameters. For example, when
    * testing a 64-bit adder, using 64 as the {@code stop} parameter will
    * specify that the last value is {@code (2^64)-1}: the maximum value that
    * can be represented with 64 bits.
    *
    * <p>
    *
    * Negative parameter values are treated specially: If {@code start} is
    * {@code -4}, the first value will be {@code -(2^|start|) = -16} (because
    * {@code 2^-4} is not an integer).
    * <p>
    *
    * @param start the exponent of the first desired value. (<em>See comment above</em>.)
    *              Also, {@code -64} &le; {@code start} &le; {@code 64} (otherwise the
    *              resulting values won't be Java {@code long}s).
    * @param stop  the exponent of the last desired value. (<em>See comment above</em>.)
    *              Must be {@code >= start}. The value{@code (2^stop)-1} will
    *              always appear in the list, even if {@code step} would
    *              otherwise cause it to be skipped. {@code stop} must be
    *              {@code >= start}. Also, {@code -64} &le; {@code stop} &le;
    *              {@code 64} (otherwise the resulting values won't be Java
    *              {@code long}s).
    * @param step  the value by which the exponent is incremented. Must be
    *              {@code >= 1}. (See comment for {@code stop} parameter.)
    * @return an array of {@code long} integers.
    * @throws IllegalArgumentException if the parameter values specify either (1) a value that is
    *                                  outside the range of a Java {@code long}, or (2) more values
    *                                  than can be stored in a single array.
    */
   public long[] generateLongArray(long start, long stop, long step) {
      if (start == stop) {
         throw new IllegalArgumentException("Start may not equal stop");
      }
      return generateLongArray(start, stop, step, calculateBase(start),
            calculateBase(stop) - 1);

   }

   /**
    * Similar to {@link #generateLongArray(long, long, long)}, except the user
    * explicitly lists minimum and maximum values. No values outside the range
    * {@code [min, max]} will appear, and both {@code min} and {@code max}
    * <em>will</em> appear in the list, even if they are not corner cases.
    *
    * @param start the exponent of the first desired value.
    * @param stop  the exponent of the last desired value. Must be
    *              {@code >= start}. The value specified by {@code stop} will
    *              always appear in the list, even if {@code step} would
    *              otherwise cause it to be skipped. {@code stop} must be
    *              {@code >= start}.
    * @param step  the value by which the exponent is incremented. Must be
    *              {@code >= 1}.
    * @param min   the minimum value to appear in the list.
    * @param max   the maximum value to appear in the list.
    * @return an array of long integers.
    * @throws {@code IllegalArgumentException}
    *                if any parameter value is unreasonable.
    */

   protected long[] generateLongArray(long start, long stop, long step,
                                      long min, long max) {
      // System.out.printf("Entering %d %d %d %d %d\n",
      // start, stop, step, min, max);
      if (start >= Long.SIZE || start <= -Long.SIZE) {
         throw new IllegalArgumentException("Must have -63 <= start <= 63");
      }

      if (stop >= Long.SIZE || stop <= -Long.SIZE) {
         throw new IllegalArgumentException("Must have -63 <= stop <= 63");
      }

      /*
         * if (step > Long.SIZE*2) { throw new IllegalArgumentException(step + "
         * is too big for long"); }
         */

      validateStartStopStepParams(start, stop, step);

      long lsize = calculateNumIterationsAsLong(start, stop, step) * 3 + 2;

      // This should always pass. The number of items should be
      // well under 2^32
      int size = RangeTests.toInt(lsize);

      ArrayWrapper aw = new ArrayWrapper(size, min, max);

      for (long place = start; place <= stop; place += step) {
         aw.addGroup(calculateBase(place));
      } // end for

      aw.addOne(max);
      return aw.getArray();

   } // end generate...
} // end class

