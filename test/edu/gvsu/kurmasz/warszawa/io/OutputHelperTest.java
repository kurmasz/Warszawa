package edu.gvsu.kurmasz.warszawa.io;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * @author Zachary Kurmas
 */
// Created  9/9/11 at 6:55 PM
// (C) Zachary Kurmas 2011


@RunWith(PowerMockRunner.class)
@PrepareForTest({OutputHelper.class})
public class OutputHelperTest {

   //TODO:  Write a function to verify that the directory doesn't exist
   public static final String FILE_IN_MISSING_DIR = "IHopeThisDirectoryDoesNotExist/someFile";

   ////////////////////////////////////////////////////////////
   //
   // Test helper methods
   //
   ////////////////////////////////////////////////////////////

   @Test
   public void testMakeDefaultOutputStreamMap() throws Exception {
      Map<String, OutputStream> map = OutputHelper.makeDefaultOutputStreamMap();
      assertEquals("Problem with -", System.out, map.get("-"));
      assertEquals("Problem with stdout", System.out, map.get("stdout"));
      assertEquals("Problem with STDOUT", System.out, map.get("STDOUT"));
      assertEquals("Problem with stderr", System.err, map.get("stderr"));
      assertEquals("Problem with stderr", System.err, map.get("STDERR"));
      assertNull(map.get("yourMom"));
   }

   @Test
   public void defaultOutputStreamMap() throws Throwable {
      assertEquals("Wrong default output map", OutputHelper.makeDefaultOutputStreamMap(), OutputHelper.DEFAULT_OUTPUT_STREAM_MAP);
   }

   @Test(expected = UnsupportedOperationException.class)
   public void verifyDefaultOutputStreamMapIsImmutable() throws Throwable {
      OutputHelper.DEFAULT_OUTPUT_STREAM_MAP.put("fred", System.out);
   }

   @Test
   public void testGetOutputStreamReturnsItemFromMap() throws Exception {
      OutputStream fredStream = mock(OutputStream.class);
      Map<String, OutputStream> testMap = new HashMap<String, OutputStream>();
      testMap.put("fred", fredStream);
      assertEquals("Wrong output stream", fredStream, OutputHelper.getOutputStream("fred", testMap));
   }

   @Test
   public void testGetOutputStreamReturnsStdout() throws Throwable {
      assertEquals("-", System.out, OutputHelper.getOutputStream("-", OutputHelper.DEFAULT_OUTPUT_STREAM_MAP));
      assertEquals("stdout", System.out, OutputHelper.getOutputStream("stdout",
            OutputHelper.DEFAULT_OUTPUT_STREAM_MAP));
      assertEquals("STDOUT", System.out, OutputHelper.getOutputStream("STDOUT",
            OutputHelper.DEFAULT_OUTPUT_STREAM_MAP));
   }

   @Test
   public void testGetOutputStreamReturnsStderr() throws Throwable {
      assertEquals("stderr", System.err, OutputHelper.getOutputStream("stderr",
            OutputHelper.DEFAULT_OUTPUT_STREAM_MAP));
      assertEquals("STDERR", System.err, OutputHelper.getOutputStream("STDERR",
            OutputHelper.DEFAULT_OUTPUT_STREAM_MAP));
   }


   private void testGetOutputStreamReturnsNewFileOutputStream(Map<String, OutputStream> map) throws Exception {
      String filename = mock(String.class);
      FileOutputStream expected = mock(FileOutputStream.class);

      spy(OutputHelper.class);
      whenNew(FileOutputStream.class).withArguments(filename).thenReturn(expected);

      OutputStream observed = OutputHelper.getOutputStream(filename, map);
      assertEquals(expected, observed);
   }

   @Test
   public void testGetOutputStreamReturnsNewFileOutputStreamWhenGivenNullMap() throws Throwable {
      testGetOutputStreamReturnsNewFileOutputStream(null);
   }

   @Test
   public void testGetOutputStreamReturnsNewFileOutputStreamWhenNotInMap() throws Throwable {
      testGetOutputStreamReturnsNewFileOutputStream(OutputHelper.DEFAULT_OUTPUT_STREAM_MAP);
   }

   @Test(expected = FileNotFoundException.class)
   public void testGetOutputStreamThrowsExceptionIfFileNotFound() throws Throwable {
      OutputHelper.getOutputStream(FILE_IN_MISSING_DIR, null);
   }

   @Test(expected = FileNotFoundException.class)
   public void testGetOutputStreamThrowsExceptionIfFileNotFound2() throws Throwable {
      OutputHelper.getOutputStream("thisDirectoryDoesntExistIHope_alkjfladllj/wow", OutputHelper.DEFAULT_OUTPUT_STREAM_MAP);
   }


   ////////////////////////////////////////////////////////////
   //
   // Setup used to beta various openWriter methods
   //
   ////////////////////////////////////////////////////////////

   public static void deleteTempFile(File file) {
      if (file == null) {
         return;
      }
      if (file.exists()) {
         assertTrue("Problem deleting temp file: " + file.getAbsolutePath(), file.delete());
      }
   }

   // IMPORTANT:  The purpose of these methods is to call the version of openWriter under beta.
   // Thus it is very important that you don't write any shortcuts here that change the method under beta.
   // For example writing code like openWriter(file, Charset.forName(charset), autoflush) would break the tests.
   // (Specifically, the beta would no longer beta the intended method.)
   public abstract class Opener {
      public abstract PrintWriter openWriter(File file, Charset charset, boolean autoflush) throws IOException;

      public PrintWriter openWriter(File file, Charset charset) throws IOException {
         return openWriter(file, charset, true);
      }

      public PrintWriter openWriter(File file, boolean autoflush) throws IOException {
         return openWriter(file, Charset.defaultCharset(), autoflush);
      }

//      public PrintWriter openWriter(File file) throws IOException {
//         return openWriter(file, Charset.defaultCharset(), true);
//      }
   }


