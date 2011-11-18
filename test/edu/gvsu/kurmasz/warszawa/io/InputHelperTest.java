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
package edu.gvsu.kurmasz.warszawa.io;

import org.apache.tools.bzip2.CBZip2InputStream;
import org.apache.tools.bzip2.CBZip2OutputStream;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * @author Zachary Kurmas
 */
// Created  9/20/11 at 10:51 AM
// (C) Zachary Kurmas 2011
@RunWith(PowerMockRunner.class)
@PrepareForTest({InputHelper.class})
public class InputHelperTest {

   // Bytes are, by definition, signed.  However, hexdump shows unsigned bytes.
   // This method helps by allowing me to hard-code the contents of a file as shown by
   // hexdump, then convert it to a byte array.
   public static byte[] makeByteArray(int[] data) {
      byte[] sampleInput = new byte[data.length];
      for (int i = 0; i < sampleInput.length; i++) {
         sampleInput[i] = (byte) data[i];
      }
      return sampleInput;
   }

   public static final byte[] COMPRESSED_BZIPPED_MESSAGE = makeByteArray(new int[]{
         0x42, 0x5a, 0x68, 0x39, 0x31, 0x41, 0x59, 0x26, 0x53, 0x59, 0x6b, 0x5d, 0xfb, 0x46, 0x00, 0x00,
         0x03, 0x9f, 0x80, 0x60, 0x04, 0x00, 0x00, 0x80, 0x40, 0x00, 0x80, 0x26, 0x04, 0x92, 0xa0, 0x20,
         0x00, 0x22, 0xb2, 0x69, 0xa6, 0xd2, 0x66, 0x90, 0xa6, 0x00, 0x01, 0x76, 0x66, 0x68, 0x80, 0x96,
         0x69, 0x43, 0x6a, 0xf1, 0xf3, 0xc8, 0x3e, 0x2e, 0xe4, 0x8a, 0x70, 0xa1, 0x20, 0xd6, 0xbb, 0xf6,
         0x8c});
   public static final String UNCOMPRESSED_BZIPPED_MESSAGE = "Hello, World! How are you?";


   @Test
   public void testMakeDefaultInputStreamMap() throws Exception {
      Map<String, InputStream> map = InputHelper.makeDefaultInputStreamMap();
      assertEquals("Problem with -", System.in, map.get("-"));
      assertEquals("Problem with stdin", System.in, map.get("stdin"));
      assertEquals("Problem with STDIN", System.in, map.get("STDIN"));
      assertNull(map.get("yourDad"));
   }

   @Test
   public void defaultInputStreamMap() throws Throwable {
      assertEquals("Wrong default input map", InputHelper.makeDefaultInputStreamMap(), InputHelper.DEFAULT_INPUT_STREAM_MAP);
   }

   @Test(expected = UnsupportedOperationException.class)
   public void verifyDefaultInputStreamMapIsImmutable() throws Throwable {
      InputHelper.DEFAULT_INPUT_STREAM_MAP.put("fred", System.in);
   }

   ////////////////////////////////////////////////////////////
   //
   // Test BZip input stream
   //
   ////////////////////////////////////////////////////////////

   @Test
   public void testBZIP2_FactoryHandlesGoodBzip2Streams() throws Throwable {

      ByteArrayInputStream sampleInputStream = new ByteArrayInputStream(COMPRESSED_BZIPPED_MESSAGE);

      InputStream bzStream = InputHelper.BZIP2_FACTORY.makeFilter(sampleInputStream);

      // Attempt to read extra data to verify that the stream produces no extra data.
      byte[] bytesRead = new byte[UNCOMPRESSED_BZIPPED_MESSAGE.length() + 10];
      int numBytes = bzStream.read(bytesRead);
      assertEquals(UNCOMPRESSED_BZIPPED_MESSAGE.length(), numBytes);

      byte[] trimmedBytesRead = new byte[numBytes];
      System.arraycopy(bytesRead, 0, trimmedBytesRead, 0, trimmedBytesRead.length);
      Assert.assertArrayEquals(UNCOMPRESSED_BZIPPED_MESSAGE.getBytes(), trimmedBytesRead);
   }

   private void verifyBzip2_makeFilterThrowsException(byte[] data, String message) {
      try {
         ByteArrayInputStream sampleInputStream = new ByteArrayInputStream(data);
         InputHelper.BZIP2_FACTORY.makeFilter(sampleInputStream);
         fail("This input should not be a valid bzip2 stream: " + message);
      } catch (InputHelper.FilterFactory.FilterFactoryException e) {
         // This is what should happen
      }
   }

   @Test
   public void testBZIP2_FactoryThrowsExceptionGivenBadBZIP2Stream() throws Throwable {
      verifyBzip2_makeFilterThrowsException(new byte[]{0x43, 0x5a}, "Bad 1st character");
      verifyBzip2_makeFilterThrowsException(new byte[]{0x42, 0x5b}, "Bad 2nd character");
      verifyBzip2_makeFilterThrowsException(new byte[]{0x42}, "Only one character");
      verifyBzip2_makeFilterThrowsException(new byte[]{}, "No characters");
      verifyBzip2_makeFilterThrowsException(new byte[]{0x42, 0x5b}, "Two characters only");
   }

   @Test(expected = NullPointerException.class)
   public void testBZIP2_FactoryThrowsExceptionWhenPassedNull() throws Throwable {
      InputHelper.BZIP2_FACTORY.makeFilter(null);
   }

   @Test
   public void testBZIP2_FactoryUsesBufferedInputStream() throws Throwable {
      InputStream in = new ByteArrayInputStream("BZ".getBytes());
      BufferedInputStream buffer = mock(BufferedInputStream.class);
      CBZip2InputStream expected = mock(CBZip2InputStream.class);

      PowerMockito.spy(InputHelper.BZIP2_FACTORY);
      whenNew(BufferedInputStream.class).withArguments(in).thenReturn(buffer);
      whenNew(CBZip2InputStream.class).withArguments(buffer).thenReturn(expected);
      InputStream observed = InputHelper.BZIP2_FACTORY.makeFilter(in);

      assertEquals(expected, observed);
   }

   ////////////////////////////////////////////////////////////
   //
   // Test beta DefaultFilterFactoryMap
   //
   ////////////////////////////////////////////////////////////

