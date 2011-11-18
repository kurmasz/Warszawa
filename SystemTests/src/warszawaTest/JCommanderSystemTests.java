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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import edu.gvsu.kurmasz.warszawa.beta.op.JCommanderWrapper;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import static warszawaTest.SystemTest.output;

/**
 * @author Zachary Kurmas
 */
// Created  11/17/11 at 10:31 AM
// (C) Zachary Kurmas 2011

public class JCommanderSystemTests {

   private static class Options {
      @Parameter(names = "--myInt")
      private int myInt = 37;

      @Parameter(names = {"--mString", "--someString"})
      private String theString = "NotSpecified";

      public String toString() {
         return String.format("myInt: %d theString: %s", myInt, theString);
      }
   }

   private static void test1(PrintWriter output) {
      Options opt = new Options();
      String[] parameters = {"--myInt", "91", "--mString", "The String"};
      JCommanderWrapper.parse(opt, parameters);
      output.println(opt);
   }


   private static void test2(PrintWriter output) {
      Options opt = new Options();
      String[] parameters2 = {"--myInt", "71", "--someString", "Another String"};
      JCommanderWrapper.parse(opt, parameters2);
      output.println(opt);
   }

   private static void test3(PrintWriter output) {
      Options opt = new Options();
      String[] parameters3 = {"--someString", "Another String"};
      JCommanderWrapper.parse(opt, parameters3);
      output.println(opt);
   }

   private static void test4(PrintWriter output) {
      Options opt = new Options();
      String[] parameters4 = {"--my", "76", "--s", "String number 3"};
      JCommanderWrapper.parse(opt, parameters4);
      output.println(opt);
   }

   private static void test5(PrintWriter output) {
      Options opt = new Options();
      try {
         String[] parameters5 = {"--m", "76"};
         JCommanderWrapper.parse(opt, parameters5);
         output.println(opt);
      } catch (ParameterException pe) {
         output.println(pe);
      }
   }

   public static void allTests() throws FileNotFoundException {
      PrintWriter output = new PrintWriter(output("jCommander.text"));
      test1(output);
      test2(output);
      test3(output);
      test4(output);
      test5(output);
      output.close();
   }
}