   private void testOpenWriterSetsAutoFlushTrue(Opener opener) throws IOException {
      File temp = File.createTempFile("OutputHelperTest", "");
      PrintWriter writer = opener.openWriter(temp, true);

      writer.println("Hello, World");
      writer.print("How are you today?");

      Scanner input = new Scanner(temp);
      assertEquals("First line", "Hello, World", input.nextLine());
      assertFalse("Shouldn't be any more", input.hasNext());

      input.close();
      writer.close();
      deleteTempFile(temp);
   }

   private void testOpenWriterSetsAutoFlushFalse(Opener opener) throws IOException {
      File temp = File.createTempFile("OutputHelperTest", "");
      PrintWriter writer = opener.openWriter(temp, false);

      writer.println("Hello, World");
      writer.println("Nice day, isn't it?");

      Scanner input = new Scanner(temp);
      if (input.hasNext()) {
         fail("Shouldn't be any more input, but found:  " + input.nextLine());
      }

      input.close();
      writer.close();
      deleteTempFile(temp);
   }

   private enum CharsetTester {
      US_ASCII("US-ASCII", 26), MAC_ROMAN("MacRoman", 26), WINDOWS_1252("windows-1252", 26), UTF_8("UTF-8", 28),
      UTF_16("UTF-16", 54);

      static {
         US_ASCII.expectedBytes = new byte[]{0x44, 0x3f, 0x69, 0x65, 0x6e, 0x20, 0x44, 0x6f, 0x62, 0x72, 0x79, 0x2e,
               0x20, 0x4a, 0x61, 0x6b, 0x20, 0x73, 0x69, 0x3f, 0x20, 0x6d, 0x61, 0x73, 0x7a, 0x3f};

         MAC_ROMAN.expectedBytes = US_ASCII.expectedBytes;

         WINDOWS_1252.expectedBytes = US_ASCII.expectedBytes;

         // -0x3b = 0xc5
         // -0x46 = 0xba
         // -0x3c = 0xc4
         // -0x67 - 0x99
         UTF_8.expectedBytes = new byte[]{0x44, -0x3b, -0x46, 0x69, 0x65, 0x6e, 0x20, 0x44, 0x6f, 0x62, 0x72, 0x79, 0x2e,
               0x20, 0x4a, 0x61, 0x6b, 0x20, 0x73, 0x69, -0x3c, -0x67, 0x20, 0x6d, 0x61, 0x73, 0x7a, 0x3f};

         // -0x2 = 0xfe
         // -0x1 = 0xff
         UTF_16.expectedBytes = new byte[]{-0x2, -0x1, 0x00, 0x44, 0x01, 0x7a, 0x00, 0x69, 0x00, 0x65,
               0x00, 0x6e, 0x00, 0x20, 0x00, 0x44, 0x00, 0x6f, 0x00, 0x62, 0x00, 0x72, 0x00, 0x79, 0x00, 0x2e, 0x00,
               0x20, 0x00, 0x4a, 0x00, 0x61, 0x00, 0x6b, 0x00, 0x20, 0x00, 0x73, 0x00, 0x69, 0x01, 0x19, 0x00, 0x20,
               0x00, 0x6d, 0x00, 0x61, 0x00, 0x73, 0x00, 0x7a, 0x00, 0x3f};
      }

      // Avoiding \n avoids the cr/crlf difference
      private static final String message = "Dźien Dobry. Jak się masz?";
      //private String charsetName;
      private Charset charset;
      private int expectedLength;
      byte[] expectedBytes;

      CharsetTester(String name_in, int length_in) {
         //charsetName = name_in;
         charset = Charset.forName(name_in);
         expectedLength = length_in;
      }

      public void testOpenWriterSetsEncoding(Opener opener) throws IOException {
         File file = File.createTempFile("OutputHelperTest", this.toString());
         //System.out.println("File name: " + file.getAbsolutePath());
         PrintWriter p1 = opener.openWriter(file, charset);
         // Using print instead of println avoids the cr/crlf difference
         p1.print(message);
         p1.close();

         assertEquals("Wrong length for " + this.toString(), expectedLength, file.length());

         FileInputStream input = new FileInputStream(file);
         byte[] observed = new byte[expectedBytes.length];
         int numRead = input.read(observed);
         assertEquals("Wrong number of bytes for " + file.getName(), expectedBytes.length, numRead);
         assertArrayEquals("Differences for " + file.getName(), observed, expectedBytes);
         assertEquals("Too much data in " + file.getName(), -1, input.read());
         input.close();
         deleteTempFile(file);
      }

      public static void testOpenWriterSetsAllEncodings(Opener opener) throws IOException {
         for (CharsetTester cst : values()) {
            cst.testOpenWriterSetsEncoding(opener);

         }
      }

      public static CharsetTester forDefaultCharset() {
         Charset defaultCharset = Charset.defaultCharset();
         for (CharsetTester cst : values()) {
            if (cst.charset.equals(defaultCharset)) {
               return cst;
            }
         }
         fail("Default charset (" + defaultCharset.displayName() + ") is not used by charsetTester");
         return null;
      }

   }

   ////////////////////////////////////////////////////////////
   //
   // test OpenWriter(OutputStream, Charset, boolean)
   //
   ////////////////////////////////////////////////////////////

   @Test
   public void testOpenWriter_OutputStream_charset_autoflush() throws Throwable {
      Charset charset = mock(Charset.class);
      OutputStream output = mock(OutputStream.class);
      OutputStreamWriter osw = mock(OutputStreamWriter.class);
      BufferedWriter bw = mock(BufferedWriter.class);
      PrintWriter expected = mock(PrintWriter.class);

      spy(OutputHelper.class);
      whenNew(OutputStreamWriter.class).withArguments(output, charset).thenReturn(osw);
      whenNew(BufferedWriter.class).withArguments(osw).thenReturn(bw);
      whenNew(PrintWriter.class).withArguments(bw, true).thenReturn(expected);

      PrintWriter observed = OutputHelper.openWriter(output, charset, true);
      assertEquals(expected, observed);

      // Calling verifyStatic() is not necessary in this case, because if the
      // expected methods are not called, then calling "new" will return null
      // causing the above assertEquals to fail.
   }

   Opener openOutputStream = new Opener() {
      public PrintWriter openWriter(File file, Charset charset, boolean autoflush) throws IOException {
         OutputStream out = new FileOutputStream(file);
         return OutputHelper.openWriter(out, charset, autoflush);
      }
   };

