/**
 * Copyright (c) Zachary Kurmas 2011
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
package edu.gvsu.kurmasz.warszawa.log;


import edu.gvsu.kurmasz.warszawa.io.OutputHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * @author Zachary Kurmas
 */
// Created  10/14/11 at 5:41 PM
// (C) Zachary Kurmas 2011


@RunWith(PowerMockRunner.class)
@PrepareForTest({OutputHelper.class, Log.class})
public class LogTest extends SimpleLogTest {

   protected Log make() {
      return new Log();
   }

   protected Log make(PrintWriter log, int threshold) {
      return new Log(log, threshold);
   }

   protected Log make(String filename, int threshold) throws FileNotFoundException {
      return new Log(filename, threshold);
   }

   protected Log make(OutputStream stream, int threshold) {
      return new Log(stream, threshold);
   }

   //
   // Test constructor (String, int)
   //
   @Test
   public void filename_constructor_setsWriterToFile() throws Throwable {
      String filename = "theFile";
      PrintWriter writer = mock(PrintWriter.class);
      mockStatic(OutputHelper.class);
      when(OutputHelper.openWriter(filename, true)).thenReturn(writer);
      Log log = make(filename, 100);
      assertEquals(writer, log.getWriter());

      verifyStatic();
      OutputHelper.openWriter(filename, true);
   }

   @Test
   public void filename_constructorSetsOutputToNullIfEmptyString() throws Throwable {
      Log log = make("", 12);
      assertNull(log.getWriter());
      PowerMockito.verifyNoMoreInteractions(OutputHelper.class);
   }

   @Test
   public void filename_constructorSetsOutputToNullIfNullString() throws Throwable {
      Log log = make((String) null, 12);
      assertNull(log.getWriter());
      PowerMockito.verifyNoMoreInteractions(OutputHelper.class);
   }

   @Test(expected = FileNotFoundException.class)
   public void filename_constructorThrowsFileNotFoundException() throws Throwable {
      mockStatic(OutputHelper.class);
      String filename = "somePig";
      when(OutputHelper.openWriter(filename, true)).thenThrow(new FileNotFoundException());
      make(filename, 12);
   }

   @Test
   public void filename_constructorSetsThreshold() throws Throwable {
      Log log = make((String) null, 65);
      assertEquals(65, log.getThreshold());
   }

   //
   // Test constructor (OutputStream, int)
   //
   @Test
   public void stream_constructor_setsWriterToStream() throws Throwable {
      OutputStream stream = mock(OutputStream.class);
      PrintWriter writer = mock(PrintWriter.class);
      mockStatic(OutputHelper.class);
      when(OutputHelper.openWriter(stream, true)).thenReturn(writer);
      Log log = make(stream, 100);
      assertEquals(writer, log.getWriter());

      verifyStatic();
      OutputHelper.openWriter(stream, true);
   }

   @Test
   public void stream_constructorSetsThreshold() throws Throwable {
      OutputStream stream = mock(OutputStream.class);
      mockStatic(OutputHelper.class);
      when(OutputHelper.openWriter(stream, true)).thenReturn(null);
      Log log = make((OutputStream) null, 65);
      assertEquals(65, log.getThreshold());
   }


   //
   //  Helpers to tests for setOutput and configure
   //
   private interface Opener {
      PrintWriter openInner(String filename) throws FileNotFoundException;

      void openOuter(Log log, String filename) throws FileNotFoundException;
   }

   private void testSetsOutputToNamedFile(Opener opener) throws Throwable {
      mockStatic(OutputHelper.class);
      String filename = "someFile";
      PrintWriter writer = mock(PrintWriter.class);

      when(opener.openInner(filename)).thenReturn(writer);

      Log log = make(mock(PrintWriter.class), 50);
      opener.openOuter(log, filename);
      assertEquals(writer, log.getWriter());

      verifyStatic();
      opener.openInner(filename);
   }

