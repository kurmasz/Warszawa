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

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Shortcuts for opening {@code PrintWriter}s.  In particular, this class contains shortcuts for
 * <ul>
 * <li>Opening a {@code PrintWriter} with autoflush either on or off</li>
 * <li>Opening a {@code PrintWriter} with a specified character set</li>
 * <li>Opening a {@code PrintWriter} to the standard output or standard error using common names like "-" and
 * "stderr".</li>
 * <li>Attempting to open a {@code PrintWriter} and quitting on failure</li>
 * </ul>
 *
 * @author Zachary Kurmas
 */
// Created  9/9/11 at 11:34 AM
// (C) Zachary Kurmas 2011

public class OutputHelper {

   /**
    * Error messages are written here by default.
    */
   public static final PrintStream DEFAULT_ERROR_STREAM = System.err;

   /**
    * Value passed to {@code System.exit} by default
    */
   public static final int DEFAULT_EXIT_VALUE = 1;

   private static final boolean DEFAULT_AUTOFLUSH = false;

   /**
    * An immutable copy of the map returned by {@link #makeDefaultOutputStreamMap()}
    */
   public static final Map<String, OutputStream> DEFAULT_OUTPUT_STREAM_MAP =
         java.util.Collections.unmodifiableMap(makeDefaultOutputStreamMap());

   /**
    * Generates a map of common names for the standard output and standard error to {@code System.out} and {@code
    * System.err} respectively.
    *
    * @return the map
    */
   public static Map<String, OutputStream> makeDefaultOutputStreamMap() {
      HashMap<String, OutputStream> map = new HashMap<String, OutputStream>();
      map.put("-", System.out);
      map.put("stdout", System.out);
      map.put("STDOUT", System.out);
      map.put("stderr", System.err);
      map.put("STDERR", System.err);
      return map;
   }
   ////////////////////////////////////////////////////////
   //
   // Open output or throw an exception
   //
   ///////////////////////////////////////////////////////


   /**
    * Returns the {@code OutputStream} contained in the {@code map}, if present,
    * or creates a new {@code OutputStream }attached to the specified file.  {@link #DEFAULT_OUTPUT_STREAM_MAP} maps
    * common names for the standard output and standard error to {@code System.out} and {@code System.err} respectively.
    *
    * @param filename the name of the file to open, or one of the keys in {@code map}.
    * @param map      a map of names to existing {@code OutputStreams}
    * @return either the {@code OutputStream} in the map, or a new {@code OutputStream}.
    * @throws FileNotFoundException if the requested file does not exist.
    */
   public static OutputStream getOutputStream(String filename,
                                              Map<String, OutputStream> map) throws FileNotFoundException {
      if (map != null && map.containsKey(filename)) {
         return map.get(filename);
      } else {
         return new FileOutputStream(filename);
      }
   }

   /**
    * Returns a {@code PrintWriter} attached to the {@code stream} with the the specified character set and autoflush.
    *
    * @param stream    the {@code OutputStream} to which to write
    * @param charset   the desired character set
    * @param autoflush if {@code true}, the {@code println}, {@code printf}, or {@code format} methods will flush the
    *                  output buffer.
    * @return the new {@code PrintWriter}
    */
   public static PrintWriter openWriter(OutputStream stream, Charset charset,
                                        boolean autoflush) {
      return new PrintWriter(new java.io.BufferedWriter(
            new java.io.OutputStreamWriter(stream, charset)), autoflush);
   }


    /**
    * Returns a {@code PrintWriter} attached to the {@code stream} with the the default character set and autoflush.
    *
    * @param stream    the {@code OutputStream} to which to write
    * @param autoflush if {@code true}, the {@code println}, {@code printf}, or {@code format} methods will flush the
    *                  output buffer.
    * @return the new {@code PrintWriter}
    */
   public static PrintWriter openWriter(OutputStream stream, boolean autoflush) {
      return openWriter(stream, Charset.defaultCharset(), autoflush);
   }