   @Test
   public void testOpenWriter_OutputStream_SetsAutoFlushTrue() throws Throwable {
      testOpenWriterSetsAutoFlushTrue(openOutputStream);
   }

   @Test
   public void testOpenWriter_OutputStream_SetsAutoFlushFalse() throws Throwable {
      testOpenWriterSetsAutoFlushFalse(openOutputStream);
   }

   @Test
   public void testOpenWriter_OutputStream_setsEncoding() throws Throwable {
      CharsetTester.testOpenWriterSetsAllEncodings(openOutputStream);
   }


   ////////////////////////////////////////////////////////////
   //
   // test OpenWriter(OutputStream, boolean)
   //
   ////////////////////////////////////////////////////////////

   @Test
   public void testOpenWriter_OutputStream_autoflush() throws Throwable {
      OutputStream stream = mock(OutputStream.class);
      Charset charset = mock(Charset.class);
      mockStatic(Charset.class);
      when(Charset.defaultCharset()).thenReturn(charset);

      PrintWriter expected = mock(PrintWriter.class);

      spy(OutputHelper.class);
      doReturn(expected).when(OutputHelper.class);
      OutputHelper.openWriter(stream, charset, true);

      PrintWriter observed = OutputHelper.openWriter(stream, true);
      assertEquals(expected, observed);

      verifyStatic();
      OutputHelper.openWriter(stream, charset, true);
   }

   Opener openOutputStream_boolean = new Opener() {
      public PrintWriter openWriter(File file, Charset charset, boolean autoflush) throws IOException {
         OutputStream out = new FileOutputStream(file);
         return OutputHelper.openWriter(out, autoflush);
      }
   };

   @Test
   public void testOpenWriter_OutputStream_boolean_SetsAutoFlushTrue() throws Throwable {
      testOpenWriterSetsAutoFlushTrue(openOutputStream_boolean);
   }

   @Test
   public void testOpenWriter_OutputStream_boolean_SetsAutoFlushFalse() throws Throwable {
      testOpenWriterSetsAutoFlushFalse(openOutputStream_boolean);
   }

   ////////////////////////////////////////////////////////////
   //
   // test OpenWriter(File, Charset, boolean)
   //
   ////////////////////////////////////////////////////////////

   @Test
   public void testOpenWriter_file_charset_autoflush() throws Throwable {
      Charset charset = mock(Charset.class);
      PrintWriter expected = mock(PrintWriter.class);
      File file = mock(File.class);
      FileOutputStream fos = mock(FileOutputStream.class);

      spy(OutputHelper.class);
      whenNew(FileOutputStream.class).withArguments(file).thenReturn(fos);
      PowerMockito.doReturn(expected).when(OutputHelper.class);
      OutputHelper.openWriter(fos, charset, true);

      PrintWriter observed = OutputHelper.openWriter(file, charset, true);
      assertEquals(expected, observed);

      verifyStatic();
      OutputHelper.openWriter(fos, charset, true);
   }


   Opener openFile_charset_boolean = new Opener() {
      public PrintWriter openWriter(File file, Charset charset, boolean autoflush) throws IOException {
         return OutputHelper.openWriter(file, charset, autoflush);
      }
   };

   @Test
   public void testOpenWriter_file_charset_autoflush_setsAutoflushToTrue() throws Throwable {
      testOpenWriterSetsAutoFlushTrue(openFile_charset_boolean);
   }

   @Test
   public void testOpenWriter_file_charset_autoflush_setsAutoflushToFalse() throws Throwable {
      testOpenWriterSetsAutoFlushFalse(openFile_charset_boolean);
   }

   @Test
   public void testOpenWriter_file_charset_autoflush_setsEncoding() throws Throwable {
      CharsetTester.testOpenWriterSetsAllEncodings(openFile_charset_boolean);
   }


   ////////////////////////////////////////////////////////////
   //
   // test OpenWriter(File, String, boolean)
   //
   ////////////////////////////////////////////////////////////

   @Test(expected = FileNotFoundException.class)
   public void testOpenWriter_File_String_boolean_ThrowsExceptionIfFileNotFound() throws Throwable {
      OutputHelper.openWriter(new File(FILE_IN_MISSING_DIR), Charset.defaultCharset().displayName(), false);
   }

   Opener openFile_string_boolean = new Opener() {
      public PrintWriter openWriter(File file, Charset charset, boolean autoflush) throws IOException {
         return OutputHelper.openWriter(file, charset.displayName(), autoflush);
      }
   };

   @Test
   public void testOpenWriter_file_string_autoflush() throws Throwable {
      String charsetName = mock(String.class);
      Charset charset = mock(Charset.class);
      PrintWriter expected = mock(PrintWriter.class);
      File file = mock(File.class);

      mockStatic(Charset.class);
      when(Charset.forName(charsetName)).thenReturn(charset);

      spy(OutputHelper.class);
      doReturn(expected).when(OutputHelper.class);
      OutputHelper.openWriter(file, charset, true);

      PrintWriter observed = OutputHelper.openWriter(file, charsetName, true);
      assertEquals(expected, observed);

      verifyStatic();
      Charset.forName(charsetName);

      verifyStatic();
      OutputHelper.openWriter(file, charset, true);
   }

   @Test
   public void testOpenWriter_file_string_autoflush_setsAutoflushToTrue() throws Throwable {
      testOpenWriterSetsAutoFlushTrue(openFile_string_boolean);
   }

   @Test
   public void testOpenWriter_file_string_autoflush_setsAutoflushToFalse() throws Throwable {
      testOpenWriterSetsAutoFlushFalse(openFile_string_boolean);
   }

   @Test
   public void testOpenWriter_file_string_autoflush_setsEncodings() throws Throwable {
      CharsetTester.testOpenWriterSetsAllEncodings(openFile_string_boolean);
   }

   ////////////////////////////////////////////////////////////
   //
   // beta OpenWriter(File, boolean)
   //
   ////////////////////////////////////////////////////////////

   @Test(expected = FileNotFoundException.class)
   public void testOpenWriter_File_boolean_ThrowsExceptionIfFileNotFound() throws Throwable {
      OutputHelper.openWriter(new File(FILE_IN_MISSING_DIR), false);
   }