   private void testSetsOutputToNullIfEmptyString(Opener opener) throws Throwable {
      Log log = make(mock(PrintWriter.class), 50);
      opener.openOuter(log, "");
      assertNull(log.getWriter());
      PowerMockito.verifyNoMoreInteractions(OutputHelper.class);
   }

   public void testSetsOutputToNullIfNullString(Opener opener) throws Throwable {
      Log log = make(mock(PrintWriter.class), 50);
      opener.openOuter(log, null);
      assertNull(log.getWriter());
      PowerMockito.verifyNoMoreInteractions(OutputHelper.class);
   }

   public void testThrowsFileNotFoundException(Opener opener) throws Throwable {
      mockStatic(OutputHelper.class);
      String filename = "somePig";
      when(opener.openInner(filename)).thenThrow(new FileNotFoundException());
      Log log = make(mock(PrintWriter.class), 50);
      opener.openOuter(log, filename);
   }

   //
   //  Test constructor (String, int)
   //


   //
   // Test setOutput (String)
   //
   Opener setOutput_String = new Opener() {
      public PrintWriter openInner(String filename) throws FileNotFoundException {
         return OutputHelper.openWriter(filename, true);
      }

      public void openOuter(Log log, String filename) throws FileNotFoundException {
         log.setOutput(filename);
      }
   };

   @Test
   public void setOutput_String_setsOutputToNamedFile() throws Throwable {
      testSetsOutputToNamedFile(setOutput_String);
   }

   @Test
   public void setOutput_String_setsOutputToNullIfEmptyString() throws Throwable {
      testSetsOutputToNullIfEmptyString(setOutput_String);
   }

   @Test
   public void setOutput_String_setsOutputToNullIfNullString() throws Throwable {
      testSetsOutputToNullIfNullString(setOutput_String);
   }

   @Test(expected = FileNotFoundException.class)
   public void setOutput_string_throwsFileNotFoundException() throws Throwable {
      testThrowsFileNotFoundException(setOutput_String);
   }

   //
   // Test setOutputOrQuit (String_stream_int)
   //

   Opener setOutputOrQuit_string_stream_int = new Opener() {
      private PrintStream error = mock(PrintStream.class);
      private int exit_value = 43093;

      public PrintWriter openInner(String filename) throws FileNotFoundException {
         return OutputHelper.openWriterOrQuit(filename, true, error, exit_value);
      }

      public void openOuter(Log log, String filename) throws FileNotFoundException {
         log.setOutputOrQuit(filename, error, exit_value);
      }
   };

   @Test
   public void setOutputOrQuit_string_stream_int_setsOutputToNamedFile() throws Throwable {
      testSetsOutputToNamedFile(setOutputOrQuit_string_stream_int);
   }

   @Test
   public void setOutputOrQuit_string_stream_int_setsOutputToNullIfEmptyString() throws Throwable {
      testSetsOutputToNullIfEmptyString(setOutputOrQuit_string_stream_int);
   }

   @Test
   public void setOutputOrQuit_string_stream_int_setsOutputToNullIfNullString() throws Throwable {
      testSetsOutputToNullIfNullString(setOutputOrQuit_string_stream_int);
   }

   //
   // Test setOutputOrQuit (String)
   //

   Opener setOutputOrQuit_String = new Opener() {
      private PrintStream error = OutputHelper.DEFAULT_ERROR_STREAM;
      private int exit_value = OutputHelper.DEFAULT_EXIT_VALUE;

      public PrintWriter openInner(String filename) throws FileNotFoundException {
         return OutputHelper.openWriterOrQuit(filename, true, error, exit_value);
      }

      public void openOuter(Log log, String filename) throws FileNotFoundException {
         log.setOutputOrQuit(filename);
      }
   };

   @Test
   public void setOutputOrQuit_String_setsOutputToNamedFile() throws Throwable {
      testSetsOutputToNamedFile(setOutputOrQuit_String);
   }

   @Test
   public void setOutputOrQuit_String_setsOutputToNullIfEmptyString() throws Throwable {
      testSetsOutputToNullIfEmptyString(setOutputOrQuit_String);
   }

