package edu.gvsu.kurmasz.warszawa.log;

import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Zachary Kurmas
 */
// Created  10/13/11 at 5:28 PM
// (C) Zachary Kurmas 2011

public class SimpleLogTest {

   protected SimpleLog make() {
      return new SimpleLog();
   }

   protected SimpleLog make(PrintWriter log, int threshold) {
      return new SimpleLog(log, threshold);
   }

   @Test
   public void defaultConstructorSetsLogToNull() throws Throwable {
      SimpleLog sl = make();
      assertNull(sl.getWriter());
   }

   @Test
   public void defaultConstructorSetsThresholdToMaxInt() throws Throwable {
      SimpleLog sl = make();
      assertEquals(Integer.MAX_VALUE, sl.getThreshold());
   }

   @Test
   public void constructorSetsLog() throws Throwable {
      PrintWriter log = mock(PrintWriter.class);
      SimpleLog sl = make(log, 50);
      assertEquals(log, sl.getWriter());
   }

   @Test
   public void constructorSetsThreshold() throws Throwable {
      PrintWriter log = mock(PrintWriter.class);
      SimpleLog sl = make(log, 50);
      assertEquals(50, sl.getThreshold());

      // verify that threshold isn't hard-coded at 50
      sl = make(log, 150);
      assertEquals(150, sl.getThreshold());
   }


   @Test
   public void testWillLogReturnsFalseIfLogIsNull() throws Exception {
      int threshold = 50;
      SimpleLog sl = make(null, threshold);
      assertFalse("level negative", sl.willLog(-1));
      assertFalse("level zero", sl.willLog(0));
      assertFalse("level under", sl.willLog(threshold - 1));
      assertFalse("level at", sl.willLog(threshold));
      assertFalse("level over", sl.willLog(threshold + 1));
      assertFalse("level max", sl.willLog(Integer.MAX_VALUE));
   }

   @Test
   public void testWillLogReturnsFalseIfLevelTooLow() throws Throwable {
      int threshold = 50;
      SimpleLog sl = make(mock(PrintWriter.class), threshold);
      assertFalse("level negative", sl.willLog(-1));
      assertFalse("level zero", sl.willLog(0));
      assertFalse("level too low", sl.willLog(threshold - 1));
   }

   @Test
   public void testWillLogReturnsTrueIfLevelHighEnough() throws Throwable {
      int threshold = 50;
      SimpleLog sl = make(mock(PrintWriter.class), threshold);
      assertTrue("Level at", sl.willLog(threshold));
      assertTrue("Level over", sl.willLog(threshold + 1));
      assertTrue("Level way", sl.willLog(Integer.MAX_VALUE));
   }

   @Test
   public void testPrintln_printsWhenLevelIsAtThreshold() throws Exception {
      int threshold = 50;
      PrintWriter log = mock(PrintWriter.class);
      SimpleLog sl = make(log, threshold);
      String message = "Message";

      sl.println(threshold, message);
      verify(log).println(message);
   }

   @Test
   public void testPrintln_printsWhenLevelIsAboveThreshold() throws Exception {
      int threshold = 50;
      PrintWriter log = mock(PrintWriter.class);
      SimpleLog sl = make(log, threshold);
      String message = "Message";

      sl.println(threshold + 1, message);
      verify(log).println(message);
   }

   @Test
   public void testPrintln_DoesNotPrintsWhenLevelIsBelowThreshold() throws Exception {
      int threshold = 50;
      PrintWriter log = mock(PrintWriter.class);
      SimpleLog sl = make(log, threshold);
      String message = "Message";

      sl.println(threshold - 1, message);
      verify(log, never()).println(message);
   }

   @Test
   public void testPrintln_DoesNotCrashWhenLogIsNull() throws Exception {
      int threshold = 50;
      SimpleLog sl = make(null, threshold);
      String message = "Message";

      sl.println(threshold + 1, message);
   }

   @Test
   public void testSetThreshold() throws Exception {
      int threshold = 50;
      int newThreshold = threshold + 5;
      PrintWriter log = mock(PrintWriter.class);
      SimpleLog sl = make(log, threshold);

      sl.setThreshold(newThreshold);
      assertEquals(newThreshold, sl.getThreshold());
   }

   @Test
   public void testSetThresholdToMax() throws Exception {
      int threshold = 50;
      PrintWriter log = mock(PrintWriter.class);
      SimpleLog sl = make(log, threshold);

      sl.setThresholdToMax();
      assertEquals(Integer.MAX_VALUE, sl.getThreshold());
   }

   @Test
   public void testSetOutput() throws Exception {
      int threshold = 50;
      PrintWriter log1 = mock(PrintWriter.class);
      PrintWriter log2 = mock(PrintWriter.class);
      SimpleLog sl = make(log1, threshold);

      sl.setOutput(log2);
      assertEquals(log2, sl.getWriter());
   }

   @Test
   public void testConfigureSetsOutput() throws Exception {
      int threshold = 50;
      PrintWriter log1 = mock(PrintWriter.class);
      PrintWriter log2 = mock(PrintWriter.class);
      SimpleLog sl = make(log1, threshold);

      sl.configure(log2, threshold);
      assertEquals(log2, sl.getWriter());
   }

   @Test
   public void testConfigureSetsThreshold() throws Exception {
      int threshold = 50;
      int newThreshold = threshold + 5;
      PrintWriter log = mock(PrintWriter.class);
      SimpleLog sl = make(log, threshold);

      sl.configure(log, newThreshold);
      assertEquals(newThreshold, sl.getThreshold());
   }

   @Test
   public void testLogGetsWritten() throws Throwable {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      SimpleLog log = make(pw, 50);
      log.println(49, "Line 1");
      log.println(50, "Line 2");
      log.println(51, "Line 3");
      log.println(48, "Line 4");
      pw.close();
      String observed = sw.toString();


      // Writers replace \n with the appropriate, platform-dependant
      // line ending (e.g., CR on UNIX CRLF on Windows).  Thus,
      // we need to generate our expected string using Writers, otherwise
      // the test will fail on Windows.
      StringWriter ew = new StringWriter();
      PrintWriter pw2 = new PrintWriter(ew);
      pw2.println("Line 2");
      pw2.println("Line 3");
      pw2.close();
      String expected = ew.toString();


      assertEquals(expected, observed);
   }
}
