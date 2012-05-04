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

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static edu.gvsu.kurmasz.warszawa.Warszawa.DEFAULT_ERROR_STREAM;
import static edu.gvsu.kurmasz.warszawa.Warszawa.DEFAULT_EXIT_VALUE;

/**
 * Shortcuts for opening {@code InputStream}s.  In particular, this class contains shortcuts for:
 * <ul>
 * <li>opening an {@code InputStream} attached to the standard input using common names like "-" and
 * "stdin"</li>
 * <li>opening and reading from a compressed file</li>
 * <li>attempting to open an {@code InputStream} and quitting on failure</li>
 * </ul>
 *
 * <p>More generally, there are two types of shortcuts:  <em>stream maps</em> and <em>filter maps</em>:</p>
 *
 * <dl>
 * <dt>Stream Maps</dt><dd><em>Stream Maps</em> map filenames onto existing {@code InputStream}s.  This feature is
 * designed
 * primarily map the names "-" and "stdin" to {@code System.in} (thereby allowing users to enter these names at the command
 * line).  However, programmers can use any any {@code Map<String, InputStream>}.  This feature (1) allows users to
 * specify custom names for {@code System.in}, and (2) allows the various {@code open} methods to easily
 * re-reference existing
 * open {@code InputStreams}.</dd>
 *
 * <dt>Filter Maps</dt> <dd><em>Filter maps</em> map file suffixes onto filter-like {@code InputStream}s that
 * pre-process
 * the file being opened.  (For example, the default filter map maps the suffix 'bz2' onto {@code CBZip2InputStream}.)  Filters are
 * designed  primarily to automate the process of opening and decompressing compressed files (bzip2, gzip,
 * etc.). However, they can be used to automate the pre-processing of any {@code InputStream} based on
 * file suffix.</dd>
 * </dl>
 *
 * <p>Note that the {@code open} methods that take {@code File} objects as parameters do not use stream maps.
 * {@code File} objects are designed to describe specific files.  There was little apparent benefit to allowing
 * {@code File} objects to refer to "virtual" files like "-" and "stdin".  In addition, the practice seemed to  seemed to
 * have high potential for confusion.  Users who want to use both stream maps and filter maps can simply call the
 * "filename" version of the method in quesiton and pass it {@code file.getAbsolutePath()}.
 * </p>
 *
 *
 * <p>Yes, I did go a little overboard with the convenience methods. (Once I added the few I use most,
 * it was trivial to add the rest.)</p>
 *
 * <table border="1">
 * <tr><th rowspan="2" colspan="2"></th><th colspan="3">Stream Map</th></tr>
 * <tr>                                               <th>none</th><th>default</th><th>explicit</th></tr>
 * <tr><th rowspan="3">Filter Map</th><th>none</th><td>not provided</td><td>{@link #openMappedInputStream(String)
 * }</td><td>{@link #openUnfilteredInputStream(String, java.util.Map)}</td></tr>
 * <tr>                                <th>default</th><td>{@link #openFilteredInputStream(String)
 * }</td><td>{@link #openMappedAndFilteredInputStream(String)}</td><td>{@link #openFilteredInputStream(String, java.util.Map)}</td></tr>
 * <tr>                                <th>explicit</th><td>{@link #openUnmappedInputStream(String,
 * java.util.Map)}</td><td>{@link #openMappedInputStream(String, java.util.Map)}</td><td>{@link #openInputStream(java.io.File, java.util.Map)}</td></tr>
 * </table>
 *
 * @author Zachary Kurmas
 */
// Created  9/20/11 at 10:45 AM
// (C) Zachary Kurmas 2011


public class InputHelper {

   private InputHelper() {
   }

   /**
    * An immutable copy of the map returned by {@link #makeDefaultInputStreamMap()}
    */
   public static final Map<String, InputStream> DEFAULT_INPUT_STREAM_MAP =
         java.util.Collections.unmodifiableMap(makeDefaultInputStreamMap());