   //
   // Open from file
   //


   /**
    * Returns a {@code PrintWriter} attached to the {@code file} with the the specified character set and autoflush.
    *
    * @param file      the {@code File} to which to write
    * @param charset   the desired character set
    * @param autoflush if {@code true}, the {@code println}, {@code printf}, or {@code format} methods will flush the
    *                  output buffer.
    * @return the new {@code PrintWriter}
    * @throws FileNotFoundException if {@code file} cannot be opened for writing.
    */
   public static PrintWriter openWriter(File file, Charset charset, boolean autoflush) throws FileNotFoundException {
      return openWriter(new FileOutputStream(file), charset, autoflush);
   }

   /**
    * Returns a {@code PrintWriter} attached to the {@code file} with the the specified character set and autoflush.
    *
    * @param file      the {@code File} to which to write
    * @param charset   the desired character set
    * @param autoflush if {@code true}, the {@code println}, {@code printf}, or {@code format} methods will flush the
    *                  output buffer.
    * @return the new {@code PrintWriter}
    * @throws FileNotFoundException if {@code file} cannot be opened for writing.
    */
   public static PrintWriter openWriter(File file, String charset, boolean autoflush) throws FileNotFoundException {
      return openWriter(file, Charset.forName(charset), autoflush);
   }

   /**
    * Returns a {@code PrintWriter} attached to the {@code file} with the the default character set specified
    * autoflush.
    *
    * @param file      the {@code File} to which to write
    * @param autoflush if {@code true}, the {@code println}, {@code printf}, or {@code format} methods will flush the
    *                  output buffer.
    * @return the new {@code PrintWriter}
    * @throws FileNotFoundException if {@code file} cannot be opened for writing.
    */
   public static PrintWriter openWriter(File file, boolean autoflush) throws FileNotFoundException {
      return openWriter(file, Charset.defaultCharset(), autoflush);
   }

   /**
    * Returns a {@code PrintWriter} attached to the {@code file} with the the specified character set and without
    * automatic line flushing.
    *
    * @param file    the {@code File} to which to write
    * @param charset the desired character set
    * @return the new {@code PrintWriter}
    * @throws FileNotFoundException if {@code file} cannot be opened for writing.
    */
   public static PrintWriter openWriter(File file, String charset) throws FileNotFoundException {
      return openWriter(file, charset, DEFAULT_AUTOFLUSH);
   }


   //
   // Open from filename
   //

   /**
    * Returns a {@code PrintWriter} attached to the file with the the specified character set and autoflush.
    *
    * @param filename  the {@code File} to which to write
    * @param charset   the desired character set
    * @param autoflush if {@code true}, the {@code println}, {@code printf}, or {@code format} methods will flush the
    *                  output buffer.
    * @return the new {@code PrintWriter}
    * @throws FileNotFoundException if {@code file} cannot be opened for writing.
    */
   public static PrintWriter openWriter(String filename, Charset charset,
                                        boolean autoflush) throws FileNotFoundException {
      return openWriter(new File(filename), charset, autoflush);
   }

   /**
    * Returns a {@code PrintWriter} attached to the file with the the specified character set and autoflush.
    *
    * @param filename  the {@code File} to which to write
    * @param charset   the desired character set
    * @param autoflush if {@code true}, the {@code println}, {@code printf}, or {@code format} methods will flush the
    *                  output buffer.
    * @return the new {@code PrintWriter}
    * @throws FileNotFoundException if {@code file} cannot be opened for writing.
    */
   public static PrintWriter openWriter(String filename, String charset,
                                        boolean autoflush) throws FileNotFoundException {
      return openWriter(filename, Charset.forName(charset), autoflush);
   }