   Opener openFile_boolean = new Opener() {
      public PrintWriter openWriter(File file, Charset charset, boolean autoflush) throws IOException {
         return OutputHelper.openWriter(file, autoflush);
      }
   };

   @Test
   public void testOpenWriter_file_autoflush() throws Throwable {
      Charset charset = mock(Charset.class);
      PrintWriter expected = mock(PrintWriter.class);
      File file = mock(File.class);

      mockStatic(Charset.class);
      when(Charset.defaultCharset()).thenReturn(charset);

      spy(OutputHelper.class);
      doReturn(expected).when(OutputHelper.class);
      OutputHelper.openWriter(file, charset, true);

      PrintWriter observed = OutputHelper.openWriter(file, true);
      assertEquals(expected, observed);

      verifyStatic();
      Charset.defaultCharset();

      verifyStatic();
      OutputHelper.openWriter(file, charset, true);
   }

   @Test
   public void testOpenWriter_file_autoflush_setsAutoflushToTrue() throws Throwable {
      testOpenWriterSetsAutoFlushTrue(openFile_boolean);
   }

   @Test
   public void testOpenWriter_file_autoflush_setsAutoflushToFalse() throws Throwable {
      testOpenWriterSetsAutoFlushFalse(openFile_boolean);
   }

   @Test
   public void testOpenWriter_file_autoflush_setsEncodings() throws Throwable {
      CharsetTester.forDefaultCharset().testOpenWriterSetsEncoding(openFile_boolean);
   }

   ////////////////////////////////////////////////////////////
   //
   // beta OpenWriter(File, String)
   //
   ////////////////////////////////////////////////////////////

   @Test(expected = FileNotFoundException.class)
   public void testOpenWriter_File_String_ThrowsExceptionIfFileNotFound() throws Throwable {
      OutputHelper.openWriter(new File(FILE_IN_MISSING_DIR), false);
   }

   @Test
   public void testOpenWriter_file_string() throws Throwable {
      String charsetName = mock(String.class);
      PrintWriter expected = mock(PrintWriter.class);
      File file = mock(File.class);

      spy(OutputHelper.class);
      doReturn(expected).when(OutputHelper.class);
      OutputHelper.openWriter(file, charsetName, false);

      PrintWriter observed = OutputHelper.openWriter(file, charsetName);
      assertEquals(expected, observed);

      verifyStatic();
      OutputHelper.openWriter(file, charsetName, false);
   }

   Opener openFile_String = new Opener() {
      public PrintWriter openWriter(File file, Charset charset, boolean autoflush) throws IOException {
         return OutputHelper.openWriter(file, charset.displayName());
      }
   };

   @Test
   public void testOpenWriter_file_string_setsAutoflushToFalse() throws Throwable {
      testOpenWriterSetsAutoFlushFalse(openFile_String);
   }

   @Test
   public void testOpenWriter_file_string_setsEncodings() throws Throwable {
      CharsetTester.testOpenWriterSetsAllEncodings(openFile_String);
   }

   ////////////////////////////////////////////////////////////
   //
   // beta OpenWriter(filename, Charset, boolean)
   //
   ////////////////////////////////////////////////////////////

   @Test(expected = FileNotFoundException.class)
   public void testOpenWriter_Filename_charset_boolean_ThrowsExceptionIfFileNotFound() throws Throwable {
      OutputHelper.openWriter(FILE_IN_MISSING_DIR, Charset.defaultCharset(), false);
   }

   @Test
   public void testOpenWriter_filename_charset_autoflush() throws Throwable {
      Charset charset = mock(Charset.class);
      PrintWriter expected = mock(PrintWriter.class);
      String filename = mock(String.class);
      File file = mock(File.class);

      mockStatic(File.class);
      whenNew(File.class).withArguments(filename).thenReturn(file);


      spy(OutputHelper.class);
      doReturn(expected).when(OutputHelper.class);
      OutputHelper.openWriter(file, charset, true);

      PrintWriter observed = OutputHelper.openWriter(filename, charset, true);
      assertEquals(expected, observed);

      verifyStatic();
      OutputHelper.openWriter(filename, charset, true);
   }


   Opener openFileName_charset_boolean = new Opener() {
      public PrintWriter openWriter(File file, Charset charset, boolean autoflush) throws IOException {
         return OutputHelper.openWriter(file.getAbsolutePath(), charset, autoflush);
      }
   };

   @Test
   public void testOpenWriter_filename_charset_autoflush_setsAutoflushToTrue() throws Throwable {
      testOpenWriterSetsAutoFlushTrue(openFileName_charset_boolean);
   }

   @Test
   public void testOpenWriter_filename_charset_autoflush_setsAutoflushToFalse() throws Throwable {
      testOpenWriterSetsAutoFlushFalse(openFileName_charset_boolean);
   }

   @Test
   public void testOpenWriter_filename_charset_autoflush_setsEncodings() throws Throwable {
      CharsetTester.testOpenWriterSetsAllEncodings(openFileName_charset_boolean);
   }


   ////////////////////////////////////////////////////////////
   //
   // beta OpenWriter(Filename, String, boolean)
   //
   ////////////////////////////////////////////////////////////

   @Test(expected = FileNotFoundException.class)
   public void testOpenWriter_Filename_string_boolean_ThrowsExceptionIfFileNotFound() throws Throwable {
      OutputHelper.openWriter(FILE_IN_MISSING_DIR, Charset.defaultCharset().displayName(), false);
   }

   @Test
   public void testOpenWriter_filename_string_autoflush() throws Throwable {
      String charsetName = mock(String.class);
      Charset charset = mock(Charset.class);
      PrintWriter expected = mock(PrintWriter.class);
      String filename = mock(String.class);

      mockStatic(Charset.class);
      when(Charset.forName(charsetName)).thenReturn(charset);

      spy(OutputHelper.class);
      doReturn(expected).when(OutputHelper.class);
      OutputHelper.openWriter(filename, charset, true);

      PrintWriter observed = OutputHelper.openWriter(filename, charsetName, true);
      assertEquals(expected, observed);

      verifyStatic();
      Charset.forName(charsetName);

      verifyStatic();
      OutputHelper.openWriter(filename, charset, true);
   }