   /**
    * Generates a map of common names for the standard input to {@code System.in}.
    *
    * @return the map
    */
   public static Map<String, InputStream> makeDefaultInputStreamMap() {
      HashMap<String, InputStream> map = new HashMap<String, InputStream>();
      map.put("-", System.in);
      map.put("stdin", System.in);
      map.put("STDIN", System.in);
      return map;
   }

   /**
    * Used to wrap {@code InputStreams} with the desired filter.
    */
   public interface FilterFactory {
      /**
       * Surrounds the {@code InputStream} with another filter-like {@code InputStream}.  Typically used to surround a
       * {@code
       * FileInputStream} with an {@code InputStream} (such as {@code CBZip2InputStream}) that can uncompress files.
       *
       * @param in the base {@code InputStream}
       * @return the new {@code InputStream} surrounding {@code in}
       * @throws FilterFactoryException if there is a problem creating or using the new {@code InputStream}
       */
      InputStream makeFilter(InputStream in) throws FilterFactoryException;

      /**
       * Thrown if there is a problem creating or using the new {@code InputStream} (e.g.,
       * thrown if the user attempts to use a {@code CBZip2InputStream} on data that is not compressed in bzip2
       * format).
       */
      public class FilterFactoryException extends RuntimeException {
         public FilterFactoryException(String message, Throwable t) {
            super(message, t);
         }

         public FilterFactoryException(String message) {
            super(message);
         }
      }
   }

   // package scope to allow test to have access.
   static class Bzip2Factory implements FilterFactory {
      public InputStream makeFilter(InputStream in) throws FilterFactoryException {
         if (in == null) {
            throw new NullPointerException("parameter \"in\" cannot be null.");
         }

         try {
            // Read the first two characters. Should be 'B' and 'Z'
            int c1 = in.read();
            int c2 = in.read();
            if (c1 != 'B' || c2 != 'Z') {
               throw new FilterFactoryException("BZip2 stream does not begin with \"BZ\".");
            }
         } catch (IOException e) {
            throw new FilterFactoryException("Problem reading from underlying stream.", e);
         }

         BufferedInputStream buffer = new BufferedInputStream(in);

         try {
            return new CBZip2InputStream(buffer);
         } catch (NullPointerException e) {
            throw new FilterFactoryException("InputStream is not a valid bzip2 stream.");
         }
      }
   }

   /**
    * Wraps an {@code InputStream} in a {@code CBZip2InputStream} that uncompresses it.
    */
   public static final FilterFactory BZIP2_FACTORY = new Bzip2Factory();

   /**
    * Generates a map of common file suffixes to appropriate {@link FilterFactory} objects.  For example,
    * the default map maps "bz2" to a {@code FilterFactory} that builds a {@code CBZip2InputStream}.
    *
    * @return a map of common suffixes to appropriate {@link FilterFactory} objects.
    */
   public static Map<String, FilterFactory> makeDefaultFilterFactoryMap() {
      HashMap<String, FilterFactory> map = new HashMap<String, FilterFactory>();
      map.put("bz2", BZIP2_FACTORY);
      return map;
   }

   /**
    * An immutable copy of the map returned by {@link #makeDefaultFilterFactoryMap()}
    */
   public static final Map<String, FilterFactory> DEFAULT_FILTER_FACTORY_MAP =
         java.util.Collections.unmodifiableMap(makeDefaultFilterFactoryMap());


   ////////////////////////////////////////////////////////
   //
   // Open input or throw an exception
   //
   ///////////////////////////////////////////////////////