   /**
    * Returns a {@code PrintWriter} attached to the file with the the default character set and specified autoflush.
    *
    * @param filename  the {@code File} to which to write
    * @param autoflush if {@code true}, the {@code println}, {@code printf}, or {@code format} methods will flush the
    *                  output buffer.
    * @return the new {@code PrintWriter}
    * @throws FileNotFoundException if {@code file} cannot be opened for writing.
    */
   public static PrintWriter openWriter(String filename, boolean autoflush) throws FileNotFoundException {
      return openWriter(filename, Charset.defaultCharset(), autoflush);
   }

   /**
    * Returns a {@code PrintWriter} attached to the file with the the specified character set and no automatic line flushing.
    *
    * @param filename the {@code File} to which to write
    * @param charset  the desired character set
    * @return the new {@code PrintWriter}
    * @throws FileNotFoundException if {@code file} cannot be opened for writing.
    */
   public static PrintWriter openWriter(String filename, String charset) throws FileNotFoundException {
      return openWriter(filename, charset, DEFAULT_AUTOFLUSH);
   }

   //
   // Open from mapped filename
   //

   /**
    * Returns a {@code PrintWriter} attached to either the named file, or the {@code OutputStream} specified in
    * {@code map}  with the the specified character set and autoflush.
    *
    * @param filename  the {@code File} to which to write
    * @param map       a map of filenames to exisiting {@code OutputStreams}.  One use of this feature is to map filenames
    *                  like "-" and "stderr" onto the standard output.
    * @param charset   the desired character set
    * @param autoflush if {@code true}, the {@code println}, {@code printf}, or {@code format} methods will flush the
    *                  output buffer.
    * @return the new {@code PrintWriter}
    * @throws FileNotFoundException if {@code file} cannot be opened for writing.
    */
   public static PrintWriter openWriter(String filename, Map<String, OutputStream> map, Charset charset,
                                        boolean autoflush) throws FileNotFoundException {
      return openWriter(getOutputStream(filename, map), charset, autoflush);
   }

   /**
    * Returns a {@code PrintWriter} attached to either the named file, or the {@code OutputStream} specified in
    * {@code map}  with the the specified character set and autoflush.
    *
    * @param filename  the {@code File} to which to write
    * @param map       a map of filenames to exisiting {@code OutputStreams}.  One use of this feature is to map filenames
    *                  like "-" and "stderr" onto the standard output.
    * @param charset   the desired character set
    * @param autoflush if {@code true}, the {@code println}, {@code printf}, or {@code format} methods will flush the
    *                  output buffer.
    * @return the new {@code PrintWriter}
    * @throws FileNotFoundException if {@code file} cannot be opened for writing.
    */
   public static PrintWriter openWriter(String filename, Map<String, OutputStream> map, String charset,
                                        boolean autoflush) throws FileNotFoundException {
      return openWriter(filename, map, Charset.forName(charset), autoflush);
   }

   /**
    * calls {@link #openWriter(String, java.util.Map, java.nio.charset.Charset, boolean)}  using {@link #DEFAULT_OUTPUT_STREAM_MAP}
    *
    * @param filename  the {@code File} to which to write
    * @param charset   the desired character set
    * @param autoflush if {@code true}, the {@code println}, {@code printf}, or {@code format} methods will flush the
    *                  output buffer.
    * @return the new {@code PrintWriter}
    * @throws FileNotFoundException if {@code file} cannot be opened for writing.
    */
   public static PrintWriter openMappedWriter(String filename, Charset charset,
                                              boolean autoflush) throws FileNotFoundException {
      return openWriter(filename, OutputHelper.DEFAULT_OUTPUT_STREAM_MAP, charset, autoflush);
   }

   /**
    * calls {@link #openWriter(String, java.util.Map, java.lang.String,
    * boolean)} using {@link #DEFAULT_OUTPUT_STREAM_MAP}
    *
    * @param filename  the {@code File} to which to write
    * @param charset   the desired character set
    * @param autoflush if {@code true}, the {@code println}, {@code printf}, or {@code format} methods will flush the
    *                  output buffer.
    * @return the new {@code PrintWriter}
    * @throws FileNotFoundException if {@code file} cannot be opened for writing.
    */
   public static PrintWriter openMappedWriter(String filename, String charset,
                                              boolean autoflush) throws FileNotFoundException {
      return openWriter(filename, OutputHelper.DEFAULT_OUTPUT_STREAM_MAP, charset, autoflush);
   }