   Opener openFilename_string_boolean = new Opener() {
      public PrintWriter openWriter(File file, Charset charset, boolean autoflush) throws IOException {
         return OutputHelper.openWriter(file.getAbsolutePath(), charset.displayName(), autoflush);
      }
   };


   @Test
   public void testOpenWriter_filename_string_autoflush_setsAutoflushToTrue() throws Throwable {
      testOpenWriterSetsAutoFlushTrue(openFilename_string_boolean);
   }

   @Test
   public void testOpenWriter_filename_string_autoflush_setsAutoflushToFalse() throws Throwable {
      testOpenWriterSetsAutoFlushFalse(openFilename_string_boolean);
   }

   @Test
   public void testOpenWriter_filename_string_autoflush_setsEncodings() throws Throwable {
      CharsetTester.testOpenWriterSetsAllEncodings(openFilename_string_boolean);
   }

   ////////////////////////////////////////////////////////////
   //
   // beta OpenWriter(Filename, boolean)
   //
   ////////////////////////////////////////////////////////////

   @Test(expected = FileNotFoundException.class)
   public void testOpenWriter_Filename_boolean_ThrowsExceptionIfFileNotFound() throws Throwable {
      OutputHelper.openWriter(FILE_IN_MISSING_DIR, false);
   }

   @Test
   public void testOpenWriter_filename_boolean() throws Throwable {
      Charset charset = mock(Charset.class);
      PrintWriter expected = mock(PrintWriter.class);
      String filename = mock(String.class);

      mockStatic(Charset.class);
      when(Charset.defaultCharset()).thenReturn(charset);

      spy(OutputHelper.class);
      doReturn(expected).when(OutputHelper.class);
      OutputHelper.openWriter(filename, charset, true);

      PrintWriter observed = OutputHelper.openWriter(filename, true);
      assertEquals(expected, observed);

      verifyStatic();
      OutputHelper.openWriter(filename, charset, true);
   }


   Opener openFilename_boolean = new Opener() {
      public PrintWriter openWriter(File file, Charset charset, boolean autoflush) throws IOException {
         return OutputHelper.openWriter(file.getAbsolutePath(), autoflush);
      }
   };

   @Test
   public void testOpenWriter_filename_autoflush_setsAutoflushToTrue() throws Throwable {
      testOpenWriterSetsAutoFlushTrue(openFilename_boolean);
   }

   @Test
   public void testOpenWriter_filename_autoflush_setsAutoflushToFalse() throws Throwable {
      testOpenWriterSetsAutoFlushFalse(openFilename_boolean);
   }

   @Test
   public void testOpenWriter_filename_autoflush_setsEncodings() throws Throwable {
      CharsetTester.forDefaultCharset().testOpenWriterSetsEncoding(openFilename_boolean);
   }

   ////////////////////////////////////////////////////////////
   //
   // beta OpenWriter(Filename, String)
   //
   ////////////////////////////////////////////////////////////

   @Test(expected = FileNotFoundException.class)
   public void testOpenWriter_Filename_String_ThrowsExceptionIfFileNotFound() throws Throwable {
      OutputHelper.openWriter(FILE_IN_MISSING_DIR, Charset.defaultCharset().displayName());
   }

   @Test
   public void testOpenWriter_filename_string() throws Throwable {
      String charsetName = mock(String.class);
      PrintWriter expected = mock(PrintWriter.class);
      String filename = mock(String.class);

      spy(OutputHelper.class);
      doReturn(expected).when(OutputHelper.class);
      OutputHelper.openWriter(filename, charsetName, false);

      PrintWriter observed = OutputHelper.openWriter(filename, charsetName);
      assertEquals(expected, observed);

      verifyStatic();
      OutputHelper.openWriter(filename, charsetName, false);
   }

   Opener openFilename_String = new Opener() {
      public PrintWriter openWriter(File file, Charset charset, boolean autoflush) throws IOException {
         return OutputHelper.openWriter(file.getAbsolutePath(), charset.displayName());
      }
   };

   @Test
   public void testOpenWriter_filename_string_setsAutoflushToFalse() throws Throwable {
      testOpenWriterSetsAutoFlushFalse(openFilename_String);
   }

   @Test
   public void testOpenWriter_filename_string_setsEncodings() throws Throwable {
      CharsetTester.testOpenWriterSetsAllEncodings(openFilename_String);
   }

   ////////////////////////////////////////////////////////////
   //
   // beta OpenWriter(filename, Map, Charset, boolean)
   //
   ////////////////////////////////////////////////////////////

   @Test(expected = FileNotFoundException.class)
   public void testOpenWriter_Filename_map_charset_boolean_ThrowsExceptionIfFileNotFound() throws Throwable {
      OutputHelper.openWriter(FILE_IN_MISSING_DIR, null, Charset.defaultCharset(), false);
   }

   @Test
   public void testOpenWriter_filename_map_charset_autoflush() throws Throwable {
      Charset charset = mock(Charset.class);
      PrintWriter expected = mock(PrintWriter.class);
      String filename = mock(String.class);
      OutputStream stream = mock(OutputStream.class);
      @SuppressWarnings("unchecked")
      Map<String, OutputStream> map = mock(Map.class);


      spy(OutputHelper.class);
      doReturn(stream).when(OutputHelper.class);
      OutputHelper.getOutputStream(filename, map);

      doReturn(expected).when(OutputHelper.class);
      OutputHelper.openWriter(stream, charset, true);

      PrintWriter observed = OutputHelper.openWriter(filename, map, charset, true);
      assertEquals(expected, observed);

      verifyStatic();
      OutputHelper.openWriter(stream, charset, true);
   }


   Opener openFileName_nullmap_charset_boolean = new Opener() {
      public PrintWriter openWriter(File file, Charset charset, boolean autoflush) throws IOException {
         return OutputHelper.openWriter(file.getAbsolutePath(), null, charset, autoflush);
      }
   };