   @Test
   public void setOutputOrQuit_String_setsOutputToNullIfNullString() throws Throwable {
      testSetsOutputToNullIfNullString(setOutputOrQuit_String);
   }

   //
   // Test configure (String, int)
   //

   Opener configure_string_int = new Opener() {
      public PrintWriter openInner(String filename) throws FileNotFoundException {
         return OutputHelper.openWriter(filename, true);
      }

      public void openOuter(Log log, String filename) throws FileNotFoundException {
         log.configure(filename, 44483);
      }
   };

   @Test
   public void configure_setsOutputToNamedFile() throws Throwable {
      testSetsOutputToNamedFile(configure_string_int);
   }

   @Test
   public void configure_setsOutputToNullIfEmptyString() throws Throwable {
      testSetsOutputToNullIfEmptyString(configure_string_int);
   }

   @Test
   public void configure_setsOutputToNullIfNullString() throws Throwable {
      testSetsOutputToNullIfNullString(configure_string_int);
   }

   @Test(expected = FileNotFoundException.class)
   public void configure_throwsFileNotFoundException() throws Throwable {
      testThrowsFileNotFoundException(configure_string_int);
   }

   @Test
   public void configure_String_setsThreshold() throws Throwable {
      mockStatic(OutputHelper.class);
      Log log = make(mock(PrintWriter.class), 50);
      log.configure("someFile", 27);
      assertEquals(27, log.getThreshold());
   }

   @Test
   public void configure_String_setsThresholdWithNullString() throws Throwable {
      mockStatic(OutputHelper.class);
      Log log = make(mock(PrintWriter.class), 50);
      log.configure((String) null, 29);
      assertEquals(29, log.getThreshold());
   }

   //
   // Test configureOrQuit (String, int, Stream, int)
   //

   Opener configureOrQuit_string_int_stream_int = new Opener() {
      private PrintStream error = mock(PrintStream.class);
      private int exit_value = 43093;

      public PrintWriter openInner(String filename) throws FileNotFoundException {
         return OutputHelper.openWriterOrQuit(filename, true, error, exit_value);
      }

      public void openOuter(Log log, String filename) throws FileNotFoundException {
         log.configureOrQuit(filename, 44483, error, exit_value);
      }
   };

   @Test
   public void configureOrQuit_setsOutputToNamedFile() throws Throwable {
      testSetsOutputToNamedFile(configureOrQuit_string_int_stream_int);
   }

   @Test
   public void configureOrQuit_setsOutputToNullIfEmptyString() throws Throwable {
      testSetsOutputToNullIfEmptyString(configureOrQuit_string_int_stream_int);
   }

   @Test
   public void configureOrQuit_setsOutputToNullIfNullString() throws Throwable {
      testSetsOutputToNullIfNullString(configureOrQuit_string_int_stream_int);
   }

   @Test
   public void configureOrQuit_String_setsThreshold() throws Throwable {
      mockStatic(OutputHelper.class);
      Log log = make(mock(PrintWriter.class), 50);
      log.configureOrQuit("someFile", 27, mock(PrintStream.class), 34332);
      assertEquals(27, log.getThreshold());
   }

   @Test
   public void configureOrQuit_String_setsThresholdWithNullString() throws Throwable {
      mockStatic(OutputHelper.class);
      Log log = make(mock(PrintWriter.class), 50);
      log.configureOrQuit((String) null, 29, mock(PrintStream.class), 34342);
      assertEquals(29, log.getThreshold());
   }

   //
   // Test configureOrQuit (String, int, Stream, int)
   //

   Opener configureOrQuit_string_int = new Opener() {
      private PrintStream error = OutputHelper.DEFAULT_ERROR_STREAM;
      private int exit_value = OutputHelper.DEFAULT_EXIT_VALUE;

      public PrintWriter openInner(String filename) throws FileNotFoundException {
         return OutputHelper.openWriterOrQuit(filename, true, error, exit_value);
      }

      public void openOuter(Log log, String filename) throws FileNotFoundException {
         log.configureOrQuit(filename, 44483);
      }
   };

