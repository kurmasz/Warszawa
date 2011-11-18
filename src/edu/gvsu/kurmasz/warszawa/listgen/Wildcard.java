package edu.gvsu.kurmasz.warszawa.listgen;

/**
 * Generate lists of integers based on an integer with "wildcards". For example,
 * the binary value {@code 10**} is expanded into the set
 * {@code {1000, 1001, 1010, 1011}}.
 *
 * @author Zachary Kurmas
 */
// (C) 2007 Grand Valley State University
public class Wildcard extends IntegerListGenerator {

   /**
    * Limit on the size of the output. In the worst case, a wildcard mask of
    * {@code -1} will produce an array containing 2<sup>32</sup> elements.
    * This is not illegal in Java; but, will almost certainly exhaust the
    * computer's virtual memory. To prevent this, we set a default upper limit.
    */
   public static final int DEFAULT_MAX_RETURN_SIZE = 1 << 24;

   protected int maxReturnSize = DEFAULT_MAX_RETURN_SIZE;

   /**
    * Sets a new upper bound for size of the array to be generated.
    *
    * @param new_mrs new maximum array size returned. Must be {@code >= 1}.
    * @throws IllegalArgumentException if {@code new_mrs} &le; {@code 0}.
    */
   public void setMaxReturnSize(int new_mrs) {
      if (new_mrs <= 0) {
         String message = "Maximum return size must be >= 1.";
         throw new IllegalArgumentException(message);
      }
      maxReturnSize = new_mrs;
   }

   /**
    * Returns the maximum size of the array that will be generated.
    *
    * @return the maximum size of the array that will be generated.
    */
   public int getMaxReturnSize() {
      return maxReturnSize;
   }

   /**
    * Generate a {@code String} describing the set of integers to be generated
    * by {@link #generateLongArray(String)} given {@code base} and
    * {@code wildcards}.
    *
    * @param base
    * @param wildcards
    * @return a {@code String} describing the set of integers to be generated
    *         by {@link #generateLongArray(String)}
    */
   public static String longPairToWildcardString(long base, long wildcards) {
      String answer = "";
      for (int x = 0; x < Long.SIZE; x++) {

         long mask = 1L << x;

         // if this position is a wildcard, add a "*" to front of the string.
         // Otherwise, just add a 1 or 0 as appropriate.
         if ((wildcards & mask) != 0) {
            answer = "*" + answer;
         } else if ((base & mask) != 0) {
            answer = "1" + answer;
         } else {
            answer = "0" + answer;
         }
      } // end for

      // Now, get rid of leading 0s.

      if (answer.length() == 1) {
         return answer;
      }
      int place = 0;
      while (answer.charAt(place) == '0' && place != answer.length() - 1)
         place++;

      return answer.substring(place);

   }

   /**
    * A class to hold a pair of values.
    *
    * @param <T> base
    * @param <U> wildcards
    * @author Zachary Kurmas
    */
   public static class Pair<T, U> {
      public T base;
      public U wildcards;
   }

   /**
    * Convert a {@code String} describing a set of integers (e.g.,
    * "0101X00XX01") into the pair of {@code long}s used by
    * {@link #generateLongArray(long, long, long)}. Wildcards may be designated
    * as "X", "x", "*", or "?". Leading and trailing whitespace will be
    * ignored. Internal whitespace is an error.
    *
    * @param str A {@code String} describing a set of integers by marking
    *            certain bits as "wildcards" with {@code X}s.
    * @return the pair of {@code long}s used by
    *         {@link #generateLongArray(long, long, long)}.
    * @throws IllegalArgumentException if {@code str} contains characters other than "0", "1", or
    *                                  valid wildcard characters.
    */
   public static Pair<Long, Long> wildcardStringToLongPair(String str) {
      if (str == null) {
         throw new IllegalArgumentException("Null strings not allowed");
      }

      String ustr = str.trim().toUpperCase();
      if (ustr.length() == 0) {
         // Empty strings are not allowed, because they would produce the
         // output {0},
         // which is not consistent.
         throw new IllegalArgumentException("Empty strings not allowed");
      }
      Pair<Long, Long> answer = new Pair<Long, Long>();

      long base = 0;
      long wildcards = 0;

      for (int x = 0; x < ustr.length(); x++) {
         long mask = 1L << x;
         char c = ustr.charAt(ustr.length() - x - 1);

         // If the character indicates a wildcard,
         // put a 1 in the corresponding bit in wildcards.
         if (c == 'X' || c == '?' || c == '*') {
            wildcards |= mask;

            // Although it technically doesn't matter, we'll make wildcard
            // spots 0s in the base (which is accomplished by doing
            // nothing).
         } else if (c == '1') {
            base |= mask;
         } else if (c != '0') {
            throw new IllegalArgumentException(String.format(
                  "\"%c\" is not a valid character in string \"%s\".", c,
                  str));
         }
      } // end for

      answer.base = base;
      answer.wildcards = wildcards;
      return answer;
   }