   Opener openFileName_defaultmap_charset_boolean = new Opener() {
      public PrintWriter openWriter(File file, Charset charset, boolean autoflush) throws IOException {
         return OutputHelper.openWriter(file.getAbsolutePath(), OutputHelper.DEFAULT_OUTPUT_STREAM_MAP, charset, autoflush);
      }
   };

   @Test
   public void testOpenWriter_filename_map_charset_autoflush_setsAutoflushToTrue() throws Throwable {
      testOpenWriterSetsAutoFlushTrue(openFileName_nullmap_charset_boolean);
      testOpenWriterSetsAutoFlushTrue(openFileName_defaultmap_charset_boolean);
   }

   @Test
   public void testOpenWriter_filename_map_charset_autoflush_setsAutoflushToFalse() throws Throwable {
      testOpenWriterSetsAutoFlushFalse(openFileName_nullmap_charset_boolean);
      testOpenWriterSetsAutoFlushFalse(openFileName_defaultmap_charset_boolean);
   }

   @Test
   public void testOpenWriter_filename_map_charset_autoflush_setsEncodings() throws Throwable {
      CharsetTester.testOpenWriterSetsAllEncodings(openFileName_nullmap_charset_boolean);
      CharsetTester.testOpenWriterSetsAllEncodings(openFileName_defaultmap_charset_boolean);
   }

   @Test
   public void testOpenWriter_filename_map_writesToSpecialStream() throws Throwable {
      Map<String, OutputStream> map = new HashMap<String, OutputStream>();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      map.put("myFile", baos);
      PrintWriter pw = OutputHelper.openWriter("myFile", map, Charset.defaultCharset(), false);
      String message = "These are the times that try men's souls.";
      pw.println(message);
      pw.close();
      assertTrue(baos.toString().startsWith(message));
   }

   // not a test.
   // Calling System.setOut can mess up other tests.  This behavior is best tested
   // by running a standalone program at the command line and verifying that the desired output appears on stdout.
   private void testOpenWriter_filename_map_writesToStdout() throws Throwable {

      // If we call System.setOut after OutputHelper.DEFAULT_OUTPUT_STREAM_MAP has
      // been created, then the map returns a stream attached to the original System.out
      // and the beta fails.  (The PrintWriter writes to the console instead of the
      // ByteArrayOutputStream.  The easiest way to fix this is to make a new
      // default output stream map and pass it to openWriter.

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
      PrintWriter pw = OutputHelper.openWriter("-", OutputHelper.makeDefaultOutputStreamMap(), Charset.defaultCharset(),
            true);
      pw.println("Ouch!");
      assertTrue("Doesn't work: ->" + baos.toString() + "<-", baos.toString().startsWith("Ouch!"));

      System.setOut(null);
   }

   // beta openWriter_filename_string_boolean

   @Test
   public void testOpeWriter_filename_String_bool() throws Throwable {
      String filename = mock(String.class);
      String charset = mock(String.class);
      Charset charset_obj = mock(Charset.class);

      mockStatic(Charset.class);
      when(Charset.forName(charset)).thenReturn(charset_obj);

      @SuppressWarnings("unchecked")
      Map<String, OutputStream> map = mock(Map.class);
      PrintWriter expected = mock(PrintWriter.class);

      spy(OutputHelper.class);
      doReturn(expected).when(OutputHelper.class);
      OutputHelper.openWriter(filename, map, charset_obj, true);

      PrintWriter observed = OutputHelper.openWriter(filename, map, charset, true);
      assertEquals(observed, expected);

      verifyStatic();
      OutputHelper.openWriter(filename, map, charset_obj, true);

   }


   // Test openMappedWriter

   @Test
   public void testOpenMappedWriter_filename_charset_bool() throws Throwable {
      String filename = mock(String.class);
      Charset charset = mock(Charset.class);
      PrintWriter expected = mock(PrintWriter.class);

      spy(OutputHelper.class);
      doReturn(expected).when(OutputHelper.class);
      OutputHelper.openWriter(filename, OutputHelper.DEFAULT_OUTPUT_STREAM_MAP, charset, true);

      PrintWriter observed = OutputHelper.openMappedWriter(filename, charset, true);
      assertEquals(observed, expected);

      verifyStatic();
      OutputHelper.openWriter(filename, OutputHelper.DEFAULT_OUTPUT_STREAM_MAP, charset, true);
   }

   @Test
   public void testOpenMappedWriter_filename_string_bool() throws Throwable {
      String filename = mock(String.class);
      String charset = mock(String.class);
      PrintWriter expected = mock(PrintWriter.class);

      spy(OutputHelper.class);
      doReturn(expected).when(OutputHelper.class);
      OutputHelper.openWriter(filename, OutputHelper.DEFAULT_OUTPUT_STREAM_MAP, charset, true);

      PrintWriter observed = OutputHelper.openMappedWriter(filename, charset, true);
      assertEquals(observed, expected);

      verifyStatic();
      OutputHelper.openWriter(filename, OutputHelper.DEFAULT_OUTPUT_STREAM_MAP, charset, true);
   }


   ////////////////////////////////////////////////////////////
   //
   // beta getOutputStreamOrQuit
   //
   ////////////////////////////////////////////////////////////

   @Test
   public void testGetOutputStreamOurQuit_doesQuit() throws Throwable {
      String filename = mock(String.class);
      @SuppressWarnings("unchecked")
      Map<String, OutputStream> map = mock(Map.class);
      PrintStream err = mock(PrintStream.class);
      int exitValue = 397433;

      mockStatic(System.class);

      spy(OutputHelper.class);
      doThrow(new FileNotFoundException()).when(OutputHelper.class);
      OutputHelper.getOutputStream(filename, map);

      OutputHelper.getOutputStreamOrQuit(filename, map, err, exitValue);

      verifyStatic();
      OutputHelper.getOutputStream(filename, map);

      verifyStatic();
      System.exit(exitValue);

      verify(err).printf(anyString(), anyString(), anyString());

   }

