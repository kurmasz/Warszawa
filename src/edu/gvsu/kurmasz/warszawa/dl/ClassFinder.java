/**
 * Copyright (c) Zachary Kurmas 2009
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
package edu.gvsu.kurmasz.warszawa.dl;

import java.lang.reflect.Modifier;

/**
 * Find and generate the {@code Class} object for a class given its name. (Such
 * a {@code Class} object is typically used to instantiate an object of a class
 * that isn't known at compile time.)
 *
 * @author Zachary Kurmas
 */
// (C) 2009 Zachary Kurmas
// Created Dec 31, 2009
public class ClassFinder {
   // The Strings below have package scope so that they can be accessed from
   // the beta class.
   static final String CLASS_NOT_FOUND1 = "Class \"%s\" was not found. "
         + "Be sure to specify the class's complete name, including the package.\n"
         + "Also check (1) that the class is spelled correctly, and (2) that the "
         + "class's .class file is in\nthe current JVM's classpath.";

   static final String CLASS_NOT_FOUND_BAD_CAPS = "Class \"%s\" was not found. "
         + "Be sure the class's name is capitalized correctly.";

   static final String CLASS_NOT_ASSIGNABLE_TO = "Class \"%s\" is not assignable to type \"%s\"."
         + "\n(This means that %1$s does not extend or implement %2$s.)";

   /**
    * Private constructor so users can't create ClassFinder object.
    */
   private ClassFinder() {
      return;
   }

   /**
    * Get the {@code Class} object for the named class and verify that {@code
    * className} is a subclass of {@code parentClass}. Internally this method
    * calls {@code Class.forName(String)}. (See
    * {@link SimpleFactory#make(String, Class)} for a discussion of the purpose
    * of {@code T} and {@code parentClass}.)
    *
    * @param <T>         The type of {@code parentClass}
    * @param className   the fully qualified name of the desired class
    * @param parentClass a superclass of {@code className}
    * @return the {@code Class} object for the class {@code className}
    * @throws DLException              if the named class isn't found, or is not a subclass of
    *                                  {@code parentClass}
    * @throws IllegalArgumentException if {@code className} or {@code parentClass} are {@code null}
    */
   public static <T> Class<T> getClass(String className, Class<T> parentClass)
         throws DLException {
      //
      // Make sure neither parameter is null
      //
      if (className == null) {
         throw new IllegalArgumentException("className cannot be null");
      }

      if (parentClass == null) {
         throw new IllegalArgumentException("parentClass cannot be null");
      }

      //
      // Use Class.forName to get the desired Class object, or throws an
      // exception of the named class isn't found.
      //
      Class<?> answer;
      try {
         answer = Class.forName(className);
      } catch (ClassNotFoundException ex) {
         throw new DLException(
               String.format(CLASS_NOT_FOUND1, className), ex);
      } catch (NoClassDefFoundError ex) {
         // This Error gets thrown if the user enters a mis-capitalized name
         // (e.g., DUMMY instead of Dummy).
         throw new DLException(String.format(CLASS_NOT_FOUND_BAD_CAPS,
               className), ex);
      }

      //
      // Now make sure answer represents a class of the desired
      // type.
      //
      if (!parentClass.isAssignableFrom(answer)) {
         // TODO: If we are searching through several packages, it my happen
         // that the wrong class gets found first. If this can happen, add a
         // message to indicate it.
         throw new DLException(String.format(CLASS_NOT_ASSIGNABLE_TO,
               answer.getName(), parentClass.getName()), null);
      } else {
         // The previous if statement verifies that this uncheckable cast is
         // safe.
         @SuppressWarnings("unchecked")
         Class<T> cast_answer = (Class<T>) answer;
         return cast_answer;
      }
   } // end getClass

   /**
    * Returns {@code true} if {@code cls} has a {@code public} constructor that
    * takes no parameters.
    *
    * @param cls the class to beta
    * @return {@code true} if {@code cls} has a {@code public} constructor that
    *         takes no parameters, {@code false} otherwise.
    */
   // This method is not public because we haven't tested it thoroughly.
   static boolean hasPublicNullaryConstructor(Class<?> cls) {
      // Attempt to access the object describing the constructor that takes no
      // parameters. If this constructor exists, beta to see if it is public.
      // If it doesn't exist, getConstructor will throw an exception. In the
      // case of an exception, return false.
      try {
         return Modifier.isPublic(cls.getConstructor().getModifiers());
      } catch (NoSuchMethodException e) {
         return false;
      }
   }

   /*
     * private static boolean hasPublicConstructor(Class<?> cls) { for
     * (Constructor<?> constructor : cls.getConstructors()) { if
     * (Modifier.isPublic(constructor.getModifiers())) { return true; } } return
     * false; }
     */

   // TODO: Write and test a method canInstantiate that will check that the
   // Class meets all the necessary conditions to be instantiated (e.g.,
   // public, not an interface, not abstract, public nullary constructor, etc.)
   // Such a method may be useful to present a menu of acceptable classes to
   // the user.
} // end ClassFinder