   @Test
   public void testMakeDefaultFilterFactoryMap() throws Throwable {
      Map<String, InputHelper.FilterFactory> map = InputHelper.makeDefaultFilterFactoryMap();
      assertEquals(InputHelper.BZIP2_FACTORY, map.get("bz2"));
   }

   @Test(expected = UnsupportedOperationException.class)
   public void verifyDefaultFilterFactoryMapIsImmutable() throws Throwable {
      InputHelper.DEFAULT_FILTER_FACTORY_MAP.put("fred", null);
   }

   ////////////////////////////////////////////////////////////
   //
   // Test openInputStream(filename, streamMap, filterMap)
   //
   ////////////////////////////////////////////////////////////

   //
   // File not found
   //

   @Test(expected = FileNotFoundException.class)
   public void testOpenInputStream_sm_fm_throwsExceptionWhenFileNotFound() throws Throwable {
      InputHelper.openInputStream("ThisFileShouldNotExist_alkadflkajdfad", null, null);
   }

   @Test(expected = FileNotFoundException.class)
   public void testOpenInputStream_sm_fm_throwsExceptionWhenFileNotFound2() throws Throwable {
      InputHelper.openInputStream("ThisFileShouldNotExist_alkadflkajdfad.bz2", null, InputHelper.DEFAULT_FILTER_FACTORY_MAP);
   }

   @Test(expected = InputHelper.FilterFactory.FilterFactoryException.class)
   public void testOpenInputStream_sm_fm_throwsExceptionOnBadFilter() throws Throwable {

      File tempFile = File.createTempFile("InputHelper", ".bz2");
      OutputStream output = new FileOutputStream(tempFile);
      output.write(new byte[]{0, 1, 2, 3, 4, 5, 6, 7});
      output.close();
      try {
         InputHelper.openInputStream(tempFile.getAbsolutePath(), null, InputHelper.DEFAULT_FILTER_FACTORY_MAP);
      } catch (AssertionError e) {
         System.out.println("What's the problem:  " + e);
      } finally {
         OutputHelperTest.deleteTempFile(tempFile);
      }
      System.out.println("'S all good!");
   }

   //
   // Streams are looked up by full path name
   //
   @Test
   public void testOpenInputStream_sm_fm_looksUpStreamsByFullPathName() throws Throwable {
      String filename = mock(String.class);

      InputStream expected = mock(InputStream.class);

      @SuppressWarnings("unchecked")
      Map<String, InputStream> streamMap = mock(Map.class);

      when(streamMap.containsKey(filename)).thenReturn(true);
      when(streamMap.get(filename)).thenReturn(expected);
      InputStream observed = InputHelper.openInputStream(filename, streamMap, null);
      assertEquals(expected, observed);

      Mockito.verify(streamMap).containsKey(filename);
      Mockito.verify(streamMap).get(filename);
   }

   // Map contains "stdin", not "a/stdin", so this should throw an "FileNotFoundException"
   // (unless the cwd happens to contain teh file a/stdin
   @Test(expected = FileNotFoundException.class)
   public void testOpenInputStream_sm_fm_looksUpStreamsByFullPathName2() throws Throwable {
      InputStream observed = InputHelper.openInputStream("a/stdin", InputHelper.DEFAULT_INPUT_STREAM_MAP, null);
      assertFalse(observed != System.in);
   }

   @Test(expected = FileNotFoundException.class)
   public void testOpenInputStream_sm_fm_looksUpStreamsByFullPathName3() throws Throwable {
      InputStream observed = InputHelper.openInputStream("/stdin", InputHelper.DEFAULT_INPUT_STREAM_MAP, null);
      assertFalse(observed != System.in);
   }

   @Test(expected = FileNotFoundException.class)
   public void testOpenInputStream_sm_fm_looksUpStreamsByFullPathName4() throws Throwable {
      InputStream observed = InputHelper.openInputStream("/etc/stdin", InputHelper.DEFAULT_INPUT_STREAM_MAP, null);
      assertFalse(observed != System.in);
   }


   //
   // Return "hits" in the stream map (hits in stream map are never filtered)
   //

   @SuppressWarnings("unchecked")
   private static final Map<String, InputHelper.FilterFactory> MOCK_FILTER_MAP = mock(Map.class);

   @Test
   public void testOpenInputStream_sm_fm_ReturnsItemFromMap() throws Exception {
      InputStream fredStream = mock(InputStream.class);
      Map<String, InputStream> testMap = new HashMap<String, InputStream>();
      testMap.put("fred", fredStream);

      assertEquals("Wrong input stream", fredStream, InputHelper.openInputStream("fred", testMap, MOCK_FILTER_MAP));
   }

   @Test
   public void testOpenInputStream_sm_fm_DoesNotFilterItemsInMap() throws Exception {
      InputStream fredStream = mock(InputStream.class);
      Map<String, InputStream> testMap = new HashMap<String, InputStream>();
      testMap.put("fred.bz2", fredStream);
      assertEquals("Wrong input stream", fredStream, InputHelper.openInputStream("fred.bz2", testMap,
            MOCK_FILTER_MAP));
   }

   @Test
   public void testOpenInputStream_sm_fm_WithDefaultsReturnsStdin() throws FileNotFoundException {
      assertEquals("-", System.in, InputHelper.openInputStream("-", InputHelper.DEFAULT_INPUT_STREAM_MAP,
            MOCK_FILTER_MAP));
      assertEquals("stdin", System.in, InputHelper.openInputStream("stdin", InputHelper.DEFAULT_INPUT_STREAM_MAP,
            MOCK_FILTER_MAP));
      assertEquals("STDIN", System.in, InputHelper.openInputStream("STDIN", InputHelper.DEFAULT_INPUT_STREAM_MAP,
            MOCK_FILTER_MAP));

      assertEquals("-", System.in, InputHelper.openInputStream("-", InputHelper.DEFAULT_INPUT_STREAM_MAP, null));
   }

   //
   // Return "misses" in the stream map and misses in filter map
   //

   private void testOpenInputStream_sm_fm_StreamReturnsNewFileInputStream(String filename, Map<String,
         InputStream> streamMap, Map<String, InputHelper.FilterFactory> filterMap) throws Throwable {
      FileInputStream expected = mock(FileInputStream.class);
      File file = mock(File.class);

      spy(InputHelper.class);
      whenNew(File.class).withArguments(filename).thenReturn(file);
      whenNew(FileInputStream.class).withArguments(file).thenReturn(expected);

      InputStream observed = InputHelper.openInputStream(filename, streamMap, filterMap);
      assertEquals(expected, observed);
   }