   // These methods are ready but untested.
//   public static PrintWriter openWriter(String filename, Map<String, OutputStream> map,
//                                        boolean autoflush) throws FileNotFoundException {
//      return openWriter(getOutputStream(filename, map), Charset.defaultCharset(), autoflush);
//   }
//
//   public static PrintWriter openWriter(String filename, Map<String, OutputStream> map,
//                                        String charset) throws FileNotFoundException {
//      return openWriter(getOutputStream(filename, map), Charset.forName(charset), DEFAULT_AUTOFLUSH);
//   }
//
//
//   public static PrintWriter openMappedWriter(String filename,
//                                              boolean autoflush) throws FileNotFoundException {
//      return openMappedWriter(filename, Charset.defaultCharset(),
//            autoflush);
//   }
//
//   public static PrintWriter openMappedWriter(String filename, String charset) throws FileNotFoundException {
//      return openMappedWriter(filename, charset, DEFAULT_AUTOFLUSH);
//   }


   ////////////////////////////////////////////////////////
   //
   // Open or quit
   //
   ///////////////////////////////////////////////////////

   private static void quit(String filename, PrintStream error, int exitValue, FileNotFoundException e) {
      error.printf("Cannot open \"%s\" for writing because %s.",
            filename, e.getMessage());
      System.exit(exitValue);
   }

   /**
    * Calls {@link #getOutputStream(String, java.util.Map)} and quits if the method cannot open the requested file.
    *
    * @param filename  the name of the file to open, or one of the keys in {@code map}.
    * @param map       a map of names to existing {@code OutputStreams}
    * @param error     the stream to which to write any error messages before quitting.
    * @param exitValue the value passed to {@code System.exit}
    * @return either the {@code OutputStream} in the map, or a new {@code OutputStream}.
    */

   public static OutputStream getOutputStreamOrQuit(String filename,
                                                    Map<String, OutputStream> map,
                                                    PrintStream error, int exitValue) {
      try {
         return getOutputStream(filename, map);
      } catch (FileNotFoundException e) {
         quit(filename, error, exitValue, e);
         return null;
      }
   }

   /**
    * Calls {@link #getOutputStream(String, java.util.Map)} and quits if the method cannot open the requested file.
    * Any error messages are written to {@code System.err} and the process exists with value 1.
    *
    * @param filename the name of the file to open, or one of the keys in {@code map}.
    * @param map      a map of names to existing {@code OutputStreams}
    * @return either the {@code OutputStream} in the map, or a new {@code OutputStream}.
    */

   public static OutputStream getOutputStreamOrQuit(String filename,
                                                    Map<String, OutputStream> map) {
      return getOutputStreamOrQuit(filename, map, DEFAULT_ERROR_STREAM, DEFAULT_EXIT_VALUE);
   }


   private interface Opener {
      PrintWriter open() throws FileNotFoundException;
   }

   private static PrintWriter openOrQuit(Opener opener, String filename, PrintStream error, int exitValue) {
      try {
         return opener.open();
      } catch (FileNotFoundException e) {
         quit(filename, error, exitValue, e);
         return null;
      }
   }

//      private static PrintWriter openOrQuit(Opener opener, String filename) {
//         return openOrQuit(opener, filename, DEFAULT_ERROR_STREAM, DEFAULT_EXIT_VALUE);
//      }
//


