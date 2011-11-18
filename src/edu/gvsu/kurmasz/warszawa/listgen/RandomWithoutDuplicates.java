package edu.gvsu.kurmasz.warszawa.listgen;

import java.util.Arrays;
import java.util.HashMap;

import edu.gvsu.kurmasz.warszawa.util.ArrayUtils;
import edu.gvsu.kurmasz.warszawa.util.RangeTests;

/*******************************************************************************
 * 
 * Generate lists of unique random integers within a specified range.  
 * 
 * @author Zachary Kurmas
 * 
 ******************************************************************************/
// (C) 2007 Grand Valley State University
public class RandomWithoutDuplicates extends RandomWithDuplicates
{

	// This is the amount by which the desired range must exceed the
	// desired number of values before we switch from the "permute"
	// method to the "discard" method.
	protected int factor = 4;

	/***************************************************************************
	 * Constructor allowing user to specify which {@code java.util.Random}
	 * object to use.
	 * 
	 * @param rin
	 *            the desired {@code java.util.Random} object.
	 * 
	 **************************************************************************/
	public RandomWithoutDuplicates(java.util.Random rin) {
		super(rin);
	}

	/***************************************************************************
	 * Default constructor. Generates a new {@link java.util.Random} object.
	 * 
	 **************************************************************************/
	public RandomWithoutDuplicates() {
		super();
	}

	protected static boolean isPowerOfTwo(long value)
	{
		return false;
	}

	/***************************************************************************
	 * Generate an array of unique random integers containing the values between
	 * {@code start} and {@code stop} (inclusive). At present, we guarantee
	 * uniqueness in one of two ways:
	 * 
	 * <ul>
	 * 
	 * <li> If {@code amount} is a large percentage of the range (i.e.,
	 * {@code max - min}), then we simply permute all the values in the range
	 * then choose the first {@code amount} values. This technique works well,
	 * because we don't waste a lot of memory or effort (almost all of the
	 * permuted values will be used).
	 * 
	 * <li> If {@code amount} is a small percentage of the range (i.e.,
	 * {@code max - min}), then we just draw values until we get the desired
	 * number of unique values. This technique works well because, when
	 * {@code amount} is a small percentage of the range, the probability of
	 * drawing a "used" number is small.  
	 * 
	 * </ul>
	 * 
	 * @param min
	 *            the minimum value that may appear
	 * 
	 * @param max
	 *            the maximum value that may appear. Must be {@code >= min} and
	 *            {@code <= (min + Integer.MAX_VALUE)}
	 * 
	 * @param amount
	 *            the number of integers to generate. Must be {@code >= 1}.
	 * 
	 * @return an array of long integers.
	 * 
	 * @throws IllegalArgumentException
	 *             if the parameters specify an array that is too large.
	 * 
	 **************************************************************************/

	// This class is specifically implemented because it maps well
	// onto the nextint(int) method provided by java.util.Rand. There
	// is no comparable method for long.
	public int[] generateIntArray(int min, int max, int amount)
	{
		validateParameters(min, max, amount);
		int range = RangeTests.toInt(max - min + 1);

		if (range <= factor * amount) {
			return generateIntArrayByPermute(min, max, amount);
		} else {
			return generateIntArrayByDiscard(min, max, amount);
		}

	} // end generateIntArray

	/***************************************************************************
	 * Generates a set of unique random numbers by randomly permuting the range
	 * {@code [min, max]} and choosing the first {@code amount} values. This
	 * works well when {@code amount} is a large percentage of
	 * {@code [min, max]}.
	 * 
	 * 
	 * @param min
	 *            the minimum value that may appear
	 * 
	 * @param max
	 *            the maximum value that may appear. Must be {@code >= min} and
	 *            {@code <= (min + Integer.MAX_VALUE)}
	 * 
	 * @param amount
	 *            the number of integers to generate. Must be {@code >= 1}.
	 * 
	 * @return an array of long integers.
	 * 
	 * @throws IllegalArgumentException
	 *             if the parameters specify an array that is too large.
	 **************************************************************************/

	protected int[] generateIntArrayByPermute(int min, int max, int amount)
	{
		// System.out.println("By Permute");
		validateParameters(min, max, amount);
		int range_size = max - min + 1;

		// Can't generate x unique random numbers from a set of < x values.
		if (range_size < amount) {
			String message = "Can't generate " + amount + " unique numbers "
					+ "from a set of only " + range_size + " values";
			throw new IllegalArgumentException(message);
		}

		int[] range = new int[range_size];
		for (int x = 0; x < range_size; x++) {
			range[x] = min + x;
		}

		ArrayUtils.permute(range, r);
		// System.out.println(Arrays.toString(range));
		int[] answer = ArrayUtils.shrinkToFit(range, amount);

		if (doSort) {
			Arrays.sort(answer);
		}
		return answer;

	} // end generateIntArrayByPermute

	/***************************************************************************
	 * Generates a set of unique random numbers by randomly drawing numbers in
	 * the range {@code [min, max]} until {@code amount} unique numbers have
	 * been drawn. This works well if {@code amount} is a small percentage of
	 * {@code [min, max]}.
	 * 
	 * @param min
	 *            the minimum value that may appear
	 * 
	 * @param max
	 *            the maximum value that may appear. Must be {@code >= min} and
	 *            {@code <= (min + Integer.MAX_VALUE)}
	 * 
	 * @param amount
	 *            the number of integers to generate. Must be {@code >= 1}.
	 * 
	 * @return an array of long integers.
	 * 
	 * @throws IllegalArgumentException
	 *             if the parameters specify an array that is too large.
	 * 
	 * 
	 **************************************************************************/

	protected int[] generateIntArrayByDiscard(int min, int max, int amount)
	{
		validateParameters(min, max, amount);
		int range = max - min + 1;
		final Integer ONE = new Integer(1);

		HashMap<Integer, Integer> chosen = new HashMap<Integer, Integer>();
		while (chosen.size() < amount) {

			int candidate = r.nextInt(range) + min;
			if (!chosen.containsKey(candidate)) {
				chosen.put(candidate, ONE);
			}
		}

		int[] answer = new int[amount];
		int count = 0;
		assert amount == chosen.keySet().size() : "Wrong number of values chosen";

		for (Integer key : chosen.keySet()) {
			answer[count++] = key.intValue();
		}

		if (!doSort) {
			ArrayUtils.permute(answer, r);
		}
		return answer;

	} // generateIntArrayByDiscard

} // end class