   @Test
   public void testOpenInputStream_sm_fm_ReturnsNewFileOutputStreamWhenGivenNullMaps() throws Throwable {
      testOpenInputStream_sm_fm_StreamReturnsNewFileInputStream("someFile", null, null);
   }

   @Test
   public void testOpenInputStream_sm_fm_ReturnsNewFileOutputStreamWhenGivenNullStreamMapAndSuffixNotInFilterMap() throws
         Throwable {
      @SuppressWarnings("unchecked")
      Map<String, InputHelper.FilterFactory> map = mock(Map.class);
      when(map.containsKey("ddf")).thenReturn(false);
      testOpenInputStream_sm_fm_StreamReturnsNewFileInputStream("someFile.ddf", null, map);
   }

   @Test
   public void testOpenInputStream_sm_fm_ReturnsNewFileInputStreamWhenNotInMapAndFileFilterNull() throws Throwable {
      @SuppressWarnings("unchecked")
      Map<String, InputStream> streamMap = mock(Map.class);
      when(streamMap.containsKey("someFile.ddf")).thenReturn(false);

      testOpenInputStream_sm_fm_StreamReturnsNewFileInputStream("someFile.ddf", streamMap, null);
   }

   @Test
   public void testOpenInputStream_sm_fm_ReturnsNewFileInputStreamWhenNotInMapAndSuffixNotInFileFilter() throws Throwable {
      @SuppressWarnings("unchecked")
      Map<String, InputStream> streamMap = mock(Map.class);
      when(streamMap.containsKey("someFile.ddf")).thenReturn(false);

      @SuppressWarnings("unchecked")
      Map<String, InputHelper.FilterFactory> filterMap = mock(Map.class);
      when(filterMap.containsKey("ddf")).thenReturn(false);
      testOpenInputStream_sm_fm_StreamReturnsNewFileInputStream("someFile.ddf", streamMap,
            filterMap);
   }

   //
   // Return "misses" in the stream map and hits in filter map
   //
   private void testOpenInputStream_sm_fm_UsesFilter(Map<String, InputStream> streamMap) throws Throwable {
      String suffix = "sfx";
      String fileName = "/a/b/someFile." + suffix;
      File file = new File(fileName);

      Map<String, InputHelper.FilterFactory> filterMap = new HashMap<String, InputHelper.FilterFactory>();
      InputHelper.FilterFactory factory = mock(InputHelper.FilterFactory.class);
      filterMap.put(suffix, factory);

      FileInputStream fis = mock(FileInputStream.class);
      InputStream expected = mock(InputStream.class);

      spy(InputHelper.class);
      whenNew(File.class).withArguments(fileName).thenReturn(file);
      whenNew(FileInputStream.class).withArguments(file).thenReturn(fis);

      when(factory.makeFilter(fis)).thenReturn(expected);

      InputStream observed = InputHelper.openInputStream(fileName, streamMap, filterMap);
      assertEquals(expected, observed);

      Mockito.verify(factory.makeFilter(fis));
   }

   @Test
   public void testOpenInputStream_sm_fm_ReturnsFilteredInputStreamWhenStreamMapNull() throws Throwable {
      testOpenInputStream_sm_fm_UsesFilter(null);
   }

   @Test
   public void testOpenInputStream_sm_fm_ReturnsFilteredInputStreamWhenFileNotInStreamMap() throws Throwable {
      @SuppressWarnings("unchecked")
      Map<String, InputStream> map = mock(Map.class);
      when(map.containsKey("someFile.sfx")).thenReturn(false);
      testOpenInputStream_sm_fm_UsesFilter(map);
   }

   //
   // Verify that we can actually read files
   //

   @Test
   public void testOpenInputStream_sm_fm_ReadsRegularFile() throws Throwable {
      // Write a file:
      File tempFile = File.createTempFile("InputHelper", ".normal");

      FileOutputStream output = new FileOutputStream(tempFile);
      String message1 = "Boots Galore!";
      output.write(message1.getBytes());
      output.close();

      InputStream input = InputHelper.openInputStream(tempFile.getAbsolutePath(), null, InputHelper.DEFAULT_FILTER_FACTORY_MAP);
      byte[] inputData = new byte[message1.length() + 100];
      int numRead = input.read(inputData);
      assertEquals(message1.length(), numRead);

      byte[] truncatedData = new byte[message1.length()];
      System.arraycopy(inputData, 0, truncatedData, 0, numRead);
      assertArrayEquals(message1.getBytes(), truncatedData);

      input.close();
      OutputHelperTest.deleteTempFile(tempFile);
   }

   // Calling System.setIn potentially messes up other tests
   // Leave this test to the system tests
   public void testOpenInputStream_sm_fm_ReadsStdin() throws Throwable {

      String message1 = "Doggggggone!";
      ByteArrayInputStream bis = new ByteArrayInputStream(message1.getBytes());
      System.setIn(bis);

      // we can't use the DEFAULT_STREAM_MAP, because it contains the original value
      // of System.in (the one connected to the actual stdin instead of bis).
      InputStream input = InputHelper.openInputStream("stdin", InputHelper.makeDefaultInputStreamMap(), null);

      byte[] inputData = new byte[message1.length() + 100];
      int numRead = input.read(inputData);
      assertEquals(message1.length(), numRead);

      byte[] truncatedData = new byte[message1.length()];
      System.arraycopy(inputData, 0, truncatedData, 0, numRead);
      assertArrayEquals(message1.getBytes(), truncatedData);
   }

   @Test
   public void testOpenInputStream_sm_fm_correctlyOpensBzipFile() throws IOException {
      // Write a bzip2 file:
      File tempFile = File.createTempFile("InputHelper", ".bz2");
      //System.out.println("FIle: " + tempFile.getAbsolutePath());
      FileOutputStream output = new FileOutputStream(tempFile);
      output.write(COMPRESSED_BZIPPED_MESSAGE);
      output.close();

      InputStream input = InputHelper.openInputStream(tempFile.getAbsolutePath(), null,
            InputHelper.DEFAULT_FILTER_FACTORY_MAP);

      byte[] inputData = new byte[UNCOMPRESSED_BZIPPED_MESSAGE.length() + 100];
      int amountRead = input.read(inputData);

      assertEquals(UNCOMPRESSED_BZIPPED_MESSAGE.length(), amountRead);

      byte[] trimmedInputData = new byte[UNCOMPRESSED_BZIPPED_MESSAGE.length()];
      System.arraycopy(inputData, 0, trimmedInputData, 0, amountRead);
      assertEquals(UNCOMPRESSED_BZIPPED_MESSAGE, new String(trimmedInputData));

      OutputHelperTest.deleteTempFile(tempFile);
   }

