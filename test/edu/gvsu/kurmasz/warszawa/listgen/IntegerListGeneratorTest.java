package edu.gvsu.kurmasz.warszawa.listgen;

import edu.gvsu.kurmasz.warszawa.util.RangeTests;
import org.junit.*;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Test for {@code IntegerValuesGenerator}
 *
 * @author Zachary Kurmas
 *
 *         (C) 2007 Grand Valley State University
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({IntegerListGenerator.class, RangeTests.class})
public class IntegerListGeneratorTest {

   public void validateStartStopTestFailure(long start, long stop, long step) {
      try {
         IntegerListGenerator.validateStartStopStepParams(start, stop, step);
         Assert.fail(String.format("Arguments of %d %d %d should assert.",
               start, stop, step));

      } catch (IllegalArgumentException e) {
      }
   }

   @Test
   public void testValidateStartStopStep() {
      long mxv = Long.MAX_VALUE;
      long mnv = Long.MIN_VALUE;
      // Should pass
      try {

         IntegerListGenerator.validateStartStopStepParams(0, 1, 1);
         IntegerListGenerator.validateStartStopStepParams(1, 1, 1);
         IntegerListGenerator.validateStartStopStepParams(1, 1, 2);

         // Extremes
         IntegerListGenerator.validateStartStopStepParams(1, mxv, 1);
         IntegerListGenerator.validateStartStopStepParams(0, mxv - 1, 1);

         IntegerListGenerator.validateStartStopStepParams(mnv, -2, 1);
         IntegerListGenerator.validateStartStopStepParams(mnv + 1, -1, 1);
         IntegerListGenerator.validateStartStopStepParams(mnv + 2, 0, 1);
         IntegerListGenerator.validateStartStopStepParams(mnv + 3, 1, 1);

         IntegerListGenerator.validateStartStopStepParams(mnv / 2,
               mxv / 2 - 1, 1);

         IntegerListGenerator.validateStartStopStepParams(mnv / 2 + 1,
               mxv / 2, 1);

      } catch (IllegalArgumentException e) {
         Assert.fail("All above should pass");
      }

      // verify start must be <= step
      validateStartStopTestFailure(0, -1, 1);
      validateStartStopTestFailure(-10, -20, 1);

      // verify stop must be >=  0
      validateStartStopTestFailure(1, 10, 0);
      validateStartStopTestFailure(1, 10, -1);

      // verify range must have a long value
      validateStartStopTestFailure(0, mxv, 1);
      validateStartStopTestFailure(mnv, 0, 1);

      validateStartStopTestFailure(mnv, -1, 1);
      validateStartStopTestFailure(mnv, mxv, 1);
      validateStartStopTestFailure(mnv + 1, mxv - 1, 1);

      validateStartStopTestFailure(mnv / 2, mxv / 2, 1);
      validateStartStopTestFailure(mnv / 2 - 1, mxv / 2, 1);
      validateStartStopTestFailure(mnv / 2, mxv / 2 + 1, 1);

   } // end testValidateStartStopStep


   public void test_calculateNumIterationsAsLong_helper(long start,
                                                        long stop,
                                                        long step) {
      long observed =
            IntegerListGenerator.calculateNumIterationsAsLong(start,
                  stop,
                  step);

      long expected = 0;

      assert step >= 1 : "A bad step got through!";
      for (long x = start; x <= stop; x += step) {
         expected++;

         // make sure x += step can't overflow.
         if (x > Long.MAX_VALUE - step) {
            break;
         }
      }
      Assert.assertEquals(expected, observed);
   }


   public void test_calculateNumIterationsAsLong_helper2(long start,
                                                         long stop,
                                                         long step,
                                                         long expected) {
      long observed =
            IntegerListGenerator.calculateNumIterationsAsLong(start,
                  stop,
                  step);
      Assert.assertEquals(expected, observed);
   }


   public void validateFailure(long start, long stop, long step) {
      try {
         IntegerListGenerator.calculateNumIterationsAsLong(start,
               stop,
               step);
         Assert.fail("This should have thrown an exception");
      } catch (IllegalArgumentException e) {
         ;
      }
      return;
   }