   /**
    * Opens an {@code InputStream} attached to the specified file, using {@code streamMap} and {@code filterMap}.
    * Specifically,
    * <ul>
    * <li>If {@code filename} appears in {@code streamMap}, then return that {@code InputSteam}.</li>
    * <li>If {@code filename}'s suffix appears in {@code filterMap}, then apply that {@code FilterFactory}.</li>
    * <li>If neither condition above applies, then return a new {@code InputStream} attached to the specified file
    * .</li>
    * </ul>
    *
    * <p>Note:  {@code InputStreams} found in {@code streamMap} are not affected by the {@code filterMap} &mdash; even
    * if
    * there is an entry in {@code filterMap} for {@code filename}'s suffix.  It is assumed that the {@code InputStream}s in
    * the {@code streamMap} will already
    * be configured with any desired filtering/pre-processing.
    *
    * @param filename  the name of the file to open (or one of the keys in {@code streamMap}).
    * @param streamMap a map of file names to existing {@code InputStream}s
    * @param filterMap a map of file suffixes to filters that will pre-process the file.
    * @return either the {@code InputStream} in {@code streamMap}, or a new, possibly filtered, {@code InputStream}.
    * @throws java.io.FileNotFoundException if the requested file does not exist.
    * @throws edu.gvsu.kurmasz.warszawa.io.InputHelper.FilterFactory.FilterFactoryException
    *                                       if the specified filter cannot
    *                                       handle the given file.
    */
   public static InputStream openInputStream(String filename,
                                             Map<String, InputStream> streamMap,
                                             Map<String, FilterFactory> filterMap
   ) throws FileNotFoundException {
      if (streamMap != null && streamMap.containsKey(filename)) {
         return streamMap.get(filename);
      } else {
         return openInputStream(new File(filename), filterMap);
      }
   }


   /**
    * Calls {@link #openInputStream(String, java.util.Map, java.util.Map)} with {@link #DEFAULT_INPUT_STREAM_MAP}
    * and {@link #DEFAULT_FILTER_FACTORY_MAP}.
    *
    * @param filename the name of the file to open (or one of the keys in {@code streamMap}).
    * @return either the {@code InputStream} in {@code streamMap}, or a new, possibly filtered, {@code InputStream}.
    * @throws java.io.FileNotFoundException if the requested file does not exist.
    * @throws edu.gvsu.kurmasz.warszawa.io.InputHelper.FilterFactory.FilterFactoryException
    *                                       if the specified filter cannot
    *                                       handle the given file.
    */
   public static InputStream openMappedAndFilteredInputStream(String filename) throws FileNotFoundException {
      return openInputStream(filename, DEFAULT_INPUT_STREAM_MAP, DEFAULT_FILTER_FACTORY_MAP);
   }

   /**
    * Calls {@link #openInputStream(String, java.util.Map, java.util.Map)} with {@link
    * #DEFAULT_FILTER_FACTORY_MAP}.
    *
    * @param filename  the name of the file to open (or one of the keys in {@code streamMap}).
    * @param streamMap a map of file names to existing {@code InputStream}s
    * @return either the {@code InputStream} in {@code streamMap}, or a new, possibly filtered, {@code InputStream}.
    * @throws java.io.FileNotFoundException if the requested file does not exist.
    * @throws edu.gvsu.kurmasz.warszawa.io.InputHelper.FilterFactory.FilterFactoryException
    *                                       if the specified filter cannot
    *                                       handle the given file.
    */
   public static InputStream openFilteredInputStream(String filename,
                                                     Map<String, InputStream> streamMap) throws FileNotFoundException {
      return openInputStream(filename, streamMap, DEFAULT_FILTER_FACTORY_MAP);
   }

   /**
    * Calls {@link #openInputStream(String, java.util.Map, java.util.Map)} with {@code null} and {@link
    * #DEFAULT_FILTER_FACTORY_MAP}.
    *
    * @param filename the name of the file to open (or one of the keys in {@code streamMap}).
    * @return either the {@code InputStream} in {@code streamMap}, or a new, possibly filtered, {@code InputStream}.
    * @throws java.io.FileNotFoundException if the requested file does not exist.
    * @throws edu.gvsu.kurmasz.warszawa.io.InputHelper.FilterFactory.FilterFactoryException
    *                                       if the specified filter cannot
    *                                       handle the given file.
    */
   public static InputStream openFilteredInputStream(String filename) throws FileNotFoundException {
      return openInputStream(filename, null, DEFAULT_FILTER_FACTORY_MAP);
   }

