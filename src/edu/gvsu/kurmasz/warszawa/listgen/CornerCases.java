package edu.gvsu.kurmasz.warszawa.listgen;

import java.math.BigInteger;

/**
 * Generate lists of integers that represent values surrounding powers of 2.
 * (These are often "corner cases" when testing circuits.) This class is similar
 * to {@link ExponentialCornerCases}; however, the parameters refer to the
 * precise desired maximum and minimum value.
 *
 * @author Zachary Kurmas
 */
// (C) 2007 Grand Valley State University
public class CornerCases extends ExponentialCornerCases {

   /**
    * Generate an array of {@code long} integers containing the values
    * surrounding powers of two. For this class, the user specifically lists the
    * values at which the sequence is to begin and end. In addition to the
    * beginning and ending values, the list includes all integers of the form
    * {@code (2^i)-1}, {@code 2^i}, and {@code (2^i)+1} that are between the
    * beginning and ending values.  (Compare to {@link ExponentialCornerCases}.)
    *
    * @param min  the minimum value to appear in the list.
    * @param max  the maximum value to appear in the list. (Must be &ge; {@code min}.)
    * @param step the value by which the implicit exponent is incremented. Must
    *             be {@code >= 1}.
    * @return an array of long integers.
    * @throws IllegalArgumentException if the parameters specify a list that is too long to fit in a Java array.
    */

   public long[] generateLongArray(long min, long max, long step) {
      BigInteger bmin = new BigInteger(min + "");
      BigInteger bmax = new BigInteger(max + "");

      long st1 = bmin.abs().bitLength() - 1;
      if (bmin.compareTo(BigInteger.ZERO) < 0) {
         st1 = -st1;
      }

      long sp1 = bmax.abs().bitLength() - 1;
      if (bmax.compareTo(BigInteger.ZERO) < 0) {
         sp1 = -sp1;
      }

      // System.out.printf("Calling with %d %d %d %d %d\n",
      // st1, sp1, step, start, stop);
      return generateLongArray(st1, sp1, step, min, max);
   }
}