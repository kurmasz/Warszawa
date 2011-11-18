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

/**
 * @author Zachary Kurmas
 */
// Created  11/17/11 at 10:30 AM
// (C) Zachary Kurmas 2011

public class SystemTest {
   public static final String OUTPUT_DIR = "actual_output";
   public static final String INPUT_DIR = "input";

   public static String output(String name) {
      return OUTPUT_DIR + '/' + name;
   }

   public static String input(String name) {
      return INPUT_DIR + '/' + name;
   }

   public static void main(String[] args) throws Exception {
      System.out.println("First line to stdout");
      System.err.println("First line to stderr");
      IOHelperSystemTest.allTests();
      JCommanderSystemTests.allTests();
      DLTest.allTests();
      TestJoswa.allTests();
      System.out.println("Last line to stdout");
      System.err.println("Last line to stderr");
   }
}