   @Test
   public void configureOrQuit_default_setsOutputToNamedFile() throws Throwable {
      testSetsOutputToNamedFile(configureOrQuit_string_int);
   }

   @Test
   public void configureOrQuit_default_setsOutputToNullIfEmptyString() throws Throwable {
      testSetsOutputToNullIfEmptyString(configureOrQuit_string_int);
   }

   @Test
   public void configureOrQuit_default_setsOutputToNullIfNullString() throws Throwable {
      testSetsOutputToNullIfNullString(configureOrQuit_string_int);
   }

   @Test
   public void configureOrQuit_default_String_setsThreshold() throws Throwable {
      mockStatic(OutputHelper.class);
      Log log = make(mock(PrintWriter.class), 50);
      log.configureOrQuit("someFile", 27);
      assertEquals(27, log.getThreshold());
   }

   @Test
   public void configureOrQuit_default_String_setsThresholdWithNullString() throws Throwable {
      mockStatic(OutputHelper.class);
      Log log = make(mock(PrintWriter.class), 50);
      log.configureOrQuit((String) null, 29);
      assertEquals(29, log.getThreshold());
   }

   //
   // makeLogOrQuit
   //
   @Test
   public void makeLogOrQuitCreatesLog() throws Throwable {
      Log expectedLog = mock(Log.class);
      spy(Log.class);
      PowerMockito.whenNew(Log.class).withNoArguments().thenReturn(expectedLog);
      assertEquals(expectedLog, Log.makeLogOrQuit("", 34));
   }

   @Test
   public void makeLogOrQuitConfiguresLog() throws Throwable {
      String filename = "fn";
      int threshold = 17;

      PrintStream errorStream = mock(PrintStream.class);
      int errorValue = 9934;

      Log expectedLog = mock(Log.class);
      spy(Log.class);
      PowerMockito.whenNew(Log.class).withNoArguments().thenReturn(expectedLog);
      Log.makeLogOrQuit(filename, threshold, errorStream, errorValue);
      verify(expectedLog).configureOrQuit(filename, threshold, errorStream, errorValue);
   }

   @Test
   public void makeLogOrQuitWithDefaults() throws Throwable {
      String filename = "OnceInAwhile";
      int threshold = 88349;
      Log expected = mock(Log.class);
      spy(Log.class);
      doReturn(expected).when(Log.class);
      Log.makeLogOrQuit(filename, threshold, Log.DEFAULT_ERROR_STREAM, Log.DEFAULT_EXIT_VALUE);
      Log observed = Log.makeLogOrQuit(filename, threshold);
      assertEquals(expected, observed);

      verifyStatic();
      Log.makeLogOrQuit(filename, threshold, Log.DEFAULT_ERROR_STREAM, Log.DEFAULT_EXIT_VALUE);
   }

   //
   // setOutput  (OutputStream)
   //
   @Test
   public void testSetOutput_OutputStream() throws Throwable {
      OutputStream stream = mock(OutputStream.class);
      PrintWriter expected = mock(PrintWriter.class);
      Log log = make();

      mockStatic(OutputHelper.class);
      when(OutputHelper.openWriter(stream, true)).thenReturn(expected);
      log.setOutput(stream);
      assertEquals(expected, log.getWriter());
      verifyStatic();
      OutputHelper.openWriter(stream, true);
   }

   //
   // setConfigure (OutputStream int)
   //
   @Test
   public void configure_OutputStream_setsStream() throws Throwable {
      OutputStream stream = mock(OutputStream.class);
      PrintWriter expected = mock(PrintWriter.class);
      Log log = make();

      mockStatic(OutputHelper.class);
      when(OutputHelper.openWriter(stream, true)).thenReturn(expected);
      log.configure(stream, 17);
      assertEquals(expected, log.getWriter());
      verifyStatic();
      OutputHelper.openWriter(stream, true);
   }

