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
 * Methods that return information about when a particular package was last
 * built. They rely on the build date being stored in a properties file with a known
 * name. (One way to do this is to have ant update the file.)
 *
 * @author Zachary Kurmas
 *
 */

public class BuildDate {

   private BuildDate() {}

   public static final String DEFAULT_RESOURCE_NAME = "builddate.properties";


   /**
    * Loads the specified resource for the class' package and returns a
    * {@code Date} object representing the date stored in the resource.  Obtained from
    * http://forum.java.sun.com/thread.jspa?threadID=584408&messageID=3012258
    *
    * @param resource the resource containing the build date
    * @param c        A class in the package containing the resource
    * @return a {@code Date} object containing the information stored in the
    *         resource, or {@code null} if the resource can't be found.
    *
    */
   public static Date getBuildDate(String resource, Class<? extends Object> c) {
      Date retValue = null;
      try {
         Properties p = new Properties();
         InputStream is = c.getResourceAsStream(resource);
         if (is != null) {
            p.load(is);
            String buildDateString = p.getProperty("builddate");
            try {
               retValue = buildDateString == null ? null
                     : new SimpleDateFormat("yyyy.MM.dd HH.mm.ss")
                     .parse(buildDateString);
            } catch (ParseException e) {
               return null;
            }
         } else {
            return null;
         }
      } catch (IOException e) {
         return null;
      }
      return retValue;
   }

   /**
    * Loads the specified resource for the object's package and returns a
    * {@code Date} object representing the date stored in the resource.
    *
    * @param resource the resource containing the build date
    * @param obj      An object in the package containing the resource
    * @return a {@code Date} object containing the information stored in the
    *         resource, or {@code null} if the resource can't be found.
    *
    */
   public static Date getBuildDate(String resource, Object obj) {
      return getBuildDate(resource, obj.getClass());
   }

   /**
    * Loads the default resource for the object's package and returns a
    * {@code Date} object representing the date stored in the resource.
    *
    * @param obj An object in the package containing the resource
    * @return a {@code Date} object containing the information stored in the
    *         resource, or {@code null} if the resource can't be found.
    *
    */
   public static Date getBuildDate(Object obj) {
      return getBuildDate(DEFAULT_RESOURCE_NAME, obj.getClass());
   }

   /**
    * Loads the default resource for the class' package and returns a
    * {@code Date} object representing the date stored in the resource.
    *
    * @param c A class in the package containing the resource
    * @return a {@code Date} object containing the information stored in the
    *         resource, or {@code null} if the resource can't be found.
    *
    */
   public static Date getBuildDate(Class<? extends Object> c) {
      return getBuildDate(DEFAULT_RESOURCE_NAME, c);
   }

   /**
    * Prints the build date of this package.
    *
    * @param args not used
    */
   public static void main(String[] args) {
      System.out.println("Warszawa build date: "
            + getBuildDate(BuildDate.class) +".");
   }

}