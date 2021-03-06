/**
 * Copyright (c) Zachary Kurmas 2010
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

import java.io.PrintWriter;

/**
 * A very simple logging utility.
 *
 * @author Zachary Kurmas
 */
// (C) 2010 Zachary Kurmas
// Created February 27, 2010
public class SimpleLog {
   // set to print no error messages
   private int debugLevel = Integer.MAX_VALUE;
   private PrintWriter log = null;

   /**
    * Default log that produces no output.
    */
   public SimpleLog() {
   }

   /**
    * Constructor
    *
    * @param log       where to write the log data
    * @param threshold Minimum level of importance to be logged. (In other words,
    *                  only messages with values at least {@code debugLevel} are
    *                  logged.)
    */
   public SimpleLog(PrintWriter log, int threshold) {
      this.log = log;
      this.debugLevel = threshold;
   }

   /**
    * Test whether logging is enabled for the specified level. (Subclasses can
    * use this method to avoid performing calculations that won't be printed.)
    *
    * @param level the importance of the message
    * @return {@code true} if messages of {@code level} will be printed.
    *         {@code false} otherwise.
    */
   public boolean willLog(int level) {
      return (log != null && debugLevel <= level);
   }

   /**
    * Print a logging message
    *
    * @param level   the importance of the message. (Higher values are more
    *                important.)
    * @param message the message to print.
    */
   public void println(int level, String message) {
      if (willLog(level)) {
         log.println(message);
      }
   }

   /**
    * Set the message threshold. Lower values produce more messages.
    *
    * @param threshold Minimum level of importance to be logged. (In other words,
    *                  only messages with values at least {@code debugLevel} are
    *                  logged.)
    */
   public void setThreshold(int threshold) {
      debugLevel = threshold;
   }

   /**
    * Effectively suspends logging by setting the threshold to the maximum possible value
    * (unless, of course, a message is printed with level {@code Integer.MAX_VALUE}).
    */
   public void setThresholdToMax() {
      setThreshold(Integer.MAX_VALUE);
   }

   /**
    * Set the stream to which to write logging messages.
    *
    * @param log the writer to which to write logging messages.
    */
   public void setOutput(PrintWriter log) {
      this.log = log;
   }

   /**
    * Configure the logger
    *
    * @param output    the file to which to write logging messages.
    *                  An empty or null string will deactivate logging.
    * @param threshold Minimum level of importance to be logged. (In other words,
    *                  only messages with values at least {@code debugLevel} are
    *                  logged.)
    */
   public void configure(PrintWriter output, int threshold) {
      setOutput(output);
      setThreshold(threshold);
   }

   /**
    * Returns the {@code PrintWriter} to which messages are logged.
    * @return the {@code PrintWriter} to which messages are logged.
    */
   protected PrintWriter getWriter() {
      return log;
   }

   /**
    * Returns the debug threshold.
    * @return the debug threshold.
    */
   protected int getThreshold() {
      return debugLevel;
   }

} // end SimpleLog