   @Test
   public void testGetOutputStreamOurQuit_returnsStream() throws Throwable {
      String filename = mock(String.class);
      @SuppressWarnings("unchecked")
      Map<String, OutputStream> map = mock(Map.class);
      PrintStream err = mock(PrintStream.class);
      int exitValue = 397433;

      OutputStream expected = mock(OutputStream.class);

      spy(OutputHelper.class);
      doReturn(expected).when(OutputHelper.class);
      OutputHelper.getOutputStream(filename, map);

      OutputStream observed = OutputHelper.getOutputStreamOrQuit(filename, map, err, exitValue);
      assertEquals(expected, observed);

      verifyStatic();
      OutputHelper.getOutputStream(filename, map);
   }

   @Test
   public void testGetOutputStreamOurQuit_Defaults() throws Throwable {
      String filename = mock(String.class);
      @SuppressWarnings("unchecked")
      Map<String, OutputStream> map = mock(Map.class);

      OutputStream expected = mock(OutputStream.class);

      spy(OutputHelper.class);
      doReturn(expected).when(OutputHelper.class);
      OutputHelper.getOutputStreamOrQuit(filename, map, System.err, 1);

      OutputStream observed = OutputHelper.getOutputStreamOrQuit(filename, map);
      assertEquals(expected, observed);

      verifyStatic();
      OutputHelper.getOutputStreamOrQuit(filename, map, System.err, 1);
   }

   ////////////////////////////////////////////////////////////
   //
   // beta openWriterOrQuit_file_charset_boolean
   //
   ////////////////////////////////////////////////////////////

   @Test
   public void testOpenWriterOrQuit_file_charset_boolean_doesQuit() throws Throwable {
      File file = mock(File.class);
      Charset charset = mock(Charset.class);

      PrintStream err = mock(PrintStream.class);
      int exitValue = 397433;

      mockStatic(System.class);

      spy(OutputHelper.class);
      doThrow(new FileNotFoundException()).when(OutputHelper.class);
      OutputHelper.openWriter(file, charset, true);

      OutputHelper.openWriterOrQuit(file, charset, true, err, exitValue);

      verifyStatic();
      OutputHelper.openWriter(file, charset, true);


      verifyStatic();
      System.exit(exitValue);
   }


   @Test
   public void testOpenWriterOrQuit_file_charset_boolean_returnsStream() throws Throwable {
      File file = mock(File.class);
      Charset charset = mock(Charset.class);

      PrintStream err = mock(PrintStream.class);
      int exitValue = 397433;

      PrintWriter expected = mock(PrintWriter.class);

      mockStatic(System.class);

      spy(OutputHelper.class);
      doReturn(expected).when(OutputHelper.class);
      OutputHelper.openWriter(file, charset, true);

      PrintWriter observed = OutputHelper.openWriterOrQuit(file, charset, true, err, exitValue);
      assertEquals(expected, observed);

      verifyStatic();
      OutputHelper.openWriter(file, charset, true);
   }

   @Test
   public void testOpenWriterOrQuit_file_charset_boolean__Defaults() throws Throwable {
      File file = mock(File.class);
      Charset charset = mock(Charset.class);

      PrintWriter expected = mock(PrintWriter.class);

      spy(OutputHelper.class);
      doReturn(expected).when(OutputHelper.class);
      OutputHelper.openWriterOrQuit(file, charset, true, System.err, 1);

      PrintWriter observed = OutputHelper.openWriterOrQuit(file, charset, true);
      assertEquals(expected, observed);

      verifyStatic();
      OutputHelper.openWriterOrQuit(file, charset, true, System.err, 1);
   }

   ////////////////////////////////////////////////////////////
   //
   // beta openWriterOrQuit_filename_charset_boolean
   //
   ////////////////////////////////////////////////////////////

   private final PrintStream MOCK_ERR = mock(PrintStream.class);
   private final int RET_VAL = 8675309;
   private final Charset MOCK_CHARSET = mock(Charset.class);
   private boolean AUTOFLUSH = false;

   private interface Opener2 {
      PrintWriter openInner(String filename) throws FileNotFoundException;

      PrintWriter openOuter(String filename);
   }

   private void testOpenWriterQuits(Opener2 opener, Throwable t) throws FileNotFoundException {
      String filename = mock(String.class);

      mockStatic(System.class);

      spy(OutputHelper.class);
      doThrow(t).when(OutputHelper.class);
      opener.openInner(filename);

      opener.openOuter(filename);

      verifyStatic();
      System.exit(RET_VAL);

      Mockito.verify(MOCK_ERR).printf(anyString(), anyString(), anyString());
      Mockito.verify(t).getMessage();
   }

   private void testOpenWriterOrQuit_returnsStream(Opener2 opener) throws Throwable {
      String filename = mock(String.class);

      PrintWriter expected = mock(PrintWriter.class);
      mockStatic(System.class);

      spy(OutputHelper.class);
      doReturn(expected).when(OutputHelper.class);
      opener.openInner(filename);

      PrintWriter observed = opener.openOuter(filename);
      assertEquals(expected, observed);

      verifyStatic();
      opener.openInner(filename);
   }

   private void testOpenWriterOrQuit_usesDefaults(Opener2 opener) throws FileNotFoundException {
      String filename = mock(String.class);
      PrintWriter expected = mock(PrintWriter.class);

      spy(OutputHelper.class);
      doReturn(expected).when(OutputHelper.class);
      opener.openInner(filename);

      PrintWriter observed = opener.openOuter(filename);

      assertEquals(expected, observed);
      verifyStatic();
      opener.openInner(filename);
   }

   private Opener2 opener_filename_charset_boolean = new Opener2() {

      public PrintWriter openInner(String filename) throws FileNotFoundException {
         return OutputHelper.openWriter(filename, MOCK_CHARSET, AUTOFLUSH);
      }

      public PrintWriter openOuter(String filename) {
         return OutputHelper.openWriterOrQuit(filename, MOCK_CHARSET, AUTOFLUSH, MOCK_ERR, RET_VAL);
      }
   };

   @Test
   public void testOpenWriterOrQuit_filename_charset_boolean_doesQuit() throws Throwable {
      testOpenWriterQuits(opener_filename_charset_boolean, mock(FileNotFoundException.class));
   }

   @Test
   public void testOpenWriterOrQuit_filename_charset_boolean_returnsStream() throws Throwable {
      testOpenWriterOrQuit_returnsStream(opener_filename_charset_boolean);
   }