   //
   // beta helpers
   //

   @SuppressWarnings("unchecked")
   private Map<String, InputStream> MOCK_STREAM_MAP = mock(Map.class);

   @Test
   public void openMappedAndFilteredInputStream_fn() throws Throwable {
      String filename = mock(String.class);
      InputStream expected = mock(InputStream.class);

      spy(InputHelper.class);
      doReturn(expected).when(InputHelper.class);
      InputHelper.openInputStream(filename, InputHelper.DEFAULT_INPUT_STREAM_MAP,
            InputHelper.DEFAULT_FILTER_FACTORY_MAP);

      InputStream observed = InputHelper.openMappedAndFilteredInputStream(filename);
      assertEquals(observed, expected);

      verifyStatic();
      InputHelper.openInputStream(filename, InputHelper.DEFAULT_INPUT_STREAM_MAP, InputHelper.DEFAULT_FILTER_FACTORY_MAP);
   }

   @Test
   public void openFilteredInputStream_fn() throws Throwable {

      String filename = mock(String.class);
      InputStream expected = mock(InputStream.class);

      spy(InputHelper.class);
      doReturn(expected).when(InputHelper.class);
      InputHelper.openInputStream(filename, MOCK_STREAM_MAP,
            InputHelper.DEFAULT_FILTER_FACTORY_MAP);

      InputStream observed = InputHelper.openFilteredInputStream(filename, MOCK_STREAM_MAP);
      assertEquals(observed, expected);

      verifyStatic();
      InputHelper.openInputStream(filename, MOCK_STREAM_MAP, InputHelper.DEFAULT_FILTER_FACTORY_MAP);
   }

   @Test
   public void openFilteredInputStream_fnonly() throws Throwable {

      String filename = mock(String.class);
      InputStream expected = mock(InputStream.class);

      spy(InputHelper.class);
      doReturn(expected).when(InputHelper.class);
      InputHelper.openInputStream(filename, null,
            InputHelper.DEFAULT_FILTER_FACTORY_MAP);

      InputStream observed = InputHelper.openFilteredInputStream(filename);
      assertEquals(observed, expected);

      verifyStatic();
      InputHelper.openInputStream(filename, null, InputHelper.DEFAULT_FILTER_FACTORY_MAP);
   }


   @Test
   public void openUnfilteredInputStream_fn() throws Throwable {

      String filename = mock(String.class);
      InputStream expected = mock(InputStream.class);

      spy(InputHelper.class);
      doReturn(expected).when(InputHelper.class);
      InputHelper.openInputStream(filename, MOCK_STREAM_MAP, null);

      InputStream observed = InputHelper.openUnfilteredInputStream(filename, MOCK_STREAM_MAP);
      assertEquals(observed, expected);

      verifyStatic();
      InputHelper.openInputStream(filename, MOCK_STREAM_MAP, null);
   }

   @Test
   public void openMappedInputStream() throws Throwable {

      String filename = mock(String.class);
      InputStream expected = mock(InputStream.class);

      spy(InputHelper.class);
      doReturn(expected).when(InputHelper.class);
      InputHelper.openInputStream(filename, InputHelper.DEFAULT_INPUT_STREAM_MAP, MOCK_FILTER_MAP);

      InputStream observed = InputHelper.openMappedInputStream(filename, MOCK_FILTER_MAP);
      assertEquals(observed, expected);

      verifyStatic();
      InputHelper.openInputStream(filename, InputHelper.DEFAULT_INPUT_STREAM_MAP, MOCK_FILTER_MAP);
   }

   @Test
   public void openMappedInputStream_fnonly() throws Throwable {

      String filename = mock(String.class);
      InputStream expected = mock(InputStream.class);

      spy(InputHelper.class);
      doReturn(expected).when(InputHelper.class);
      InputHelper.openInputStream(filename, InputHelper.DEFAULT_INPUT_STREAM_MAP, null);

      InputStream observed = InputHelper.openMappedInputStream(filename);
      assertEquals(observed, expected);

      verifyStatic();
      InputHelper.openInputStream(filename, InputHelper.DEFAULT_INPUT_STREAM_MAP, null);
   }


   @Test
   public void openUmappedInputStream() throws Throwable {

      String filename = mock(String.class);
      InputStream expected = mock(InputStream.class);

      spy(InputHelper.class);
      doReturn(expected).when(InputHelper.class);
      InputHelper.openInputStream(filename, null, MOCK_FILTER_MAP);

      InputStream observed = InputHelper.openUnmappedInputStream(filename, MOCK_FILTER_MAP);
      assertEquals(observed, expected);

      verifyStatic();
      InputHelper.openInputStream(filename, null, MOCK_FILTER_MAP);
   }


   ////////////////////////////////////////////////////////////
   //
   // Test openInputStream (file)
   //
   ////////////////////////////////////////////////////////////

   @Test(expected = FileNotFoundException.class)
   public void testOpenInputStream_File_filterMap_throwsExceptionWhenFileNotFound() throws Throwable {
      InputHelper.openInputStream(new File("ThisFileShouldNotExist_alkadflkajdfad"),
            null);
   }

   @Test(expected = FileNotFoundException.class)
   public void testOpenInputStream_File_filterMap_throwsExceptionWhenFileNotFound2() throws Throwable {
      InputHelper.openInputStream(new File("ThisFileShouldNotExist_alkadflkajdfad.bz2"),
            InputHelper.DEFAULT_FILTER_FACTORY_MAP);
   }