   /**
    * Calls {@link #openInputStream(String, java.util.Map, java.util.Map)} with {@code null}.
    *
    * @param filename  the name of the file to open (or one of the keys in {@code streamMap}).
    * @param streamMap a map of file names to existing {@code InputStream}s
    * @return either the {@code InputStream} in {@code streamMap}, or a new {@code InputStream}.
    * @throws java.io.FileNotFoundException if the requested file does not exist.
    * @throws edu.gvsu.kurmasz.warszawa.io.InputHelper.FilterFactory.FilterFactoryException
    *                                       if the specified filter cannot
    *                                       handle the given file.
    */
   public static InputStream openUnfilteredInputStream(String filename,
                                                       Map<String, InputStream> streamMap) throws
         FileNotFoundException {
      return openInputStream(filename, streamMap, null);
   }


   /**
    * Calls {@link #openInputStream(String, java.util.Map, java.util.Map)} with {@link
    * #DEFAULT_INPUT_STREAM_MAP}.
    *
    * @param filename  the name of the file to open (or one of the keys in {@code streamMap}).
    * @param filterMap a map of file suffixes to filters that will pre-process the file.
    * @return either the {@code InputStream} in {@code streamMap}, or a new, possibly filtered, {@code InputStream}.
    * @throws java.io.FileNotFoundException if the requested file does not exist.
    * @throws edu.gvsu.kurmasz.warszawa.io.InputHelper.FilterFactory.FilterFactoryException
    *                                       if the specified filter cannot
    *                                       handle the given file.
    */
   public static InputStream openMappedInputStream(String filename,
                                                   Map<String, FilterFactory> filterMap) throws FileNotFoundException {
      return openInputStream(filename, DEFAULT_INPUT_STREAM_MAP, filterMap);
   }


   /**
    * Calls {@link #openInputStream(String, java.util.Map, java.util.Map)} with {@link
    * #DEFAULT_INPUT_STREAM_MAP} and {@code null}.
    *
    * @param filename the name of the file to open (or one of the keys in {@code streamMap}).
    * @return either the {@code InputStream} in {@code streamMap}, or a new, possibly filtered, {@code InputStream}.
    * @throws java.io.FileNotFoundException if the requested file does not exist.
    * @throws edu.gvsu.kurmasz.warszawa.io.InputHelper.FilterFactory.FilterFactoryException
    *                                       if the specified filter cannot
    *                                       handle the given file.
    */
   public static InputStream openMappedInputStream(String filename) throws FileNotFoundException {
      return openInputStream(filename, DEFAULT_INPUT_STREAM_MAP, null);
   }


   /**
    * Calls {@link #openInputStream(String, java.util.Map, java.util.Map)} with {@code null}.
    *
    * @param filename  the name of the file to open (or one of the keys in {@code streamMap}).
    * @param filterMap a map of file suffixes to filters that will pre-process the file.
    * @return a new, possibly filtered, {@code InputStream}.
    * @throws java.io.FileNotFoundException if the requested file does not exist.
    * @throws edu.gvsu.kurmasz.warszawa.io.InputHelper.FilterFactory.FilterFactoryException
    *                                       if the specified filter cannot
    *                                       handle the given file.
    */
   public static InputStream openUnmappedInputStream(String filename,
                                                     Map<String, FilterFactory> filterMap) throws FileNotFoundException {
      return openInputStream(filename, null, filterMap);
   }