   @Test
   public void testOpenWriterOrQuit_filename_charset_boolean__Defaults() throws Throwable {
      testOpenWriterOrQuit_usesDefaults(new Opener2() {
         public PrintWriter openInner(String filename) throws FileNotFoundException {
            return OutputHelper.openWriterOrQuit(filename, MOCK_CHARSET, true, System.err, 1);
         }

         public PrintWriter openOuter(String filename) {
            return OutputHelper.openWriterOrQuit(filename, MOCK_CHARSET, true);
         }
      });
   }


   ////////////////////////////////////////////////////////////
   //
   // openWriterOrQuit_filename_string_boolean
   //
   ////////////////////////////////////////////////////////////

   private String SAMPLE_STRING = "Annap";
   private Opener2 opener_filename_string_boolean = new Opener2() {

      public PrintWriter openInner(String filename) throws FileNotFoundException {
         return OutputHelper.openWriter(filename, SAMPLE_STRING, AUTOFLUSH);
      }

      public PrintWriter openOuter(String filename) {
         return OutputHelper.openWriterOrQuit(filename, SAMPLE_STRING, AUTOFLUSH, MOCK_ERR, RET_VAL);
      }
   };

   @Test
   public void testOpenWriterOrQuit_filename_string_boolean_doesQuit() throws Throwable {
      testOpenWriterQuits(opener_filename_string_boolean, mock(FileNotFoundException.class));
   }

   @Test
   public void testOpenWriterOrQuit_filename_string_boolean_returnsStream() throws Throwable {
      testOpenWriterOrQuit_returnsStream(opener_filename_string_boolean);
   }

   @Test
   public void testOpenWriterOrQuit_filename_string_boolean__Defaults() throws Throwable {
      testOpenWriterOrQuit_usesDefaults(new Opener2() {
         public PrintWriter openInner(String filename) throws FileNotFoundException {
            return OutputHelper.openWriterOrQuit(filename, SAMPLE_STRING, AUTOFLUSH, System.err, 1);
         }

         public PrintWriter openOuter(String filename) {
            return OutputHelper.openWriterOrQuit(filename, SAMPLE_STRING, AUTOFLUSH);
         }
      });
   }

   ////////////////////////////////////////////////////////////
   //
   // openWriterOrQuit_filename_boolean
   //
   ////////////////////////////////////////////////////////////

   private Opener2 opener_filename_boolean = new Opener2() {

      public PrintWriter openInner(String filename) throws FileNotFoundException {
         return OutputHelper.openWriter(filename, AUTOFLUSH);
      }

      public PrintWriter openOuter(String filename) {
         return OutputHelper.openWriterOrQuit(filename, AUTOFLUSH, MOCK_ERR, RET_VAL);
      }
   };

   @Test
   public void testOpenWriterOrQuit_filename_boolean_doesQuit() throws Throwable {
      testOpenWriterQuits(opener_filename_boolean, mock(FileNotFoundException.class));
   }

   @Test
   public void testOpenWriterOrQuit_filename_boolean_returnsStream() throws Throwable {
      testOpenWriterOrQuit_returnsStream(opener_filename_boolean);
   }

   @Test
   public void testOpenWriterOrQuit_filename_boolean_Defaults() throws Throwable {
      testOpenWriterOrQuit_usesDefaults(new Opener2() {
         public PrintWriter openInner(String filename) throws FileNotFoundException {
            return OutputHelper.openWriterOrQuit(filename, AUTOFLUSH, System.err, 1);
         }

         public PrintWriter openOuter(String filename) {
            return OutputHelper.openWriterOrQuit(filename, AUTOFLUSH);
         }
      });
   }


   ////////////////////////////////////////////////////////////
   //
   // beta openMappedWriterOrQuit_filename_string_boolean
   //
   ////////////////////////////////////////////////////////////

   private Opener2 mappedOpener_filename_string_boolean = new Opener2() {

      public PrintWriter openInner(String filename) throws FileNotFoundException {
         return OutputHelper.openMappedWriter(filename, SAMPLE_STRING, AUTOFLUSH);
      }

      public PrintWriter openOuter(String filename) {
         return OutputHelper.openMappedWriterOrQuit(filename, SAMPLE_STRING, AUTOFLUSH, MOCK_ERR, RET_VAL);
      }
   };

   @Test
   public void testOpenMappedWriterOrQuit_filename_string_boolean_doesQuit() throws Throwable {
      testOpenWriterQuits(mappedOpener_filename_string_boolean, mock(FileNotFoundException.class));
   }

   @Test
   public void testOpenMappedWriterOrQuit_filename_string_boolean_returnsStream() throws Throwable {
      testOpenWriterOrQuit_returnsStream(mappedOpener_filename_string_boolean);
   }

   @Test
   public void testOpenMappedWriterOrQuit_filename_string_boolean__Defaults() throws Throwable {
      testOpenWriterOrQuit_usesDefaults(new Opener2() {
         public PrintWriter openInner(String filename) throws FileNotFoundException {
            return OutputHelper.openMappedWriterOrQuit(filename, SAMPLE_STRING, AUTOFLUSH, System.err, 1);
         }

         public PrintWriter openOuter(String filename) {
            return OutputHelper.openMappedWriterOrQuit(filename, SAMPLE_STRING, AUTOFLUSH);
         }
      });
   }

   public static void main(String[] args) throws Throwable {

      org.junit.runner.JUnitCore.main(OutputHelperTest.class.getName());


//      System.out.println(" Java Version: " +
//            System.getProperty("java.version") +
//            " from " + System.getProperty("java.vendor"));
//      String message = "Dźien Dobry. Jak się masz?";
//      ByteArrayOutputStream stream = new ByteArrayOutputStream();
//      Charset charset = Charset.forName("UTF-8");
//      PrintWriter pw = new PrintWriter(new java.io.BufferedWriter(
//            new java.io.OutputStreamWriter(stream, charset)), true);
//      pw.write(message);
//      pw.close();
//      System.out.println(stream.toString());
//      System.out.println(java.util.Arrays.toString(stream.toByteArray()));

      //new OutputHelperTest().testOpenWriter_OutputStream_setsEncoding();
   }

}