   @Test
   public void testOpenInputStreamThrowsUncheckedExceptionWhenCloseFails() throws Throwable {

      Map<String, InputHelper.FilterFactory> ffMap = new HashMap<String, InputHelper.FilterFactory>();
      ffMap.put(".bz2", new InputHelper.FilterFactory() {
         public InputStream makeFilter(InputStream in) throws FilterFactoryException {
            throw new FilterFactoryException("Problem!");
         }
      });

      String filename =   "notAbz2.bz2";
      File file = new File(filename);
      FileInputStream fis = mock(FileInputStream.class);

      spy(InputHelper.class);
      whenNew(FileInputStream.class).withArguments(file).thenReturn(fis);
      Mockito.doThrow(new IOException()).when(fis).close();
      try {
         InputHelper.openInputStream(file, InputHelper.DEFAULT_FILTER_FACTORY_MAP);
         fail("This call should throw an exception");
      } catch (InputHelper.FilterFactory.FilterFactoryException e) {
         fail("This call should throw a generic RuntimeException");
      } catch (RuntimeException e) {
         assertTrue("Should be caused by an IOException", e.getCause() instanceof IOException );
      }
   }

   //
   // When suffix is not in map
   //

   @Test
   public void testOpenInputStream_File_filterMap_returnsNewInputStreamWhenMapIsNull() throws Throwable {
      File file = mock(File.class);

      FileInputStream expected = mock(FileInputStream.class);

      spy(InputHelper.class);
      whenNew(FileInputStream.class).withArguments(file).thenReturn(expected);

      InputStream observed = InputHelper.openInputStream(file, null);
      assertEquals(expected, observed);
   }

   @Test
   public void testOpenInputStream_File_filterMap_returnsNewInputStreamWhenSuffixNotInMap() throws Throwable {
      File file = new File("testFile.withSuffix");
      Map<String, InputHelper.FilterFactory> filters = new HashMap<String, InputHelper.FilterFactory>();

      FileInputStream expected = mock(FileInputStream.class);

      spy(InputHelper.class);
      whenNew(FileInputStream.class).withArguments(file).thenReturn(expected);

      InputStream observed = InputHelper.openInputStream(file, filters);
      assertEquals(expected, observed);
   }

   //
   // When suffix is in map
   //

   @Test
   public void testOpenInputStream_File_filterMap_returnsFilteredStreamWhenSuffixIsInMap() throws Throwable {
      File file = new File("testFile.withSuffix");

      InputHelper.FilterFactory filter = mock(InputHelper.FilterFactory.class);
      Map<String, InputHelper.FilterFactory> filters = new HashMap<String, InputHelper.FilterFactory>();
      filters.put("withSuffix", filter);

      spy(InputHelper.class);
      FileInputStream fis = mock(FileInputStream.class);
      whenNew(FileInputStream.class).withArguments(file).thenReturn(fis);

      InputStream expected = mock(InputStream.class);
      when(filter.makeFilter(fis)).thenReturn(expected);


      InputStream observed = InputHelper.openInputStream(file, filters);
      assertEquals(expected, observed);

      Mockito.verify(filter).makeFilter(fis);
   }

   @Test
   public void testOpenInputStream_File_filterMap_correctlyOpensBzipFile() throws IOException {
      // Write a bzip2 file:
      File tempFile = File.createTempFile("InputHelper", ".bz2");
      //System.out.println("FIle: " + tempFile.getAbsolutePath());
      FileOutputStream output = new FileOutputStream(tempFile);
      output.write(COMPRESSED_BZIPPED_MESSAGE);
      output.close();

      InputStream input = InputHelper.openInputStream(tempFile, InputHelper.DEFAULT_FILTER_FACTORY_MAP);

      byte[] inputData = new byte[UNCOMPRESSED_BZIPPED_MESSAGE.length() + 100];
      int amountRead = input.read(inputData);

      assertEquals(UNCOMPRESSED_BZIPPED_MESSAGE.length(), amountRead);

      byte[] trimmedInputData = new byte[UNCOMPRESSED_BZIPPED_MESSAGE.length()];
      System.arraycopy(inputData, 0, trimmedInputData, 0, amountRead);
      assertEquals(UNCOMPRESSED_BZIPPED_MESSAGE, new String(trimmedInputData));

      OutputHelperTest.deleteTempFile(tempFile);
   }

   @Test
   public void openFilteredInputStream_file() throws Throwable {
      File file = mock(File.class);
      InputStream expected = mock(InputStream.class);

      spy(InputHelper.class);
      doReturn(expected).when(InputHelper.class);
      InputHelper.openInputStream(file, InputHelper.DEFAULT_FILTER_FACTORY_MAP);

      InputStream observed = InputHelper.openFilteredInputStream(file);
      assertEquals(observed, expected);

      verifyStatic();
      InputHelper.openInputStream(file, InputHelper.DEFAULT_FILTER_FACTORY_MAP);
   }

   @Test
   public void openUnfilteredInputStream_file() throws Throwable {

      String filename = mock(String.class);
      InputStream expected = mock(InputStream.class);

      spy(InputHelper.class);
      doReturn(expected).when(InputHelper.class);
      InputHelper.openInputStream(filename, MOCK_STREAM_MAP, null);

      InputStream observed = InputHelper.openUnfilteredInputStream(filename, MOCK_STREAM_MAP);
      assertEquals(observed, expected);

      verifyStatic();
      InputHelper.openInputStream(filename, MOCK_STREAM_MAP, null);
   }


   ////////////////////////////////////////////////////////////
   //
   // Test openInputStream or quit (filename)
   //
   ////////////////////////////////////////////////////////////

   private final PrintStream MOCK_ERR = mock(PrintStream.class);
   private final int RET_VAL = 8675309;

   private interface Opener {
      InputStream openInner(String filename) throws FileNotFoundException;

      InputStream openOuter(String filename);
   }

   private void testOpenInputStreamQuits(Opener opener, Throwable t) throws FileNotFoundException {
      String filename = mock(String.class);

      mockStatic(System.class);

      spy(InputHelper.class);
      doThrow(t).when(InputHelper.class);
      opener.openInner(filename);

      opener.openOuter(filename);

      verifyStatic();
      System.exit(RET_VAL);

      Mockito.verify(MOCK_ERR).printf(anyString(), anyString(), anyString());
      Mockito.verify(t).getMessage();
   }

   private void testOpenInputStreamOrQuit_returnsStream(Opener opener) throws Throwable {
      String filename = mock(String.class);

      InputStream expected = mock(InputStream.class);
      mockStatic(System.class);

      spy(InputHelper.class);
      doReturn(expected).when(InputHelper.class);
      opener.openInner(filename);

      InputStream observed = opener.openOuter(filename);
      assertEquals(expected, observed);

      verifyStatic();
      opener.openInner(filename);
   }