   /**
    * Opens an {@code InputStream} attached to the specified file using the {@code filterMap}.
    * Specifically, if the {@code file}'s suffix appears in {@code filterMap}, then apply that {@code FilterFactory},
    * otherwise, return a new {@code InputStream} attached to the specified file
    *
    * @param file      the name of the file to open
    * @param filterMap a map of file suffixes to filters that will pre-process the file.
    * @return a new, possibly filtered, {@code InputStream}.
    * @throws FileNotFoundException if the file cannot be opened.
    * @throws edu.gvsu.kurmasz.warszawa.io.InputHelper.FilterFactory.FilterFactoryException
    *                               if the specified filter cannot
    *                               handle the given file.
    */
   public static InputStream openInputStream(File file, Map<String, FilterFactory> filterMap) throws
         FileNotFoundException {
      String fileSuffix = FileHelper.getSuffix(file.getName());
      FileInputStream base = new FileInputStream(file);
      if (filterMap == null || !filterMap.containsKey(fileSuffix)) {
         return base;
      } else {
         try {
            return filterMap.get(fileSuffix).makeFilter(base);
         } catch (FilterFactory.FilterFactoryException e) {
            try {
               base.close();
            } catch (IOException e1) {
               String message = "Here's what probably happened: " +
                     "The program attempted to use a bzip2 filter to open a file not in bzip2 format " +
                     "then attempted to close the file handle before passing up the FilterFactoryException. " +
                     "For some reason, the close() method threw and IO exception.  Since I can't imagine how this " +
                     "could happen, I'm recasting this IOException as an unchecked exception";
               throw new RuntimeException(message, e1);
            }
            throw e;
         }
      }
   }

   /**
    * Calls {@link #openInputStream(java.io.File, java.util.Map)} with {@link #DEFAULT_FILTER_FACTORY_MAP}
    *
    * @param file the file to open
    * @return a new, possibly filtered, {@code InputStream}.
    * @throws FileNotFoundException if the file cannot be opened.
    * @throws edu.gvsu.kurmasz.warszawa.io.InputHelper.FilterFactory.FilterFactoryException
    *                               if the specified filter cannot
    *                               handle the given file.
    */
   public static InputStream openFilteredInputStream(File file) throws FileNotFoundException {
      return openInputStream(file, DEFAULT_FILTER_FACTORY_MAP);
   }

   // Note:  There is no openInputStream(File file) or openUnfilteredInputStram(File file) because calling this method
   // would  have no benefits over just typing "new FileInputStream(file)"


   ////////////////////////////////////////////////////////
   //
   // Open input or quit
   //
   ///////////////////////////////////////////////////////

   private static void quit(String filename, PrintStream error, int exitValue, Throwable e) {
      error.printf("Cannot open \"%s\" for reading because %s.",
            filename, e.getMessage());
      System.exit(exitValue);
   }

   private interface Opener {
      InputStream open() throws FileNotFoundException;

   }

   private static InputStream openOrQuit(Opener opener, String filename, PrintStream error, int exitValue) {
      try {
         return opener.open();
      } catch (FileNotFoundException e) {
         quit(filename, error, exitValue, e);
         return null;
      } catch (FilterFactory.FilterFactoryException e) {
         quit(filename, error, exitValue, e);

         // Quit calls System.exit(0), so this line of code should never run.
         assert false : "This line of code should never run.";
         return null;
      }
   }

   /**
    * Calls {@link #openInputStream(String, java.util.Map, java.util.Map)} and exits if the file can't be opened.
    *
    * @param filename  the name of the file to open (or one of the keys in {@code streamMap}).
    * @param streamMap a map of file names to existing {@code InputStream}s
    * @param filterMap a map of file suffixes to filters that will pre-process the file.
    * @param error     the {@code PrintStream} to which to write any errors.
    * @param exitValue the value to pass to {@code System.exit} in the event of an error
    * @return either the {@code InputStream} in {@code streamMap}, or a new, possibly filtered, {@code InputStream}.
    */
   public static InputStream openInputStreamOrQuit(final String filename,
                                                   final Map<String, InputStream> streamMap,
                                                   final Map<String, FilterFactory> filterMap,
                                                   PrintStream error, int exitValue) {
      return openOrQuit(new Opener() {
         public InputStream open() throws FileNotFoundException {
            return openInputStream(filename, streamMap, filterMap);
         }
      }, filename, error, exitValue);
   }