   @Test
   public void configure_OutputStream_setsThreshold() throws Throwable {
      int threshold = 8983;
      OutputStream stream = mock(OutputStream.class);
      Log log = make();
      log.setThreshold(threshold + 1);

      mockStatic(OutputHelper.class);
      log.configure(stream, threshold);
      assertEquals(threshold, log.getThreshold());
   }


   //
   // Test that the whole thing works
   //
   interface Opener2 {
      Log open(String filename, int threshold) throws FileNotFoundException;
   }

   private void verify_logWritesToFile(Opener2 opener) throws Throwable {
      File file = File.createTempFile("Log", "lt");
      BufferedReader input = null;
      Log log = null;
      try {
         log = opener.open(file.getAbsolutePath(), 50);
         log.println(49, "Line 1");
         log.println(50, "Line 2");
         log.println(51, "Line 3");
         log.println(48, "Line 4");

         // Note:  Since Log sets autoflush to true, we should see
         // all the output, even if the file hasn't been explicitly closed.


         input = new BufferedReader(new FileReader(file));
         assertEquals("Line 2", input.readLine());
         assertEquals("Line 3", input.readLine());
         assertNull(input.readLine());
      } finally {
         log.close();
         input.close();
         assertTrue(file.delete());
      }
   }

   @Test
   public void logWritesToFile_fromStringConstructor() throws Throwable {
      verify_logWritesToFile(new Opener2() {
         public Log open(String filename, int threshold) throws FileNotFoundException {
            return make(filename, threshold);
         }
      });
   }

   @Test
   public void logWritesToFile_fromMakeOrQuit() throws Throwable {
      verify_logWritesToFile(new Opener2() {
         public Log open(String filename, int threshold) throws FileNotFoundException {
            return Log.makeLogOrQuit(filename, threshold, null, 0);
         }
      });
   }

   @Test
   public void logWritesToFile_fromDefaultMakeOrQuit() throws Throwable {
      verify_logWritesToFile(new Opener2() {
         public Log open(String filename, int threshold) throws FileNotFoundException {
            return Log.makeLogOrQuit(filename, threshold);
         }
      });
   }

   @Test
   public void logWritesToFile_fromSetOutput() throws Throwable {
      verify_logWritesToFile(new Opener2() {
         public Log open(String filename, int threshold) throws FileNotFoundException {
            Log log = make();
            log.setOutput(filename);
            log.setThreshold(50);
            return log;
         }
      });
   }

   @Test
   public void logWritesToFile_fromConfigure() throws Throwable {
      verify_logWritesToFile(new Opener2() {
         public Log open(String filename, int threshold) throws FileNotFoundException {
            Log log = make();
            log.configure(filename, threshold);
            return log;
         }
      });
   }

   @Test
   public void logWritesToFileFromStreamConstructor() throws Throwable {
      verify_logWritesToFile(new Opener2() {
         public Log open(String filename, int threshold) throws FileNotFoundException {
            FileOutputStream fos = new FileOutputStream(filename);
            return make(fos, threshold);
         }
      });
   }

   @Test
   public void logWritesToFileFromStreamSetOutput() throws Throwable {
      verify_logWritesToFile(new Opener2() {
         public Log open(String filename, int threshold) throws FileNotFoundException {
            Log log = make();
            FileOutputStream fos = new FileOutputStream(filename);
            log.setOutput(fos);
            log.setThreshold(threshold);
            return log;
         }
      });
   }

   @Test
   public void logWritesToFileFromStreamConfigure() throws Throwable {
      verify_logWritesToFile(new Opener2() {
         public Log open(String filename, int threshold) throws FileNotFoundException {
            Log log = make();
            FileOutputStream fos = new FileOutputStream(filename);
            log.configure(fos, threshold);
            return log;
         }
      });
   }

   //
   //  close
   //
   @Test
   public void callingCloseOnNullDoesNotCrash() throws Throwable {
      Log log = make();
      log.close();
   }

   @Test
   public void callingCloseClosesPrintWriter() throws Throwable {
      PrintWriter pw = mock(PrintWriter.class);
      Log log = make(pw, 155);
      log.close();
      Mockito.verify(pw).close();
   }


}