   @Test
   public void test_calculateNumIterationsAsLong() {
      // 0 - pos
      test_calculateNumIterationsAsLong_helper(0, 10, 1);
      test_calculateNumIterationsAsLong_helper(0, 10, 2);
      test_calculateNumIterationsAsLong_helper(0, 10, 3);

      test_calculateNumIterationsAsLong_helper(0, 11, 1);
      test_calculateNumIterationsAsLong_helper(0, 11, 2);
      test_calculateNumIterationsAsLong_helper(0, 11, 3);

      test_calculateNumIterationsAsLong_helper(0, 12, 1);
      test_calculateNumIterationsAsLong_helper(0, 12, 2);
      test_calculateNumIterationsAsLong_helper(0, 12, 3);

      test_calculateNumIterationsAsLong_helper2(0, Long.MAX_VALUE - 2, 1,
            Long.MAX_VALUE - 1);
      test_calculateNumIterationsAsLong_helper2(0, Long.MAX_VALUE - 1, 1,
            Long.MAX_VALUE);

      test_calculateNumIterationsAsLong_helper2(0, Long.MAX_VALUE - 2, 2,
            (Long.MAX_VALUE - 1) / 2);
      test_calculateNumIterationsAsLong_helper2(0, Long.MAX_VALUE - 1, 2,
            (Long.MAX_VALUE / 2) + 1);


      // 1 -pos
      test_calculateNumIterationsAsLong_helper(1, 10, 1);
      test_calculateNumIterationsAsLong_helper(1, 10, 2);
      test_calculateNumIterationsAsLong_helper(1, 10, 3);

      test_calculateNumIterationsAsLong_helper(1, 11, 1);
      test_calculateNumIterationsAsLong_helper(1, 11, 2);
      test_calculateNumIterationsAsLong_helper(1, 11, 3);

      test_calculateNumIterationsAsLong_helper(1, 12, 1);
      test_calculateNumIterationsAsLong_helper(1, 12, 2);
      test_calculateNumIterationsAsLong_helper(1, 12, 3);

      test_calculateNumIterationsAsLong_helper2(1, Long.MAX_VALUE - 1, 1,
            Long.MAX_VALUE - 1);
      test_calculateNumIterationsAsLong_helper2(1, Long.MAX_VALUE, 1,
            Long.MAX_VALUE);

      test_calculateNumIterationsAsLong_helper2(1, Long.MAX_VALUE - 1, 2,
            (Long.MAX_VALUE - 1) / 2);
      test_calculateNumIterationsAsLong_helper2(1, Long.MAX_VALUE, 2,
            (Long.MAX_VALUE / 2) + 1);


      // neg odd pos
      test_calculateNumIterationsAsLong_helper(-5, 10, 1);
      test_calculateNumIterationsAsLong_helper(-5, 10, 2);
      test_calculateNumIterationsAsLong_helper(-5, 10, 3);

      test_calculateNumIterationsAsLong_helper(-5, 11, 1);
      test_calculateNumIterationsAsLong_helper(-5, 11, 2);
      test_calculateNumIterationsAsLong_helper(-5, 11, 3);

      test_calculateNumIterationsAsLong_helper(-5, 12, 1);
      test_calculateNumIterationsAsLong_helper(-5, 12, 2);
      test_calculateNumIterationsAsLong_helper(-5, 12, 3);

      // -1 to largest value possible by 1 and 2
      test_calculateNumIterationsAsLong_helper2(-1, Long.MAX_VALUE - 2, 1,
            Long.MAX_VALUE);
      test_calculateNumIterationsAsLong_helper2(-1, Long.MAX_VALUE - 2, 2,
            (Long.MAX_VALUE / 2) + 1);

      // -1 to largest value possible by 1 and 2
      test_calculateNumIterationsAsLong_helper2(-2, Long.MAX_VALUE - 4, 1,
            Long.MAX_VALUE - 1);
      test_calculateNumIterationsAsLong_helper2(-2, Long.MAX_VALUE - 4, 2,
            (Long.MAX_VALUE - 1) / 2);

      // middle to middle
      test_calculateNumIterationsAsLong_helper2(Long.MIN_VALUE / 2,
            Long.MAX_VALUE / 2 - 1, 1,
            Long.MAX_VALUE);

      // as small as possible to 1
      test_calculateNumIterationsAsLong_helper2(Long.MIN_VALUE + 3,
            1, 1,
            Long.MAX_VALUE);
      test_calculateNumIterationsAsLong_helper2(Long.MIN_VALUE + 4,
            1, 1,
            Long.MAX_VALUE - 1);

      // as small as possible to 1 by 2
      test_calculateNumIterationsAsLong_helper2(Long.MIN_VALUE + 3,
            1, 2,
            (Long.MAX_VALUE / 2) + 1);
      test_calculateNumIterationsAsLong_helper2(Long.MIN_VALUE + 4,
            1, 2,
            (Long.MAX_VALUE - 1) / 2);

      // neg even pos
      test_calculateNumIterationsAsLong_helper(-6, 10, 1);
      test_calculateNumIterationsAsLong_helper(-6, 10, 2);
      test_calculateNumIterationsAsLong_helper(-6, 10, 3);

      test_calculateNumIterationsAsLong_helper(-6, 11, 1);
      test_calculateNumIterationsAsLong_helper(-6, 11, 2);
      test_calculateNumIterationsAsLong_helper(-6, 11, 3);

      test_calculateNumIterationsAsLong_helper(-6, 12, 1);
      test_calculateNumIterationsAsLong_helper(-6, 12, 2);
      test_calculateNumIterationsAsLong_helper(-6, 12, 3);


      // odd neg neg
      test_calculateNumIterationsAsLong_helper(-27, -14, 1);
      test_calculateNumIterationsAsLong_helper(-27, -14, 2);
      test_calculateNumIterationsAsLong_helper(-27, -14, 3);

      test_calculateNumIterationsAsLong_helper(-27, -14, 1);
      test_calculateNumIterationsAsLong_helper(-27, -14, 2);
      test_calculateNumIterationsAsLong_helper(-27, -14, 3);

      test_calculateNumIterationsAsLong_helper(-27, -14, 1);
      test_calculateNumIterationsAsLong_helper(-27, -14, 2);
      test_calculateNumIterationsAsLong_helper(-27, -14, 3);


      // even neg neg
      test_calculateNumIterationsAsLong_helper(-30, -14, 1);
      test_calculateNumIterationsAsLong_helper(-30, -14, 2);
      test_calculateNumIterationsAsLong_helper(-30, -14, 3);

      test_calculateNumIterationsAsLong_helper(-30, -14, 1);
      test_calculateNumIterationsAsLong_helper(-30, -14, 2);
      test_calculateNumIterationsAsLong_helper(-30, -14, 3);

      test_calculateNumIterationsAsLong_helper(-30, -14, 1);
      test_calculateNumIterationsAsLong_helper(-30, -14, 2);
      test_calculateNumIterationsAsLong_helper(-30, -14, 3);


      // min value to largest value possible by 1 and 2
      test_calculateNumIterationsAsLong_helper2(Long.MIN_VALUE, -2, 1,
            Long.MAX_VALUE);
      test_calculateNumIterationsAsLong_helper2(-1, Long.MAX_VALUE - 2, 2,
            (Long.MAX_VALUE / 2) + 1);


      // misc
      test_calculateNumIterationsAsLong_helper(Long.MAX_VALUE - 100,
            Long.MAX_VALUE, 2);


      validateFailure(10, 5, 3); // stop < start
      validateFailure(5, 10, 0); // invalid step
      validateFailure(5, 10, -1); // invalid step

      // invalid range
      validateFailure(0, Long.MAX_VALUE, 10);
      validateFailure(-1, Long.MAX_VALUE - 1, 10);
      validateFailure(-1000000, Long.MAX_VALUE - 1, 10);
      validateFailure(Long.MIN_VALUE / 2, Long.MAX_VALUE / 2, 10);
      validateFailure(Long.MIN_VALUE, -1, 10);
      validateFailure(Long.MIN_VALUE, 0, 10);
      validateFailure(Long.MIN_VALUE, 1, 10);
      validateFailure(Long.MIN_VALUE, Long.MAX_VALUE, 10);
   }