   /**
    * Calls {@link #openInputStream(String, java.util.Map, java.util.Map)} and exits if the file can't be opened.
    *
    * @param filename  the name of the file to open (or one of the keys in {@code streamMap}).
    * @param streamMap a map of file names to existing {@code InputStream}s
    * @param filterMap a map of file suffixes to filters that will pre-process the file.
    * @return either the {@code InputStream} in {@code streamMap}, or a new, possibly filtered, {@code InputStream}.
    */
   public static InputStream openInputStreamOrQuit(String filename,
                                                   Map<String, InputStream> streamMap,
                                                   Map<String, FilterFactory> filterMap) {
      return openInputStreamOrQuit(filename, streamMap, filterMap, DEFAULT_ERROR_STREAM,
            DEFAULT_EXIT_VALUE);
   }


   /**
    * Calls {@link #openInputStream(String, java.util.Map, java.util.Map)} with {@link #DEFAULT_INPUT_STREAM_MAP}
    * and {@link #DEFAULT_FILTER_FACTORY_MAP} and exists if the file can't be opened.
    *
    * @param error     the {@code PrintStream} to which to write any errors.
    * @param exitValue the value to pass to {@code System.exit} in the event of an error
    * @param filename  the name of the file to open (or one of the keys in {@code streamMap}).
    * @return either the {@code InputStream} in {@code streamMap}, or a new, possibly filtered, {@code InputStream}.
    */
   public static InputStream openMappedAndFilteredInputStreamOrQuit(final String filename,
                                                                    PrintStream error, int exitValue) {
      return openOrQuit(new Opener() {
         public InputStream open() throws FileNotFoundException {
            return openMappedAndFilteredInputStream(filename);
         }
      }, filename, error, exitValue);
   }

   /**
    * Calls {@link #openInputStream(String, java.util.Map, java.util.Map)} with {@link #DEFAULT_INPUT_STREAM_MAP}
    * and {@link #DEFAULT_FILTER_FACTORY_MAP} and exists if the file can't be opened.
    *
    * @param filename the name of the file to open (or one of the keys in {@code streamMap}).
    * @return either the {@code InputStream} in {@code streamMap}, or a new, possibly filtered, {@code InputStream}.
    */
   public static InputStream openMappedAndFilteredInputStreamOrQuit(final String filename) {
      return openMappedAndFilteredInputStreamOrQuit(filename, DEFAULT_ERROR_STREAM,
           DEFAULT_EXIT_VALUE);
   }


   /**
    * Calls {@link #openFilteredInputStream(String, java.util.Map)} and exists if the file can't be opened.
    *
    * @param filename  the name of the file to open (or one of the keys in {@code streamMap}).
    * @param streamMap a map of file names to existing {@code InputStream}s
    * @param error     the {@code PrintStream} to which to write any errors.
    * @param exitValue the value to pass to {@code System.exit} in the event of an error
    * @return either the {@code InputStream} in {@code streamMap}, or a new, possibly filtered, {@code InputStream}.
    */
   public static InputStream openFilteredInputStreamOrQuit(final String filename,
                                                           final Map<String, InputStream> streamMap,
                                                           PrintStream error, int exitValue) {

      return openOrQuit(new Opener() {
         public InputStream open() throws FileNotFoundException {
            return InputHelper.openFilteredInputStream(filename, streamMap);
         }
      }, filename, error, exitValue);
   }

   /**
    * Calls {@link #openFilteredInputStream(String, java.util.Map)} and exists if the file can't be opened.
    *
    * @param filename  the name of the file to open (or one of the keys in {@code streamMap}).
    * @param streamMap a map of file names to existing {@code InputStream}s
    * @return either the {@code InputStream} in {@code streamMap}, or a new, possibly filtered, {@code InputStream}.
    */
   public static InputStream openFilteredInputStreamOrQuit(String filename,
                                                           Map<String, InputStream> streamMap) {
      return openFilteredInputStreamOrQuit(filename, streamMap, DEFAULT_ERROR_STREAM, DEFAULT_EXIT_VALUE);
   }