   private void testOpenInputStreamOrQuit_usesDefaults(Opener opener) throws FileNotFoundException {
      String filename = mock(String.class);
      InputStream expected = mock(InputStream.class);

      spy(InputHelper.class);
      doReturn(expected).when(InputHelper.class);
      opener.openInner(filename);

      InputStream observed = opener.openOuter(filename);

      assertEquals(expected, observed);
      verifyStatic();
      opener.openInner(filename);
   }

   //
   // filename, streamMap, filterMap
   //

   private Opener open_filename_all = new Opener() {
      public InputStream openInner(String filename) throws FileNotFoundException {
         return InputHelper.openInputStream(filename, MOCK_STREAM_MAP, MOCK_FILTER_MAP);
      }

      public InputStream openOuter(String filename) {
         return InputHelper.openInputStreamOrQuit(filename, MOCK_STREAM_MAP, MOCK_FILTER_MAP, MOCK_ERR, RET_VAL);
      }
   };

   @Test
   public void testOpenInputStreamOrQuit_filename_all_quitsOnFileNotFound() throws Throwable {
      testOpenInputStreamQuits(open_filename_all, mock(FileNotFoundException.class));
   }

   @Test
   public void testOpenInputStreamOrQuit_filename_all_quitsOnFilterFactoryException() throws Throwable {
      testOpenInputStreamQuits(open_filename_all, mock(InputHelper.FilterFactory.FilterFactoryException.class));
   }

   @Test(expected = NullPointerException.class)
   public void testOpenInputStreamOrQuit_filename_all_passesOtherExceptions() throws Throwable {
      testOpenInputStreamQuits(open_filename_all, new NullPointerException());
   }

   @Test
   public void testOpenInputStreamOrQuit_filename_all_returnsStream() throws Throwable {
      testOpenInputStreamOrQuit_returnsStream(open_filename_all);
   }

   @Test
   public void testOpenInputStreamOrQuit_filename_all_defaults() throws Throwable {
      testOpenInputStreamOrQuit_usesDefaults(new Opener() {
         public InputStream openInner(String filename) throws FileNotFoundException {
            return InputHelper.openInputStreamOrQuit(filename, MOCK_STREAM_MAP, MOCK_FILTER_MAP, System.err, 1);
         }

         public InputStream openOuter(String filename) {
            return InputHelper.openInputStreamOrQuit(filename, MOCK_STREAM_MAP, MOCK_FILTER_MAP);
         }
      });
   }

   //
   // openMappedAndFilteredInputStreamOrQuit
   //

   private Opener open_mappedAndFiltered = new Opener() {
      public InputStream openInner(String filename) throws FileNotFoundException {
         return InputHelper.openMappedAndFilteredInputStream(filename);
      }

      public InputStream openOuter(String filename) {
         return InputHelper.openMappedAndFilteredInputStreamOrQuit(filename, MOCK_ERR,
               RET_VAL);
      }
   };

   @Test
   public void testOpenMappedAndFilteredInputStreamOrQuit_filename_all_quitsOnFileNotFound() throws Throwable {
      testOpenInputStreamQuits(open_mappedAndFiltered, mock(FileNotFoundException.class));
   }

   @Test
   public void testOpenMappedAndFilteredInputStreamOrQuit_filename_all_quitsOnFilterFactoryException() throws
         Throwable {
      testOpenInputStreamQuits(open_mappedAndFiltered, mock(InputHelper.FilterFactory.FilterFactoryException.class));
   }

   @Test(expected = NullPointerException.class)
   public void testOpenMappedAndFilteredInputStreamOrQuit_filename_all_passesOtherExceptions() throws Throwable {
      testOpenInputStreamQuits(open_mappedAndFiltered, new NullPointerException());
   }

   @Test
   public void testOpenMappedAndFilteredInputStreamOrQuit_filename_all_returnsStream() throws Throwable {
      testOpenInputStreamOrQuit_returnsStream(open_mappedAndFiltered);
   }

   @Test
   public void testOpenMappedAndFilteredInputStreamOrQuit_filename_all_defaults() throws Throwable {
      testOpenInputStreamOrQuit_usesDefaults(new Opener() {
         public InputStream openInner(String filename) throws FileNotFoundException {
            return InputHelper.openMappedAndFilteredInputStreamOrQuit(filename, System.err, 1);
         }

         public InputStream openOuter(String filename) {
            return InputHelper.openMappedAndFilteredInputStreamOrQuit(filename);
         }
      });
   }


   //
   // beta OpenFilteredInputStream(filename, streamMap)
   //

   private Opener open_filtered_filename = new Opener() {
      public InputStream openInner(String filename) throws FileNotFoundException {
         return InputHelper.openFilteredInputStream(filename, MOCK_STREAM_MAP);
      }

      public InputStream openOuter(String filename) {
         return InputHelper.openFilteredInputStreamOrQuit(filename, MOCK_STREAM_MAP, MOCK_ERR, RET_VAL);
      }
   };

   @Test
   public void testOpenFilteredInputStreamOrQuit_filename_quitsOnFileNotFound() throws Throwable {
      testOpenInputStreamQuits(open_filtered_filename, mock(FileNotFoundException.class));
   }

   @Test
   public void testOpenFilteredInputStreamOrQuit_filename_quitsOnFilterFactoryException() throws Throwable {
      testOpenInputStreamQuits(open_filtered_filename, mock(InputHelper.FilterFactory.FilterFactoryException.class));
   }

   @Test(expected = NullPointerException.class)
   public void testOpenFilteredInputStreamOrQuit_filename_passesOtherExceptions() throws Throwable {
      testOpenInputStreamQuits(open_filtered_filename, new NullPointerException());
   }

   @Test
   public void testOpenFilteredInputStreamOrQuit_filename_all_returnsStream() throws Throwable {
      testOpenInputStreamOrQuit_returnsStream(open_filtered_filename);
   }

   @Test
   public void testOpenFilteredInputStreamOrQuit_filename_defaults() throws Throwable {
      testOpenInputStreamOrQuit_usesDefaults(new Opener() {
         public InputStream openInner(String filename) throws FileNotFoundException {
            return InputHelper.openFilteredInputStreamOrQuit(filename, MOCK_STREAM_MAP, System.err,
                  1);
         }

         public InputStream openOuter(String filename) {
            return InputHelper.openFilteredInputStreamOrQuit(filename, MOCK_STREAM_MAP);
         }
      });
   }