   /**
    * Returns a {@code PrintWriter} attached to the {@code file}, or quits if the specified file cannot be opened for
    * writing.  (See {@link #openWriter(java.io.File, String, boolean)}.
    *
    * @param file      the {@code File} to which to write
    * @param charset   the desired character set
    * @param autoflush if {@code true}, the {@code println}, {@code printf}, or {@code format} methods will flush the
    *                  output buffer.
    * @param error     the stream to which to write any error messages before quitting.
    * @param exitValue the value passed to {@code System.exit}
    * @return the new {@code PrintWriter}
    */
   public static PrintWriter openWriterOrQuit(final File file, final Charset charset, final boolean autoflush,
                                              final PrintStream error,
                                              final int exitValue) {
      return openOrQuit(new Opener() {
         public PrintWriter open() throws FileNotFoundException {
            return OutputHelper.openWriter(file, charset, autoflush);
         }
      }, file.getAbsolutePath(), error, exitValue);
   }

   /**
    * Returns a {@code PrintWriter} attached to the {@code file}, or quits if the specified file cannot be opened for
    * writing.  (See {@link #openWriter(java.io.File, String, boolean)}.
    *
    * @param file      the {@code File} to which to write
    * @param charset   the desired character set
    * @param autoflush if {@code true}, the {@code println}, {@code printf}, or {@code format} methods will flush the
    *                  output buffer.
    * @return the new {@code PrintWriter}
    */
   public static PrintWriter openWriterOrQuit(File file, Charset charset, boolean autoflush) {
      return openWriterOrQuit(file, charset, autoflush, DEFAULT_ERROR_STREAM, DEFAULT_EXIT_VALUE);
   }


   /**
    * Returns a {@code PrintWriter} attached to the {@code file}, or quits if the specified file cannot be opened for
    * writing.  (See {@link #openWriter(java.io.File, String, boolean)}.
    *
    * @param filename  the {@code File} to which to write
    * @param charset   the desired character set
    * @param autoflush if {@code true}, the {@code println}, {@code printf}, or {@code format} methods will flush the
    *                  output buffer.
    * @param error     the stream to which to write any error messages before quitting.
    * @param exitValue the value passed to {@code System.exit}
    * @return the new {@code PrintWriter}
    */
   public static PrintWriter openWriterOrQuit(final String filename, final Charset charset, final boolean autoflush,
                                              PrintStream error,
                                              int exitValue) {
      return openOrQuit(new Opener() {
         public PrintWriter open() throws FileNotFoundException {
            return OutputHelper.openWriter(filename, charset, autoflush);
         }
      }, filename, error, exitValue);
   }

   /**
    * Returns a {@code PrintWriter} attached to the {@code file}, or quits if the specified file cannot be opened for
    * writing.  (See {@link #openWriter(java.io.File, String, boolean)}.
    *
    * @param filename  the {@code File} to which to write
    * @param charset   the desired character set
    * @param autoflush if {@code true}, the {@code println}, {@code printf}, or {@code format} methods will flush the
    *                  output buffer.
    * @return the new {@code PrintWriter}
    */
   public static PrintWriter openWriterOrQuit(final String filename, final Charset charset, final boolean autoflush) {
      return openWriterOrQuit(filename, charset, autoflush, DEFAULT_ERROR_STREAM, DEFAULT_EXIT_VALUE);
   }

   /**
    * Returns a {@code PrintWriter} attached to the {@code file}, or quits if the specified file cannot be opened for
    * writing.  (See {@link #openWriter(java.io.File, String, boolean)}.
    *
    * @param filename  the {@code File} to which to write
    * @param charset   the desired character set
    * @param autoflush if {@code true}, the {@code println}, {@code printf}, or {@code format} methods will flush the
    *                  output buffer.
    * @param error     the stream to which to write any error messages before quitting.
    * @param exitValue the value passed to {@code System.exit}
    * @return the new {@code PrintWriter}
    */
   public static PrintWriter openWriterOrQuit(final String filename, final String charset, final boolean autoflush,
                                              PrintStream error,
                                              int exitValue) {
      return openOrQuit(new Opener() {
         public PrintWriter open() throws FileNotFoundException {
            return OutputHelper.openWriter(filename, charset, autoflush);
         }
      }, filename, error, exitValue);
   }

