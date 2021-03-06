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

import java.util.List;
import java.util.ArrayList;

/**
 * Abstract superclass of generators for integer lists. In general, the subclasses implement
 * {@link #generateLongArray(long, long, long)}. The other methods convert an
 * array of {@code long}s into {@code int} and/or a {@code List}, if desired.
 *
 * @author Zachary Kurmas
 */
// (C) 2007 Grand Valley State University
public abstract class IntegerListGenerator {

   /**
    * Generate an array of {@code long} integers using rules specified by the
    * implementing sub-class. The meaning of the parameters is also specified
    * by the implementing class.
    * @param a (meaning defined by subclass)
    * @param b (meaning defined by subclass)
    * @param c (meaning defined by subclass)
    * @return an array of long integers.
    */
   abstract public long[] generateLongArray(long a, long b, long c);

   /**
    * Takes the array of {@code long} integers generated by
    * {@link #generateLongArray(long, long, long)} and converts it to a
    * {@code List} of {@code long} integers.
    *
    * @param a (meaning defined by subclass)
    * @param b (meaning defined by subclass)
    * @param c (meaning defined by subclass)
    *
    * @return an {@code List} of long integers.
    */
   public List<Long> generateLongList(long a, long b, long c) {
      long[] start = generateLongArray(a, b, c);

      // There is a method java.util.Arrays.toList that does this;
      // however, to use it, we would have to first "box up" all the
      // longs in Long objects, which would negate most of the "code
      // re-use" savings in using the library method.
      List<Long> answer = new ArrayList<Long>(start.length);
      for (long value : start) {
         answer.add(value);
      }
      return answer;
   } // end generateLongList

   /**
    * Takes the array of {@code long} integers generated by
    * {@link #generateLongArray(long, long, long)} and converts it to an array
    * of "regular" {@code int}s. This code assumes that if the parameters to
    * {@code generateLongArray} have integer-sized values, than the values
    * returned will be integer-sized also. (If this assumption is not valid, it
    * may be necessary for a subclass to override this method.)
    *
    * @param a (meaning defined by subclass)
    * @param b (meaning defined by subclass)
    * @param c (meaning defined by subclass)
    *
    * @return an array of integers.
    * @throws edu.gvsu.kurmasz.warszawa.util.RangeTests.OutOfRangeException
    *          if any of the values generated are outside the range of an
    *          integer.
    */
   public int[] generateIntArray(int a, int b, int c) {
      long[] start = generateLongArray(a, b, c);
      int[] answer = new int[start.length];
      for (int x = 0; x < start.length; x++) {
         answer[x] = RangeTests.toInt(start[x]);
      }
      return answer;
   } // end generateIntArray

   /**
    * Takes the array of {@code long} integers generated by
    * {@link #generateLongArray(long, long, long)} and converts it to an
    * {@code ArrayList} of "regular" {@code int}s. This code assumes that if the parameters
    * to {@code generateLongArray} have integer-sized values, than the values
    * returned will have integer-sized values also. (If this assumption is not valid, it
    * may be necessary for a subclass to override this method.)
    *
    * @param a (meaning defined by subclass)
    * @param b (meaning defined by subclass)
    * @param c (meaning defined by subclass)
    *
    * @return an {@code ArrayList} of integers.
    * @throws edu.gvsu.kurmasz.warszawa.util.RangeTests.OutOfRangeException
    *          if any of the values generated are outside the range of an
    *          integer.
    */
   public List<Integer> generateIntList(int a, int b, int c) {
      long[] start = generateLongArray(a, b, c);
      ArrayList<Integer> answer = new ArrayList<Integer>(start.length);
      for (long value : start) {
         answer.add(RangeTests.toInt(value));
      }
      return answer;
   }

   /**
    * Calculates how many times the following loop will run: {@code for(int x =
    * start; x <= stop; x+= step)}. (This method is used to calculate the
    * needed size of an array that will hold such values.)
    *
    * @param start the starting value for the loop
    * @param stop  the stopping value for the loop
    * @param step  the step of the loop
    * @return the number of times the described loop will run
    */
   protected static long calculateNumIterationsAsLong(long start, long stop,
                                                      long step) {
      validateStartStopStepParams(start, stop, step);

      long full_range = (stop - start + 1);
      long range = full_range / step;
      if (full_range % step != 0) {
         range++;
      }

      return range;

      /*
         * Bit bucket, in case we ever need to deal with big integers
         *
         * System.out.println("Using big ints"); // if start - stop - 1
         * overflows, then use BigIntegers. BigInteger bstart = new
         * BigInteger(start + ""); BigInteger bstop = new BigInteger(stop + "");
         * BigInteger diff1 = bstop.subtract(bstart); BigInteger bfull_range =
         * diff1.add(BigInteger.ONE); BigInteger[] parts =
         * bfull_range.divideAndRemainder(new BigInteger(step + "")); if
         * (parts[1].equals(BigInteger.ZERO)) { parts[0] =
         * parts[0].add(BigInteger.ONE); } if
         * (!RangeTests.inLongRange(parts[0])) { String message = "Parameters
         * define too many iterations " + " (in particular, more than can be
         * stored in a java long)."; throw new
         * IllegalArgumentException(message); } System.out.println("Answer = " +
         * parts[0]); return RangeTests.toLong(parts[0]);
         */
   } // calculate numIterations as long.

   /**
    * See {@link #calculateNumIterationsAsLong(long, long, long)}.
    */
   protected static int calculateNumIterationsAsInt(int start, int stop,
                                                    int step) {
      long lanswer = calculateNumIterationsAsLong(start, stop, step);
      if (!RangeTests.inIntegerRange(lanswer)) {
         String message = "Parameters define too many iterations "
               + " (in particular, more than can be stored in a java int).";
         throw new IllegalArgumentException(message);
      }
      return (RangeTests.toInt(lanswer));
   } // calculateNumIterationsAsInt

   /**
    * make sure start, stop, and step have sane values (start <= stop, step >=1
    * and (stop - start + 1) is a {@code long}).
    *
    * @param start the starting value for the loop
    * @param stop  the stopping value for the loop
    * @param step  the step of the loop
    * @throws IllegalArgumentException if any parameter value is unreasonable.
    */

   protected static void validateStartStopStepParams(long start, long stop,
                                                     long step) {
      if (stop < start) {
         String message = String.format("Start (%d) must be <= stop (%d)",
               start, stop);
         throw new IllegalArgumentException(message);
      }
      if (step < 1) {
         throw new IllegalArgumentException("step must be >= 1");
      }

      // This lines tests to be sure calculating (stop - start + 1)
      // won't result in an overflow. Thus, we want
      //
      // (stop - start + 1) <= Long.MAX_VALUE;
      //
      // but, we also don't want any intermediate calculations to
      // overflow. Thus, we reorder the inequality such that
      //
      // stop <= Long.MAX_VALUE + start - 1
      //
      // In order for this calculation to not overflow, start must
      // be <= 0. In addition, if stop is < -1 and start <= stop
      // (as previously tests), then no overflow can occur, so we
      // don't need to perform the final comparison.
      if (start <= 0 && stop >= -1 && start + Long.MAX_VALUE - 1 < stop) {
         throw new IllegalArgumentException("The range is too large");
      }
   }
} // end IntegerListGenerator
