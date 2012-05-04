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

import edu.gvsu.kurmasz.warszawa.dl.SimpleFactory;
import edu.gvsu.kurmasz.warszawa.io.InputHelper;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Verifies that methods that are supposed to quit, do quit.
 *
 * @author Zachary Kurmas
 */
// Created  11/2/11 at 1:41 PM
// (C) Zachary Kurmas 2011

public class VerifyQuit {

   @Retention(RetentionPolicy.RUNTIME)
   @interface Quitter {
   }


   @Quitter
   public void testOpenFilteredInputStreamOrQuit_Defaults(int exit_code) throws FileNotFoundException {
      InputHelper.openFilteredInputStreamOrQuit("noSuchFileAnywhereAtAll", null, System.err, exit_code);
   }

   @Quitter
   public void testOpenFilteredInputStreamOrQuit(int exit_code) throws FileNotFoundException {
      PrintStream alt = new PrintStream(new FileOutputStream(SystemTest.output("quit_message1")));
      InputHelper.openFilteredInputStreamOrQuit("anotherFileThatDoesntExist", null, alt, exit_code);
   }

   @Quitter
   public void testMakeOrQuit1(int exit_code) throws FileNotFoundException {
      PrintStream alt = new PrintStream(new FileOutputStream(SystemTest.output("quit_message2")));
      SimpleFactory.makeOrQuit("no.such.class.InThisPackage", Object.class, true, alt, exit_code );
   }

   @Quitter
   public void testMakeOrQuit2(int exit_code) throws FileNotFoundException {
      PrintStream alt = new PrintStream(new FileOutputStream(SystemTest.output("quit_message3")));
      SimpleFactory.makeOrQuit("java.lang.String", Integer.class, alt, exit_code );
   }


   private static List<Method> quitterList(Class c) {
      List<Method> methods = new ArrayList<Method>();
      for (Method m : c.getMethods()) {
         if (m.getAnnotation(Quitter.class) != null) {
            methods.add(m);
         }
      }
      return methods;
   }

   public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
      List<Method> methods = quitterList(VerifyQuit.class);
      if (args.length == 0) {
         System.out.println(methods.size());
      } else {
         int exit_code = 5;
         if (args.length > 1) {
            exit_code = Integer.parseInt(args[1]);
         }
         VerifyQuit vq = new VerifyQuit();
         int toRun = Integer.parseInt(args[0]);
         methods.get(toRun).invoke(vq, exit_code);
         System.out.println("Method " + methods.get(toRun) + "failed to quit");
      }
   }
}
