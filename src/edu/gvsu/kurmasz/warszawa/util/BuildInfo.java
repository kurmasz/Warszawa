/**
 * Copyright (c) Zachary Kurmas 2007
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
package edu.gvsu.kurmasz.warszawa.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Build information about a package, including version number and build date.
 * This class relies on the build info being stored in a properties file with a known
 * name. (One way to do this is to have ant update the file.)
 *
 * @author Zachary Kurmas
 */
// Created  5/3/12 at 9:06 PM
// (C) Zachary Kurmas 2012

public class BuildInfo {

   public static final String DEFAULT_RESOURCE_NAME = "buildInfo.properties";

   /**
    * Exception thrown when either the properties file won't parse, or
    * the date in the properties file won't parse
    */
   public static class InvalidBuildInfoFile extends RuntimeException {
      public InvalidBuildInfoFile(String s) {
         super(s);
      }
   }

   private Date buildDate;
   private String version;

   private BuildInfo(Date buildDate, String version) {
      this.buildDate = buildDate;
      this.version = version;
   }

   public Date getBuildDate() {
      return buildDate;
   }

   public String getVersion() {
      return version;
   }

   /**
    * Loads the specified resource for the class' package and returns a
    * {@code BuildInfo} object representing data stored in the resource.  Obtained from
    * <a href="http://forum.java.sun.com/thread.jspa?threadID=584408&amp;messageID=3012258">Sun's Java Forum</a>
    *
    * @param resource the resource containing the build date
    * @param c        A class in the package containing the resource
    * @return a {@code BuildInfo} object containing the information stored in the
    *         resource, or {@code null} if the resource can't be found.
    */
   public static BuildInfo make(String resource, Class<? extends Object> c) {
      BuildInfo retValue = null;
      try {
         Properties p = new Properties();
         InputStream is = c.getResourceAsStream(resource);
         if (is != null) {
            p.load(is);
            String buildDateString = p.getProperty("builddate");
            Date buildDate;
            try {
               buildDate = buildDateString == null ? null
                     : new SimpleDateFormat("yyyy.MM.dd HH.mm.ss")
                     .parse(buildDateString);
            } catch (ParseException e) {
               throw new InvalidBuildInfoFile("Date in properties file won't parse: \"" + buildDateString + "\"");
            }
            retValue = new BuildInfo(buildDate, p.getProperty("version"));
         } else {
            throw new InvalidBuildInfoFile("Build info resource file \"" + resource + "\" does not exist for class " +
                  c.getName() + ".");
         }
      } catch (IOException e) {
         return null;
      }
      return retValue;
   }

   /**
    * Loads the default resource for the class' package and returns a
    * {@code BuildInfo} object representing data stored in the resource.  Obtained from
    * <a href="http://forum.java.sun.com/thread.jspa?threadID=584408&amp;messageID=3012258">Sun's Java Forum</a>
        *
    * @param c A class in the package containing the resource
    * @return a {@code BuildInfo} object containing the information stored in the
    *         resource, or {@code null} if the resource can't be found.
    */
   public static BuildInfo make(Class<? extends Object> c) {
      return make(DEFAULT_RESOURCE_NAME, c);
   }
}