   /**
    * Calls {@link #openUnfilteredInputStream(String, java.util.Map)} and exists if the file can't be opened.
    *
    * @param filename  the name of the file to open (or one of the keys in {@code streamMap}).
    * @param streamMap a map of file names to existing {@code InputStream}s
    * @param error     the {@code PrintStream} to which to write any errors.
    * @param exitValue the value to pass to {@code System.exit} in the event of an error
    * @return either the {@code InputStream} in {@code streamMap}, or a new {@code InputStream}.
    */
   public static InputStream openUnfilteredInputStreamOrQuit(final String filename,
                                                             final Map<String, InputStream> streamMap,
                                                             PrintStream error, int exitValue) {

      return openOrQuit(new Opener() {
         public InputStream open() throws FileNotFoundException {
            return InputHelper.openUnfilteredInputStream(filename, streamMap);
         }
      }, filename, error, exitValue);
   }

   /**
    * Calls {@link #openUnfilteredInputStream(String, java.util.Map)} and exists if the file can't be opened.
    *
    * @param filename  the name of the file to open (or one of the keys in {@code streamMap}).
    * @param streamMap a map of file names to existing {@code InputStream}s
    * @return either the {@code InputStream} in {@code streamMap}, or a new {@code InputStream}.
    */
   public static InputStream openUnfilteredInputStreamOrQuit(String filename,
                                                             Map<String, InputStream> streamMap) {
      return openUnfilteredInputStreamOrQuit(filename, streamMap, DEFAULT_ERROR_STREAM, DEFAULT_EXIT_VALUE);
   }

   /**
    * Calls {@link #openMappedInputStream(String, java.util.Map)} and exists if the file can't be opened.
    *
    * @param filename  the name of the file to open (or one of the keys in {@code streamMap}).
    * @param filterMap a map of file suffixes to filters that will pre-process the file.
    * @param error     the {@code PrintStream} to which to write any errors.
    * @param exitValue the value to pass to {@code System.exit} in the event of an error
    * @return either the {@code InputStream} in the default stream map or a new, possibly filtered, {@code InputStream}.
    */
   public static InputStream openMappedInputStreamOrQuit(final String filename,
                                                         final Map<String, FilterFactory> filterMap,
                                                         PrintStream error, int exitValue) {

      return openOrQuit(new Opener() {
         public InputStream open() throws FileNotFoundException {
            return InputHelper.openMappedInputStream(filename, filterMap);
         }
      }, filename, error, exitValue);
   }

   /**
    * Calls {@link #openMappedInputStream(String, java.util.Map)} and exists if the file can't be opened.
    *
    * @param filename  the name of the file to open (or one of the keys in {@code streamMap}).
    * @param filterMap a map of file suffixes to filters that will pre-process the file.
    * @return either the {@code InputStream} in the default stream map or a new, possibly filtered, {@code InputStream}.
    */
   public static InputStream openMappedInputStreamOrQuit(String filename,
                                                         Map<String, FilterFactory> filterMap) {
      return openMappedInputStreamOrQuit(filename, filterMap, DEFAULT_ERROR_STREAM, DEFAULT_EXIT_VALUE);
   }


   /**
    * Calls {@link #openUnmappedInputStream(String, java.util.Map)} and exists if the file can't be opened.
    *
    * @param filename  the name of the file to open (or one of the keys in {@code streamMap}).
    * @param filterMap a map of file suffixes to filters that will pre-process the file.
    * @param error     the {@code PrintStream} to which to write any errors.
    * @param exitValue the value to pass to {@code System.exit} in the event of an error
    * @return a new, possibly filtered, {@code InputStream}.
    */
   public static InputStream openUnmappedInputStreamOrQuit(final String filename,
                                                           final Map<String, FilterFactory> filterMap,
                                                           PrintStream error, int exitValue) {

      return openOrQuit(new Opener() {
         public InputStream open() throws FileNotFoundException {
            return InputHelper.openUnmappedInputStream(filename, filterMap);
         }
      }, filename, error, exitValue);
   }