   //
   // beta OpenUnfilteredInputStream(filename, streamMap)
   //

   private Opener open_unfiltered_filename = new Opener() {
      public InputStream openInner(String filename) throws FileNotFoundException {
         return InputHelper.openUnfilteredInputStream(filename, MOCK_STREAM_MAP);
      }

      public InputStream openOuter(String filename) {
         return InputHelper.openUnfilteredInputStreamOrQuit(filename, MOCK_STREAM_MAP, MOCK_ERR, RET_VAL);
      }
   };

   @Test
   public void testOpenUnfilteredInputStreamOrQuit_filename_quitsOnFileNotFound() throws Throwable {
      testOpenInputStreamQuits(open_unfiltered_filename, mock(FileNotFoundException.class));
   }

   @Test
   public void testOpenUnfilteredInputStreamOrQuit_filename_quitsOnFilterFactoryException() throws Throwable {
      testOpenInputStreamQuits(open_unfiltered_filename, mock(InputHelper.FilterFactory.FilterFactoryException.class));
   }

   @Test(expected = NullPointerException.class)
   public void testOpenUnfilteredInputStreamOrQuit_filename_passesOtherExceptions() throws Throwable {
      testOpenInputStreamQuits(open_unfiltered_filename, new NullPointerException());
   }

   @Test
   public void testOpenUnfilteredInputStreamOrQuit_filename_all_returnsStream() throws Throwable {
      testOpenInputStreamOrQuit_returnsStream(open_unfiltered_filename);
   }

   @Test
   public void testOpenUnfilteredInputStreamOrQuit_filename_defaults() throws Throwable {
      testOpenInputStreamOrQuit_usesDefaults(new Opener() {
         public InputStream openInner(String filename) throws FileNotFoundException {
            return InputHelper.openUnfilteredInputStreamOrQuit(filename, MOCK_STREAM_MAP, System.err,
                  1);
         }

         public InputStream openOuter(String filename) {
            return InputHelper.openUnfilteredInputStreamOrQuit(filename, MOCK_STREAM_MAP);
         }
      });
   }

   //
   // beta OpenMappedInputStream(filename, streamMap)
   //

   private Opener open_mapped_filename = new Opener() {
      public InputStream openInner(String filename) throws FileNotFoundException {
         return InputHelper.openMappedInputStream(filename, MOCK_FILTER_MAP);
      }

      public InputStream openOuter(String filename) {
         return InputHelper.openMappedInputStreamOrQuit(filename, MOCK_FILTER_MAP, MOCK_ERR, RET_VAL);
      }
   };

   @Test
   public void testOpenMappedInputStreamOrQuit_filename_quitsOnFileNotFound() throws Throwable {
      testOpenInputStreamQuits(open_mapped_filename, mock(FileNotFoundException.class));
   }

   @Test
   public void testOpenMappedInputStreamOrQuit_filename_quitsOnFilterFactoryException() throws Throwable {
      testOpenInputStreamQuits(open_mapped_filename, mock(InputHelper.FilterFactory.FilterFactoryException.class));
   }

   @Test(expected = NullPointerException.class)
   public void testOpenMappedInputStreamOrQuit_filename_passesOtherExceptions() throws Throwable {
      testOpenInputStreamQuits(open_mapped_filename, new NullPointerException());
   }

   @Test
   public void testOpenMappedInputStreamOrQuit_filename_all_returnsStream() throws Throwable {
      testOpenInputStreamOrQuit_returnsStream(open_mapped_filename);
   }

   @Test
   public void testOpenMappedInputStreamOrQuit_filename_defaults() throws Throwable {
      testOpenInputStreamOrQuit_usesDefaults(new Opener() {
         public InputStream openInner(String filename) throws FileNotFoundException {
            return InputHelper.openMappedInputStreamOrQuit(filename, MOCK_FILTER_MAP, System.err,
                  1);
         }

         public InputStream openOuter(String filename) {
            return InputHelper.openMappedInputStreamOrQuit(filename, MOCK_FILTER_MAP);
         }
      });
   }

   //
   // beta OpenUnmappedInputStream(filename, streamMap)
   //

   private Opener open_unmapped_filename = new Opener() {
      public InputStream openInner(String filename) throws FileNotFoundException {
         return InputHelper.openUnmappedInputStream(filename, MOCK_FILTER_MAP);
      }

      public InputStream openOuter(String filename) {
         return InputHelper.openUnmappedInputStreamOrQuit(filename, MOCK_FILTER_MAP, MOCK_ERR, RET_VAL);
      }
   };

   @Test
   public void testOpenUnmappedInputStreamOrQuit_filename_quitsOnFileNotFound() throws Throwable {
      testOpenInputStreamQuits(open_unmapped_filename, mock(FileNotFoundException.class));
   }

   @Test
   public void testOpenUnmappedInputStreamOrQuit_filename_quitsOnFilterFactoryException() throws Throwable {
      testOpenInputStreamQuits(open_unmapped_filename, mock(InputHelper.FilterFactory.FilterFactoryException.class));
   }

   @Test(expected = NullPointerException.class)
   public void testOpenUnmappedInputStreamOrQuit_filename_passesOtherExceptions() throws Throwable {
      testOpenInputStreamQuits(open_unmapped_filename, new NullPointerException());
   }

   @Test
   public void testOpenUnmappedInputStreamOrQuit_filename_all_returnsStream() throws Throwable {
      testOpenInputStreamOrQuit_returnsStream(open_unmapped_filename);
   }

   @Test
   public void testOpenUnmappedInputStreamOrQuit_filename_defaults() throws Throwable {
      testOpenInputStreamOrQuit_usesDefaults(new Opener() {
         public InputStream openInner(String filename) throws FileNotFoundException {
            return InputHelper.openUnmappedInputStreamOrQuit(filename, MOCK_FILTER_MAP, System.err,
                  1);
         }

         public InputStream openOuter(String filename) {
            return InputHelper.openUnmappedInputStreamOrQuit(filename, MOCK_FILTER_MAP);
         }
      });
   }


   //
   // file, filterMap
   //