   //
   // calculateNumInterationsAsInt
   //

   @Test
   public void calculateNumIterationsAsInt_callsLongVersion_convertsToInt() throws Throwable {
      spy(IntegerListGenerator.class);
      int start = 10;
      int stop = 20;
      int step = 5;
      long answer = 3434;

      PowerMockito.doReturn(answer).when(IntegerListGenerator.class);
      IntegerListGenerator.calculateNumIterationsAsLong(start, stop, step);

      int observed = IntegerListGenerator.calculateNumIterationsAsInt(start, stop, step);
      assertEquals((int) answer, observed);

      verifyStatic();
      IntegerListGenerator.calculateNumIterationsAsLong(start, stop, step);
   }

   @Test
   public void calculateNumIterationsAsInt_castsSafely() throws Throwable {
      mockStatic(RangeTests.class);
      spy(IntegerListGenerator.class);
      int start = 10;
      int stop = 20;
      int step = 5;
      long answer = 3434;
      int ianswer = 27;

      when(IntegerListGenerator.calculateNumIterationsAsLong(start, stop, step)).thenReturn(answer);
      when(RangeTests.inIntegerRange(answer)).thenReturn(true);
      when(RangeTests.toInt(answer)).thenReturn(ianswer);


      int observed = IntegerListGenerator.calculateNumIterationsAsInt(start, stop, step);
      assertEquals(ianswer, observed);

      verifyStatic();
      RangeTests.toInt(answer);
   }

   @Test(expected= IllegalArgumentException.class)
   public void calculateNumIterationsAsInt_throwsExceptionIfResultIsLong() throws Throwable {
      IntegerListGenerator.calculateNumIterationsAsInt(Integer.MIN_VALUE, 10, 1);
   }

   @Test
   public void testConversions() throws Throwable {
      final long[] values = {Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE};
      IntegerListGenerator ilg = new IntegerListGenerator() {
         @Override
         public long[] generateLongArray(long a, long b, long c) {
            return values;
         }
      };

      IntegerListGeneratorTestBase.testConversions(ilg, 1L, 2L, 3L, values);
   }
} // end class