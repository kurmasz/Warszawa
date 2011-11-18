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
package warszawaTest;

import edu.gvsu.kurmasz.warszawa.deprecated.joswa.JoswaOption;
import edu.gvsu.kurmasz.warszawa.deprecated.joswa.JoswaOptionParser;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Just a quick program to make sure that the .jar file contains the necessary components.
 */
public class TestJoswa {
   public static class MyOptions {
      @JoswaOption
      public boolean verbose = false;

      @JoswaOption
      public Integer debugLevel = 6;
   }


   public static void allTests() throws FileNotFoundException {
      PrintWriter output = new PrintWriter(SystemTest.output("joswa.txt"));

      MyOptions mo = new MyOptions();
      JoswaOptionParser parser = new JoswaOptionParser(mo);
      parser.parse(new String[]{"--verbose", "--debug", "3"});

      output.println("Verbose is " + mo.verbose);
      output.println("Debug level is " + mo.debugLevel);
      output.close();
   }
}