   private void testOpenInputStreamQuits_file(Throwable t) throws FileNotFoundException {
      File file = mock(File.class);

      PrintStream err = mock(PrintStream.class);
      int exitValue = 8675309;

      mockStatic(System.class);

      spy(InputHelper.class);
      doThrow(t).when(InputHelper.class);
      InputHelper.openInputStream(file, MOCK_FILTER_MAP);

      InputHelper.openInputStreamOrQuit(file, MOCK_FILTER_MAP, err, exitValue);

      verifyStatic();
      System.exit(exitValue);

      Mockito.verify(err).printf(anyString(), anyString(), anyString());
   }

   @Test
   public void testOpenInputStreamOrQuit_file_all_quitsOnFileNotFound() throws Throwable {
      testOpenInputStreamQuits_file(new FileNotFoundException());
   }

   @Test
   public void testOpenInputStreamOrQuit_file_all_quitsOnFilterFactoryException() throws Throwable {
      testOpenInputStreamQuits_file(new InputHelper.FilterFactory.FilterFactoryException(null));
   }

   @Test(expected = NullPointerException.class)
   public void testOpenInputStreamOrQuit_file_all_passesOtherExceptions() throws Throwable {
      testOpenInputStreamQuits_file(new NullPointerException());
   }

   @Test
   public void testOpenInputStreamOrQuit_file_all_returnsStream() throws Throwable {
      File file = mock(File.class);

      PrintStream err = mock(PrintStream.class);
      int exitValue = 8675309;

      InputStream expected = mock(InputStream.class);
      mockStatic(System.class);

      spy(InputHelper.class);
      doReturn(expected).when(InputHelper.class);
      InputHelper.openInputStream(file, MOCK_FILTER_MAP);

      InputHelper.openInputStreamOrQuit(file, MOCK_FILTER_MAP, err, exitValue);

      verifyStatic();
      InputHelper.openInputStream(file, MOCK_FILTER_MAP);
   }

   @Test
   public void testOpenInputStreamOrQuit_file_all_defaults() throws Throwable {
      File file = mock(File.class);
      InputStream expected = mock(InputStream.class);

      spy(InputHelper.class);
      doReturn(expected).when(InputHelper.class);
      InputHelper.openInputStreamOrQuit(file, MOCK_FILTER_MAP, System.err, 1);

      InputStream observed = InputHelper.openInputStreamOrQuit(file, MOCK_FILTER_MAP);

      assertEquals(expected, observed);
      verifyStatic();
      InputHelper.openInputStreamOrQuit(file, MOCK_FILTER_MAP, System.err, 1);

   }

   //
   // openFilteredInputStream or quit
   //

   private void testOpenFilteredInputStreamQuits_file(Throwable t) throws FileNotFoundException {
      File file = mock(File.class);

      PrintStream err = mock(PrintStream.class);
      int exitValue = 8675309;

      mockStatic(System.class);

      spy(InputHelper.class);
      doThrow(t).when(InputHelper.class);
      InputHelper.openFilteredInputStream(file);

      InputHelper.openFilteredInputStreamOrQuit(file, err, exitValue);

      verifyStatic();
      System.exit(exitValue);

      Mockito.verify(err).printf(anyString(), anyString(), anyString());
      Mockito.verify(t).getMessage();
   }

   @Test
   public void testOpenFilteredInputStreamOrQuit_file_all_quitsOnFileNotFound() throws Throwable {
      testOpenFilteredInputStreamQuits_file(mock(FileNotFoundException.class));
   }

   @Test
   public void testOpenFilteredInputStreamOrQuit_file_all_quitsOnFilterFactoryException() throws Throwable {
      testOpenFilteredInputStreamQuits_file(mock(InputHelper.FilterFactory.FilterFactoryException.class));
   }

   @Test(expected = NullPointerException.class)
   public void testOpenFilteredInputStreamOrQuit_file_all_passesOtherExceptions() throws Throwable {
      testOpenFilteredInputStreamQuits_file(new NullPointerException());
   }

   @Test
   public void testOpenFilteredInputStreamOrQuit_file_all_returnsStream() throws Throwable {
      File file = mock(File.class);

      PrintStream err = mock(PrintStream.class);
      int exitValue = 8675309;

      InputStream expected = mock(InputStream.class);
      mockStatic(System.class);

      spy(InputHelper.class);
      doReturn(expected).when(InputHelper.class);
      InputHelper.openFilteredInputStream(file);

      InputHelper.openFilteredInputStreamOrQuit(file, err, exitValue);

      verifyStatic();
      InputHelper.openFilteredInputStream(file);
   }

   @Test
   public void testOpenFilteredInputStreamOrQuit_file_all_defaults() throws Throwable {
      File file = mock(File.class);
      InputStream expected = mock(InputStream.class);

      spy(InputHelper.class);
      doReturn(expected).when(InputHelper.class);
      InputHelper.openFilteredInputStreamOrQuit(file, System.err, 1);

      InputStream observed = InputHelper.openFilteredInputStreamOrQuit(file);

      assertEquals(expected, observed);
      verifyStatic();
      InputHelper.openFilteredInputStreamOrQuit(file, System.err, 1);
   }

   ////////////////////////////////////////////////////////////
   //
   // looking for bug in CBZip2InputStream
   //
   ////////////////////////////////////////////////////////////

   // I thought I had found a bug in teh CBZip2 library.
   // So, I coded this up to document it.  By the time I got it
   // coded up, the bug disappeared.
   @Ignore
   @Test
   public void lookingForBug() throws Throwable {
      File tempFile = File.createTempFile("TestBz2Lib", ".bz2");
      String message = "Hello, World! How are you?";
      //
      // Write a string to a file and zip it up
      //

      FileOutputStream fileOut = new FileOutputStream(tempFile);
      // The first two characters of a bzip2 stream must be 'B' and 'Z'
      fileOut.write(0x42);
      fileOut.write(0x5a);

      PrintStream output = new PrintStream(new CBZip2OutputStream(fileOut));
      output.print(message);
      output.close();

      //
      // Now, read the file back in
      //
      FileInputStream fileIn = new FileInputStream(tempFile);
      System.out.println("Should be 'B': " + (char) fileIn.read());
      System.out.println("Should be 'Z': " + (char) fileIn.read());

      CBZip2InputStream input = new CBZip2InputStream(fileIn);
      // make an input array big enough to hold the entire file and then some.
      byte[] bytesIn = new byte[message.length() + 100];
      int bytesRead = input.read(bytesIn);
      System.out.println("Should be " + message.length() + ":  " + bytesRead);
      fileIn.close();
      input.close();

      OutputHelperTest.deleteTempFile(tempFile);
   }

}


