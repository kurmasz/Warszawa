package edu.gvsu.kurmasz.warszawa.listgen;

import edu.gvsu.kurmasz.warszawa.util.RangeTests;
import org.junit.*;

import static edu.gvsu.kurmasz.warszawa.listgen.IntegerListGeneratorTestBase.assertInOrder;
import static edu.gvsu.kurmasz.warszawa.listgen.IntegerListGeneratorTestBase.findUp;
import static edu.gvsu.kurmasz.warszawa.listgen.IntegerListGeneratorTestBase.testConversions;

/**
 * Test for {@code Range}
 *
 * @author Zachary Kurmas
 *
 *         (C) 2007 Grand Valley State University
 */

public class RangeTest {

   protected Range r = new Range();

   public void testGenerateLongArrayHelper(long start, long stop,
                                           long step) {
      //System.out.printf("Testing (%d, %d, %d, %b)\n",
      //		  start, stop, step, mirror);
      long[] answer = r.generateLongArray(start, stop, step);

      //System.out.println(Arrays.toString(answer));
      assertInOrder(answer);

      int place = 0;
      long f;
      for (f = start; f < (stop - step); f += step) {
         place = findUp(f, place, answer);
      }
      findUp(f, place, answer);

      if (RangeTests.inIntegerRange(start) &&
            RangeTests.inIntegerRange(stop)) {
         testConversions(r, start, stop, step, answer);
      }
   }

   @Test
   public void testGenerateLongArray() {
      testGenerateLongArrayHelper(0, 10, 1);
      testGenerateLongArrayHelper(5, 10, 1);
      testGenerateLongArrayHelper(-5, 10, 1);
      testGenerateLongArrayHelper(-25, -15, 1);

      testGenerateLongArrayHelper(0, 10, 2);
      testGenerateLongArrayHelper(0, 11, 2);
      testGenerateLongArrayHelper(5, 10, 2);
      testGenerateLongArrayHelper(5, 11, 2);
      testGenerateLongArrayHelper(-5, 10, 2);
      testGenerateLongArrayHelper(-5, 11, 2);
      testGenerateLongArrayHelper(-4, 10, 2);
      testGenerateLongArrayHelper(-4, 11, 2);

      testGenerateLongArrayHelper(-25, -15, 2);
      testGenerateLongArrayHelper(-25, -14, 2);

      testGenerateLongArrayHelper(-24, -15, 2);
      testGenerateLongArrayHelper(-24, -14, 2);

      testGenerateLongArrayHelper(Long.MAX_VALUE - 100, Long.MAX_VALUE, 2);
      testGenerateLongArrayHelper(1, Long.MAX_VALUE, Long.MAX_VALUE / 143);
      testGenerateLongArrayHelper(1, Long.MAX_VALUE - 1, Long.MAX_VALUE / 143);
      testGenerateLongArrayHelper(2, Long.MAX_VALUE, Long.MAX_VALUE / 143);
      testGenerateLongArrayHelper(-100, Long.MAX_VALUE - 1000,
            Long.MAX_VALUE / 143);

      testGenerateLongArrayHelper(Long.MIN_VALUE / 2 + 1,
            Long.MAX_VALUE / 2 - 1,
            Long.MAX_VALUE / 143);


      testGenerateLongArrayHelper(Long.MIN_VALUE, -2, Long.MAX_VALUE / 143);
      testGenerateLongArrayHelper(Long.MIN_VALUE + 1, -1, Long.MAX_VALUE / 143);
      testGenerateLongArrayHelper(Long.MIN_VALUE + 2, 0, Long.MAX_VALUE / 143);
      testGenerateLongArrayHelper(Long.MIN_VALUE + 3, 1, Long.MAX_VALUE / 143);
      testGenerateLongArrayHelper(Long.MIN_VALUE + 4, 2, Long.MAX_VALUE / 143);
   } // end method


   public void verifyAssert(long a, long b, long c) {
      try {
         r.generateLongArray(a, b, c);
         Assert.fail(String.format("Parameters %d, %d, %d should have " +
               "caused an exception.", a, b, c));
      } catch (IllegalArgumentException e) {
      }
   }

   @Test
   public void verifyAssert() {
      verifyAssert(10, 1, 1);
      verifyAssert(10, 1, 2);
      verifyAssert(-10, -20, 1);
      verifyAssert(10, 1, 0);
      verifyAssert(10, 1, -1);

      // invalid range
      verifyAssert(0, Long.MAX_VALUE, Long.MAX_VALUE / 143);
      verifyAssert(-1, Long.MAX_VALUE - 1, Long.MAX_VALUE / 143);
      verifyAssert(-Long.MAX_VALUE / 14300000, Long.MAX_VALUE - 1,
            Long.MAX_VALUE / 143);
      verifyAssert(Long.MIN_VALUE / 2, Long.MAX_VALUE / 2,
            Long.MAX_VALUE / 143);
      verifyAssert(Long.MIN_VALUE, -1, Long.MAX_VALUE / 143);
      verifyAssert(Long.MIN_VALUE, 0, Long.MAX_VALUE / 143);
      verifyAssert(Long.MIN_VALUE, 1, Long.MAX_VALUE / 143);
      verifyAssert(Long.MIN_VALUE, Long.MAX_VALUE, Long.MAX_VALUE / 143);

      // Too many outputs
      verifyAssert(2, Long.MAX_VALUE, 3);
      verifyAssert(1, Long.MAX_VALUE, Integer.MAX_VALUE - 1);

   }
} // end class



	
	