   /**
    * Calls {@link #openUnmappedInputStream(String, java.util.Map)} and exists if the file can't be opened.
    *
    * @param filename  the name of the file to open (or one of the keys in {@code streamMap}).
    * @param filterMap a map of file suffixes to filters that will pre-process the file.
    * @return a new, possibly filtered, {@code InputStream}.
    */
   public static InputStream openUnmappedInputStreamOrQuit(String filename,
                                                           Map<String, FilterFactory> filterMap) {
      return openUnmappedInputStreamOrQuit(filename, filterMap, DEFAULT_ERROR_STREAM, DEFAULT_EXIT_VALUE);
   }


   //
   // File
   //


   /**
    * Calls {@link #openInputStream(java.io.File, java.util.Map)} and exits if the file can't be opened.
    *
    * @param file      the {@code file} to open (or one of the keys in {@code streamMap}).
    * @param filterMap a map of file suffixes to filters that will pre-process the file.
    * @param error     the {@code PrintStream} to which to write any errors.
    * @param exitValue the value to pass to {@code System.exit} in the event of an error
    * @return either the {@code InputStream} in {@code streamMap}, or a new, possibly filtered, {@code InputStream}.
    */
   public static InputStream openInputStreamOrQuit(final File file, final Map<String, FilterFactory> filterMap,
                                                   PrintStream error, int exitValue) {
      return openOrQuit(new Opener() {
         public InputStream open() throws FileNotFoundException {
            return openInputStream(file, filterMap);
         }
      }, file.getAbsolutePath(), error, exitValue);
   }


   /**
    * Calls {@link #openInputStream(java.io.File, java.util.Map)} and exits if the file can't be opened.
    *
    * @param file      the {@code file} to open (or one of the keys in {@code streamMap}).
    * @param filterMap a map of file suffixes to filters that will pre-process the file.
    * @return either the {@code InputStream} in {@code streamMap}, or a new, possibly filtered, {@code InputStream}.
    */
   public static InputStream openInputStreamOrQuit(final File file, final Map<String, FilterFactory> filterMap) {
      return openInputStreamOrQuit(file, filterMap, DEFAULT_ERROR_STREAM, DEFAULT_EXIT_VALUE);
   }

   /**
    * Calls {@link #openFilteredInputStream(java.io.File)}  and exits if the file can't be opened.
    *
    * @param file      the {@code file} to open (or one of the keys in {@code streamMap}).
    * @param error     the {@code PrintStream} to which to write any errors.
    * @param exitValue the value to pass to {@code System.exit} in the event of an error
    * @return either the {@code InputStream} in {@code streamMap}, or a new, possibly filtered, {@code InputStream}.
    */
   public static InputStream openFilteredInputStreamOrQuit(final File file, PrintStream error, int exitValue) {
      return openOrQuit(new Opener() {
         public InputStream open() throws FileNotFoundException {
            return InputHelper.openFilteredInputStream(file);
         }
      }, file.getAbsolutePath(), error, exitValue);
   }

   /**
    * Calls {@link #openFilteredInputStream(java.io.File)}  and exits if the file can't be opened.
    *
    * @param file the {@code file} to open (or one of the keys in {@code streamMap}).
    * @return either the {@code InputStream} in {@code streamMap}, or a new, possibly filtered, {@code InputStream}.
    */
   public static InputStream openFilteredInputStreamOrQuit(File file) {
      return openFilteredInputStreamOrQuit(file, DEFAULT_ERROR_STREAM, DEFAULT_EXIT_VALUE);
   }
}
