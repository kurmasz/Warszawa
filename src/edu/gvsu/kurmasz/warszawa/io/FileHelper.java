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

import java.io.File;

/**
 * @author Zachary Kurmas
 */
// Created  9/21/11 at 1:40 PM
// (C) Zachary Kurmas 2011

public class FileHelper {
   /**
    * Return the filename suffix.  The suffix is defined to be the part of the file name after the final period.
    * The leading dots do not count.  Thus, ".bashrc" has no suffix. (It does <em>not</em> have a suffix of
    * "bashrc".)  Similarly,
    * the files ".." and "..data" also have no suffix.
    * The parameter {@code filename} is assumed to be a base file name, not a full path.  Thus,
    * the method will throw an {@code IllegalArgumentException} if {@code filename} contains any directory separator
    * characters (i.e., "/" on Unix and "\\" on Windows).
    * (In the future I may modify this method to handle full path names,
    * I just have not yet had the need to do it.  If you want this feature, just ask.)
    *
    * @param filename the files <em>base</em> name (i.e., directory separators are not currently allowed.)
    * @return the filename's suffix (the part after the final period.
    * @throws IllegalArgumentException if {@code filename} contains any slashes.
    */
   public static String getSuffix(String filename) throws IllegalArgumentException {
      if (filename == null || filename.length() == 0) {
         return null;
      }
      final char DOT = '.';
      if (filename.contains(File.separator)) {
         throw new IllegalArgumentException("filename \"" + filename + "\" should be a base file name, " +
               "not a full path (i.e., it should not contain any directory separators");
      }

      int firstNonDot = 0;
      while (filename.charAt(firstNonDot) == DOT) {
         firstNonDot++;
         if (firstNonDot >= filename.length()) {
            return null;
         }
      }


      int lastDotLoc = filename.lastIndexOf(".");
      if (lastDotLoc == -1 || lastDotLoc < firstNonDot) {
         return null;
      }
      return filename.substring(lastDotLoc + 1);

   }

}
