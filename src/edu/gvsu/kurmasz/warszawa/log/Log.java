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

import edu.gvsu.kurmasz.warszawa.Warszawa;
import edu.gvsu.kurmasz.warszawa.io.OutputHelper;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * A {@link SimpleLog} with extended configurability.
 *
 * @author Zachary Kurmas
 */
// Created  10/13/11 at 5:42 PM
// (C) Zachary Kurmas 2011

public class Log extends SimpleLog {


   //private boolean closeAutomatically = true;


   /**
    * Constructs a silent log.
    */
   public Log() {
      super();
   }

   /**
    * Constructor
    *
    * @param log       where to write the log data
    * @param threshold Minimum level of importance to be logged. (In other words,
    *                  only messages with values at least {@code debugLevel} are
    *                  logged.)
    */
   public Log(PrintWriter log, int threshold) {
      super(log, threshold);
   }

   /**
    * Constructor
    *
    * @param logfile   where to write the log data
    * @param threshold Minimum level of importance to be logged. (In other words,
    *                  only messages with values at least {@code debugLevel} are
    *                  logged.)
    * @throws FileNotFoundException if {@code logfile} can't be written to
    */
   public Log(String logfile, int threshold) throws FileNotFoundException {
      super(null, threshold);
      setOutput(logfile);
   }

   /**
    * Constructor
    *
    * @param stream    {@code OutputStream} to which to write the log data
    * @param threshold Minimum level of importance to be logged. (In other words,
    *                  only messages with values at least {@code debugLevel} are
    *                  logged.)
    * @throws FileNotFoundException if {@code logfile} can't be written to
    */
   public Log(OutputStream stream, int threshold) {
      super(null, threshold);
      setOutput(stream);
   }


   /**
    * Specifies the stream to which to write the logging messages.
    *
    * @param stream the stream
    */
   public void setOutput(OutputStream stream) {
      setOutput(OutputHelper.openWriter(stream, true));
   }

   /**
    * Specifies the file to which to write the logging messages
    *
    * @param filename the file to which to write logging messages.
    *                 An empty or null string will deactivate logging.
    * @throws FileNotFoundException if {@code logfile} can't be written to
    */
   public void setOutput(String filename) throws FileNotFoundException {
      if (filename == null || filename.length() == 0) {
         setOutput((PrintWriter) null);
      } else {
         setOutput(OutputHelper.openWriter(filename, true));
      }
   }

   /**
    * Specifies the writer to which to write the logging messages or quit if
    * the file is not writable.
    *
    * @param filename   the file to which to write logging messages
    * @param error      the stream to which to write error messages
    * @param exit_value exit value for process should log creation fail
    */
   public void setOutputOrQuit(String filename, final java.io.PrintStream error,
                               final int exit_value) {
      if (filename == null || filename.length() == 0) {
         setOutput((PrintWriter) null);
      } else {
         setOutput(OutputHelper.openWriterOrQuit(filename, true, error, exit_value));
      }
   }

   /**
    * Specifies the file to which to write the logging messages or quit if the
    * file is not writable. (Process exits with a return value of 1.)
    *
    * @param file the file to which to write logging messages
    */
   public void setOutputOrQuit(String file) {
      setOutputOrQuit(file, Warszawa.DEFAULT_ERROR_STREAM, Warszawa.DEFAULT_EXIT_VALUE);
   }

   /**
    * Configure the logger
    *
    * @param stream    the {@code OutputStream} to which to write logging messages.
    * @param threshold Minimum level of importance to be logged. (In other words,
    *                  only messages with values at least {@code threshold} are
    *                  logged.)
    */
   public void configure(OutputStream stream, int threshold) {
      setOutput(stream);
      setThreshold(threshold);
   }


   /**
    * Configure the logger
    *
    * @param logfile   the file to which to write logging messages.
    *                  An empty or null string will deactivate logging.
    * @param threshold Minimum level of importance to be logged. (In other words,
    *                  only messages with values at least {@code threshold} are
    *                  logged.)
    * @throws FileNotFoundException if {@code logfile} cannot be opened for writing.
    */

   public void configure(String logfile, int threshold) throws FileNotFoundException {
      setOutput(logfile);
      setThreshold(threshold);
   }


   /**
    * Configure the logger or quit if the file cannot be opened for writing.
    *
    * @param file      the file to which to write logging messages.
    *                  An empty or null string will deactivate logging.
    * @param threshold Minimum level of importance to be logged. (In other words,
    *                  only messages with values at least {@code threshold} are
    *                  logged.)
    * @param error     the stream to which to write error messages
    * @param exitValue exit value for process should log creation fail
    */
   public void configureOrQuit(String file, int threshold, PrintStream error, int exitValue) {
      setOutputOrQuit(file, error, exitValue);
      setThreshold(threshold);
   }

   /**
    * Configure the logger or quit if the file cannot be opened for writing.
    *
    * @param file      the file to which to write logging messages.
    *                  An empty or null string will deactivate logging.
    * @param threshold Minimum level of importance to be logged. (In other words,
    *                  only messages with values at least {@code threshold} are
    *                  logged.)
    */
   public void configureOrQuit(String file, int threshold) {
      configureOrQuit(file, threshold, Warszawa.DEFAULT_ERROR_STREAM, Warszawa.DEFAULT_EXIT_VALUE);
   }

   /**
    * Try to create a {@code Log} attached to the specified file. If the
    * file is not writable, print an error message and quit.
    *
    * @param file      the file to which to write logging messages
    * @param threshold Minimum level of importance to be logged. (In other words,
    *                  only messages with values at least {@code theshold} are
    *                  logged.)
    * @param error     the stream to which to write error messages
    * @param exitValue exit value for process should log creation fail
    * @return a valid {@code Log} object
    */
   public static Log makeLogOrQuit(String file, int threshold, java.io.PrintStream error,
                                   int exitValue) {

      Log log = new Log();
      log.configureOrQuit(file, threshold, error, exitValue);
      return log;
   }


   /**
    * Try to create a {@code Log} attached to the specified file. If the
    * file is not writable, print an error message and quit.
    *
    * @param file      the file to which to write logging messages
    * @param threshold Minimum level of importance to be logged. (In other words,
    *                  only messages with values at least {@code debugLevel} are
    *                  logged.)
    * @return a valid {@code Log} object
    */
   public static Log makeLogOrQuit(String file, int threshold) {
      return makeLogOrQuit(file, threshold, Warszawa.DEFAULT_ERROR_STREAM, Warszawa.DEFAULT_EXIT_VALUE);
   }

   /**
    * Close the underlying {@code PrintWriter}.
    */
   public void close() {
      if (getWriter() != null) {
        getWriter().close();
      }
   }

//   /**
//    * Specify whether the underlying {@code PrintWriter} should close
//    * when this object is destroyed.
//    *
//    * @param doClose whether to close automatically
//    */
//   public void closeAutomatically(boolean doClose) {
//      closeAutomatically = doClose;
//   }
//

//
//   /*
//   * Close the log automatically (unless specified otherwise.)
//   */
//   protected void finalize() throws Throwable {
//      if (!closeAutomatically && getWriter() != null) {
//         getWriter().close();
//      }
//      super.finalize();
//   }


   public static void main(String[] args) {
      System.out.println("Before");
      //Log sl = new Log(OutputHelper.openWriter(System.err, Charset.defaultCharset(), true), 5);
      Log sl = new Log();
      sl.configure(System.out, 12);
      sl.println(500, "WTF");
      System.out.println("After");
      sl.close();
      sl.println(500, "Oops");
      System.out.println("Write after close");
   }

}
