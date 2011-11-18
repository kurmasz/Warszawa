package edu.gvsu.kurmasz.warszawa.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * A set of useful methods for dealing with arrays (different from those in
 * {@code Java.util.Arrays}).
 *
 * @author Zachary Kurmas
 */
// (C) 2007 Zachary Kurmas
public class ArrayUtils {

   private static Random r = null;
   static Random getRandom() {
      if (r == null) {
         r = new Random();
      }
      return r;
   }

   private static Object shrinkToFit(Object array, int currentLength, int size) {
      if (size > currentLength) {
         String message = "size must be <= array.length";
         throw new IllegalArgumentException(message);
      }
      if (currentLength == 0) {
         return array;
      }

      Object answer = java.lang.reflect.Array.newInstance(array.getClass().getComponentType(), size);
      System.arraycopy(array, 0, answer, 0, size);
      return answer;
   }

   /**
    * Generate and fill a new array with no extra room. Note: For consistency
    * (i.e., having the method always return a new object), a new array is
    * generated, even if {@code array.length == size}. If {@code array.length
    * == 0} then {@code array} is returned (because {@code array} is immutable).
    *
    * @param array the array to "shrink".
    * @param size  the number of elements in the array to keep.
    * @return a new array containing the first {@code size} elements of {@code
    *         array}. (See note above.)
    * @throws IllegalArgumentException if {@code size > array.length}.
    */
   public static long[] shrinkToFit(long[] array, int size) {
      return (long[]) shrinkToFit(array, array.length, size);
   } // end shrinkToFit(long)

   /**
    * Generate and fill a new array with no extra room. Note: For consistency
    * (i.e., having the method always return a new object), a new array is
    * generated, even if {@code array.length == size}. If {@code array.length
    * == 0} then {@code array} is returned (because {@code array} is immutable).
    *
    * @param array the array to "shrink".
    * @param size  the number of elements in the array to keep.
    * @return a new array containing the first {@code size} elements of {@code
    *         array}. (See note above.)
    * @throws IllegalArgumentException if {@code size > array.length}.
    */

   public static int[] shrinkToFit(int[] array, int size) {
      return (int[]) shrinkToFit(array, array.length, size);
   } // end shrinkToFit(int)

   /**
    * Generate and fill a new array with no extra room. Note: For consistency
    * (i.e., having the method always return a new object), a new array is
    * generated, even if {@code array.length == size}. If {@code array.length
    * == 0} then {@code array} is returned (because {@code array} is immutable).
    *
    * @param array the array to "shrink".
    * @param size  the number of elements in the array to keep.
    * @return a new array containing the first {@code size} elements of {@code
    *         array}. (See note above.)
    * @throws IllegalArgumentException if {@code size > array.length}.
    */

   public static <T> T[] shrinkToFit(T[] array, int size) {
      @SuppressWarnings("unchecked")
      T[] answer = (T[]) shrinkToFit(array, array.length, size);
      return answer;
   } // end shrinkToFit(int)

   /**
    * Randomly permute the values in {@code array}.
    *
    * @param array the array to permute
    * @param rnd the {@code java.util.Random} object used to determine where
    *              values are randomly moved.
    */
   public static <T> void permute(T[] array, java.util.Random rnd) {
      Collections.shuffle(Arrays.asList(array), r);
   }

   /**
    * Randomly permute the values in {@code array}.
    *
    * @param array the array to permute
    */
   public static <T> void permute(T[] array) {
      Collections.shuffle(Arrays.asList(array));
   }

   /**
    * Randomly permute the values in {@code array}.
    *
    * @param array the array to permute
    * @param rnd the {@code java.util.Random} object used to determine where
    *              values are randomly moved.
    */

   public static void permute(int[] array, java.util.Random rnd) {
      if (array.length <= 1) {
         return;
      }

      int to, x;

      /*
         * This is the fisher_yates shuffle as presented in the Perl Cookbook
         * p121
         */
      for (x = array.length -1; x > 0; x--) {
         to = rnd.nextInt(x + 1);

         if (to == x)
            continue;

         int temp;
         temp = array[to];
         array[to] = array[x];
         array[x] = temp;
      } // end for
   }

   /**
    * Randomly permute the values in {@code array}.
    *
    * @param array the array to permute
    *
    */
   public static void permute(int[] array) {
      permute(array, getRandom());
   }
}