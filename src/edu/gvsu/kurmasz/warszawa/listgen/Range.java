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

/**
 * Generate lists of integers within a specified range (like a simple
 * {@code for} loop).
 *
 * @author Zachary Kurmas
 */
// (C) 2007 Grand Valley State University
public class Range extends IntegerListGenerator {

   /**
    * Generate an array of long integers containing the values between
    * {@code start} and {@code stop} (inclusive). Calling
    * {@code generateLongArray(start, stop, step)} will produce the list of
    * values taken on by {@code i} in this loop:
    * {@code for(int i = start; i <= stop; i+= step)}.
    * <p>
    *
    * The parameters must meet these conditions:
    *
    * <ul>
    *
    * <li> {@code start <= stop}
    *
    * <li> {@code step >= 1}
    *
    * <li> {@code start} and {@code stop} defined such that the total range ({@code stop - start + 1})
    * is a valid Java {@code long}.  (This means that calling {@code generateLongArray(Long.MIN_VALUE, 1, 1)}
    * won't work.)
    *
    * <li> {@code start}, {@code stop}, and {@code step} defined such that
    * the number of values generated is a valid Java {@code int} (i.e., not
    * larger than the maximum size of an array).
    *
    * </ul>
    *
    * @param start the first value in the output.
    * @param stop  the upper-bound for values in the output. (If {@code step} is
    *              not equal to 1, then {@code stop} may not appear in the
    *              output. Must be {@code >= start}.
    * @param step  the difference between successive values in the list. Must be
    *              {@code >= 1}.
    * @return an array of long integers.
    * @throws IllegalArgumentException if the parameters violate any of the conditions listed above.
    */
   public long[] generateLongArray(long start, long stop, long step) {

      // make sure start, stop, and step have sane values
      validateStartStopStepParams(start, stop, step);

      long lsize = calculateNumIterationsAsLong(start, stop, step);
      int size;
      if (RangeTests.inIntegerRange(lsize)) {
         size = RangeTests.toInt(lsize);
      } else {
         String message = "The parameters define an array "
               + "that is too long (" + lsize + ")";
         throw new IllegalArgumentException(message);
      }

      long[] answer = null;

      try {
         answer = new long[size];
      } catch (OutOfMemoryError ome) {
         String message = "The parameters define an array "
               + "that uses up all the virtual memory (" + lsize + ")";
         throw new IllegalArgumentException(message);
      }
      assert answer != null : "Try-catch block not set up correctly";

      // To prevent place += step from overflowing, we stop the loop
      // one step early and do that last addition by hand.
      int count = 0;
      long place;
      for (place = start; place <= (stop - step); place += step, count++) {
         answer[count] = place;
      }
      answer[count++] = place;

      assert count == size : String.format(
            "size calculated incorrectly:  count %d, size %d", count, size);

      return answer;
   }

} // end Range