   /**
    * Generate an array of {@code long} integers based on an integer with some
    * of its bits designated as "wildcards". For example, the binary value
    * {@code 10**} is expanded into the set {@code {1000, 1001, 1010, 1011}}
    * (which is returned as the array {@code {8, 9, 10, 11}}).
    *
    * <p>
    *
    * Because the parameters to the generation methods are integers, and not
    * {@code Strings}, we must use two parameters: One to specify the value of
    * the "fixed" bits, and the other to specify the locations of the
    * wildcards. In the example above, the first parameter would be
    * <code>8</code> (1000 in binary) and the second parameter would be
    * <code>3</code> (11 in binary). The second parameter tells us that the
    * last two bits are wildcards. The first parameter tells us the values of
    * those bits that are not wildcards (in this case that the second two bits
    * are 10).
    *
    * <p>
    *
    * Here are some example inputs and outputs:
    *
    * <ol>
    *
    * <li> {@code generateLongArray(x, 0) = {x}} for all {@code x}. (No bits
    * are wildcards.)
    *
    * <li> {@code generateLongArray(0, 1) = {0, 1}}. The least significant bit
    * is the only wildcard.
    *
    * <li> {@code generateLongArray(1, 1) = {0, 1}}. The least significant bit
    * is the only wildcard. Notice that if a bit is specified as a wildcard, it
    * doesn't matter what the corresponding bit in the "fixed" parameter is.
    *
    * <li> {@code generateLongArray(52, 1) = {52, 53}}. The least significant
    * bit is the only wildcard.
    *
    * <li> {@code generateLongArray(53, 1) = {52, 53}}. The least significant
    * bit is the only wildcard.
    *
    *
    * <li> {@code generateLongArray(0, 3) = {0, 1, 2, 3}}.
    *
    * <li> {@code generateLongArray(4, 3), = {4, 5, 6 , 7}}.
    *
    * <li>{@code generateLongArray(9, 18) = {9, 11, 25, 27}}. ({@code 9 = 01001b}
    * and {@code 18 = 10010b}. Together, they define the pattern "{@code *10*1}".
    * The pattern produces {@code {01001, 01011, 11001,11011}}.)
    * </ol>
    *
    * <p>
    * If trying to figure out how to use {@code base} and {@code wildcards}
    * gives you a headache, check out {@link #wildcardStringToLongPair(String)}
    * and {@link #longPairToWildcardString(long, long)}.
    *
    * @param base      an integer specifying the bits that are considered "fixed".
    * @param wildcards an integer whose "1" bits identify the location of the
    *                  "wildcards".
    * @param notused   not used.
    * @return an array of {@code long} integers.
    * @throws IllegalArgumentException of {@code wildcards} defines too many values.
    */
   public long[] generateLongArray(long base, long wildcards, long notused) {
      // First, we need to make sure that there is a 0 in base at
      // every wildcard position. Wildcards are specified by 1s, so
      // we invert wildcards and do an "&". This will turn all
      // wildcard positions to 0.
      long clean_base = base & ~wildcards;

      // Now, we get the wildcard masks: The set of all 2^n masks
      // defined by n wildcard bits.
      long[] wildcardMasks = getWildcardMasks(wildcards);

      // At this point, the answer is each of the wildcard masks
      // or'd with the "clean" base.
      for (int x = 0; x < wildcardMasks.length; x++) {
         wildcardMasks[x] |= clean_base;
      }

      return wildcardMasks;
   }