   /**
    * Returns a {@code PrintWriter} attached to the {@code file}, or quits if the specified file cannot be opened for
    * writing.  (See {@link #openWriter(java.io.File, String, boolean)}.
    *
    * @param filename  the {@code File} to which to write
    * @param charset   the desired character set
    * @param autoflush if {@code true}, the {@code println}, {@code printf}, or {@code format} methods will flush the
    *                  output buffer.
    * @return the new {@code PrintWriter}
    */
   public static PrintWriter openWriterOrQuit(final String filename, final String charset, final boolean autoflush) {
      return openWriterOrQuit(filename, charset, autoflush, DEFAULT_ERROR_STREAM, DEFAULT_EXIT_VALUE);
   }


   /**
    * Calls {@link #openMappedWriter(String, String, boolean)}, or quits if the specified file cannot be opened for
    * writing.
    *
    * @param filename  the file to which to write
    * @param charset   the desired character set
    * @param autoflush if {@code true}, the {@code println}, {@code printf}, or {@code format} methods will flush the
    *                  output buffer.
    * @param error     the stream to which to write any error messages before quitting.
    * @param exitValue the value passed to {@code System.exit}
    * @return the new {@code PrintWriter}
    */
   public static PrintWriter openMappedWriterOrQuit(final String filename, final String charset,
                                                    final boolean autoflush,
                                                    PrintStream error,
                                                    int exitValue) {
      return openOrQuit(new Opener() {
         public PrintWriter open() throws FileNotFoundException {
            return OutputHelper.openMappedWriter(filename, charset, autoflush);
         }
      }, filename, error, exitValue);
   }

   /**
    * Calls {@link #openMappedWriter(String, String, boolean)}, or quits if the specified file cannot be opened for
    * writing.
    *
    * @param filename  the file to which to write
    * @param charset   the desired character set
    * @param autoflush if {@code true}, the {@code println}, {@code printf}, or {@code format} methods will flush the
    *                  output buffer.
    * @return the new {@code PrintWriter}
    */
   public static PrintWriter openMappedWriterOrQuit(final String filename, final String charset,
                                                    final boolean autoflush) {
      return openMappedWriterOrQuit(filename, charset, autoflush, DEFAULT_ERROR_STREAM, DEFAULT_EXIT_VALUE);
   }

   /**
     * Returns a {@code PrintWriter} attached to the {@code file}, or quits if the specified file cannot be opened for
     * writing.  (See {@link #openWriter(java.io.File, String, boolean)}.
     *
     * @param filename  the {@code File} to which to write
     * @param autoflush if {@code true}, the {@code println}, {@code printf}, or {@code format} methods will flush the
     *                  output buffer.
     * @param error     the stream to which to write any error messages before quitting.
     * @param exitValue the value passed to {@code System.exit}
     * @return the new {@code PrintWriter}
     */
    public static PrintWriter openWriterOrQuit(final String filename, final boolean autoflush,
                                               PrintStream error,
                                               int exitValue) {
       return openOrQuit(new Opener() {
          public PrintWriter open() throws FileNotFoundException {
             return OutputHelper.openWriter(filename, autoflush);
          }
       }, filename, error, exitValue);
    }

    /**
     * Returns a {@code PrintWriter} attached to the {@code file}, or quits if the specified file cannot be opened for
     * writing.  (See {@link #openWriter(java.io.File, String, boolean)}.
     *
     * @param filename  the {@code File} to which to write
     * @param autoflush if {@code true}, the {@code println}, {@code printf}, or {@code format} methods will flush the
     *                  output buffer.
     * @return the new {@code PrintWriter}
     */
    public static PrintWriter openWriterOrQuit(final String filename, final boolean autoflush) {
       return openWriterOrQuit(filename, autoflush, DEFAULT_ERROR_STREAM, DEFAULT_EXIT_VALUE);
    }




}