   /**
    * Generate an array of {@code long} integers based on an integer with some
    * of its bits designated as "wildcards". For example, {@code "10**"} is
    * expanded into the set {@code {1000, 1001, 1010, 1011}} (which is
    * returned as the array {@code {8, 9, 10, 11}}).
    *
    * @param str A description of the set of values to return
    * @return an array of {@code long} integers.
    */
   public long[] generateLongArray(String str) {
      Pair<Long, Long> p = wildcardStringToLongPair(str);
      return generateLongArray(p.base, p.wildcards, 0);
   }

   /**
    * Generates the set of masks specified by {@code wildcards}. In
    * particular, each bit in {@code wildcards} with a value of 1 is a
    * wildcard. Thus if {@code wildcards} contains {@code n} bits with a value
    * of 1, it specifies a set of {@code 2^n} masks.
    *
    * For example the input 5 (binary {@code 101b}) specifies 4 masks:
    * {@code 000}, {@code 001}, {@code 100}, and {@code 101}.
    *
    * @param wildcards a long integer whose bits with value 1 specify the location of
    *                  wildcards.
    * @return a set of masks that implement the wildcards.
    * @throws IllegalArgumentException of {@code wildcards} defines too many values.
    */
   public long[] getWildcardMasks(long wildcards) {

      // Suppose wildcards = 001101. There are 1s in positions 0, 2, and 3.
      // Therefore, generate the array {000001, 000100, 001000}.
      long[] one_bit_masks = new long[Long.SIZE];
      int num_ones = 0;
      long ONE = 1L;
      for (long x = 0; x < Long.SIZE; x++) {
         if ((wildcards & (ONE << x)) != 0) {
            one_bit_masks[num_ones] = ONE << x;
            num_ones++;
         }
      }

      // if num_ones >= Long.SIZE -1, then 1<< num_ones will either be
      // negative or nonsense.
      if (num_ones >= Long.SIZE - 1) {
         String message = "A mask of " + wildcards
               + " produces more outputs than will fit in a Java array.";
         throw new IllegalArgumentException(message);
      }

      // The number of masks generated is 2^{num_ones}
      int size = 1 << num_ones;
      assert size >= 0 : "Programmer error! Size is " + size;
      // System.err.printf("Mask: %d size %d num_ones %d max %d\n",
      // wildcards, size, num_ones, maxReturnSize);

      if (size > maxReturnSize) {
         String message = "Wildcard mask of " + wildcards
               + " produces too many values.";
         throw new IllegalArgumentException(message);
      }

      // With 1s in num_ones positions, we know that we will be producing
      // size = 2^{num_ones} masks.

      // Here's the main idea: If we put the masks to be generated in
      // ascending order, then the least-significant bit that will change
      // will strictly alternate 010101 as you look down the list.. The next
      // bit that will change will
      // have a pattern like this 00110011 as you look down the list.
      //
      // For example, consider the input 001101 (which corresponds to XX??X?).
      // The output in ascending order would be:

      // XX00X0
      // XX00X1
      // XX01X0
      // XX01X1
      // XX10X0
      // XX10X1
      // XX11X0
      // XX11X1

      // Notice that bit 0 alternates 010101. Bit 2 (the next wildcard bit)
      // has the pattern 00110011.
      // Bit 3 has the pattern 00001111

      // The loop below iterates through each of the size masks being created.
      // For each mask, it looks at each of the wildcard bits and decides
      // whether it should be a 1 or a 0 based on its index in maskArray.
      // We least significant wildcard bit (where oneCount = 0) to have the
      // same pattern as bit 0 in a counter (that is 010101). We want the next
      // wildcard bit to have the same pattern as bit 1 in a counter
      // (00110011).
      // We set lmask = 2^{oneCount} to isolate bit oneCount in counter. We
      // then set the corresponding bit in maskArray[count] to the value of
      // the
      // isolated bit in the counter.

      long[] maskArray = new long[size];
      for (int count = 0; count < size; count++) {
         maskArray[count] = 0;
         for (int oneCount = 0; oneCount < num_ones; oneCount++) {
            int lmask = 1 << oneCount;
            if ((lmask & count) != 0) {
               maskArray[count] |= one_bit_masks[oneCount];
            }
         } // end inner for
      } // end outer for
      return maskArray;
   } // end getMasks

   protected static void main(String[] args) {

      Wildcard w = new Wildcard();
      w.getWildcardMasks(-1);
   }

}