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

import edu.gvsu.kurmasz.warszawa.Warszawa;

import javax.xml.bind.annotation.XmlElementRef;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * Contains methods for instantiating objects of a class specified at runtime.
 *
 * @author Zachary Kurmas
 */
// (C) 2009 Zachary Kurmas
// Created December 31, 2009
public class SimpleFactory {

   private static final boolean DEFAULT_RUNTIMEPASSTHROUGH = false;

   // These are not private so that SimpleFactoryTest can access them.
   static final String BASE = "Cannot instantiate an object of \"%s\" because ";
   static final String ILLEGAL_ACCESS_NONPUBLIC_CLASS = BASE
         + "it is not public.";

   static final String INSTANTIATION_EXCEPTION_ABSTRACT_CLASS = BASE
         + "it is abstract.";
   static final String INSTANTIATION_EXCEPTION_INTERFACE = BASE
         + "it is an interface.";


   private static final String UNKNOWN_REASON = BASE
         + "newInstance() threw a known exception for an unknown reason."
         + "(If this happens, please e-mail the developers so we can fix our code.)";

   static final String EXCEPTION_FROM_CONSTRUCTOR = BASE
         + "%1$s's constructor threw an exception.";

   static final String MULTIPLE_CONSTRUCTORS_MATCH = BASE
         + "%1$s has multiple constructors that match the given parameters.";

   static final String NO_SUCH_PUBLIC_CONSTRUCTOR = BASE +
         "%1$s does not have any public constructors that match the parameters given.";

   static final String INSTANTIATION_EXCEPTION_NON_STATIC_INNER =
         NO_SUCH_PUBLIC_CONSTRUCTOR
               + " This class appears to be a non-static inner class.  "
               + "Remember that every constructor for a Non-static inner class implicitly takes "
               + "the enclosing object as a parameter.";

   static final String NULL_PARAMETERS = BASE +
         "one of the parameters to the factory was null.  The factory methods currently cannot handle null " +
         "parameters.  (We hope to remove this restriction in a future version.)";

   static final String NULL_PARAMETER_LIST = BASE +
         "the parameter list was null.  (Passing null as the final parameter to make will not pass" +
         " a single null; it will pass an array with the value null.  To pass a single null, " +
         "try \"new Object[]{null}\".)";

   /*
   No longer needed

   static final String ILLEGAL_ACCESS_PRIVATE_CONSTRUCTOR = BASE
         + "the constructor that takes no parameters is not public.";
   static final String INSTANTIATION_EXCEPTION_NO_NULLARY = BASE
         + "it is has no public constructors that take no parameters.";
   */

   /**
    * Instantiate an object of type {@code name} and cast the new object to be
    * of type {@code T}. This method's main purpose is to instantiate objects
    * that are not known at compile time (e.g., plug-ins). The new object will
    * be cast to some super-type, {@code T}. (It can't be cast to its own type,
    * because its own type isn't known at compile time.) Because of Java's <a
    * target="_top" href=
    * "http://java.sun.com/docs/books/tutorial/java/generics/erasure.html">type
    * erasure</a>, we need the <code>Class</code> object of <code>T</code> as a
    * parameter to verify that the requested object is actually of type {@code
    * T}. (This check is not strictly necessary because if we don't have this {@code Class} object,
    * and there is a type
    * mismatch, Java will eventually generate a <code>ClassCastException</code>
    * ; however, we prefer to find any such problems
    * as early as possible.)
    *
    * <p>
    * ExampleUsage:
    *
    * <pre>
    * PluginInterface plugin = SimpleFactory.make(&quot;MyPlugIn&quot;, PluginInterface.class);
    * </pre>
    *
    * <p>
    * The above code assumes that <code>PluginInterface</code> is a Java
    * interface that is known at compile time, and that <code>MyPlugIn</code>
    * is a class that implements the <code>PluginInterface</code> interface.
    * </p>
    *
    * <p>Two important limitations:</p>
    * <ul>
    * <li> This
    * code does not search for the most specific match among several overload constructors.  It will
    * simply throw an exception if more than one constructor could be called given the types of the
    * actual parameters, but no one constructor matches exactly.</li>
    *
    * <li>You cannot directly pass primitive data to the constructor.  Any primitive data will be autoboxed.  You
    * cannot pass <code>null</code> as a parameter to the constructor.
    * </li>
    *
    * </ul>
    *
    * <p>Note:  It is assumed that the class being dynamically loaded is not in the package {@code edu.gvsu.kurmasz
    * .warszawa.dl}.  Therefore, it will automatically throw an exception if the user attempts to instantiate class
    * with {@code private} or package-protected protection, even though that would normally be allowed for classes in
    * the same package as {@code SimpleFactory}.  Other "overly aggressive" exceptions may be thrown for similar
    * reasons when attempting to load classes in
    * {@code edu.gvsu.kurmasz.warszawa.dl}.
    * </p>
    *
    * <p>Normally, when a dynamically invoked constructor throws an exception,
    * a <code>InvocationTargetException</code>< is generated with the the exception generated by the constructor as
    * its cause.  When <code>rethrowRuntimeExceptions</code> is set to {@code true},
    * any {@code RuntimeException}s generated by the constructor are simply re-thrown.  This allows the calling
    * method to simply check for exceptions as if the object were being created directly (i.e.,
    * using {@code new}).  This feature is limited to {@code RuntimeExceptions} only because rethrowing any {@code
    * Exception} in general would require this method to include <code>throws Exception</code> in its signature,
    * and we don't want the user to have to explicitly check for a general <code>Exception</code>.</p>
    *
    * @param <T>                      A super-type of the object to be created
    * @param name                     The fully qualified name of the class to be instantiated
    * @param parentClass              The {@code Class} object for {@code T} (needed to verify that
    *                                 the cast from {@code Object} to {@code T} is safe).
    * @param rethrowRuntimeExceptions when {@code true}, then this method re-throws any {@code RuntimeException}s
    *                                 that are the cause of any {@code InvocationTargetException}.  When {@code false},
    *                                 all {@code InvocationTargetExceptions} generate a {@link DLException} (the same
    *                                 as any other exception). (See note above).
    * @param params                   a list of parameters to the constructor
    * @return an instance of the desired object
    * @throws DLException              if anything goes wrong.
    * @throws IllegalArgumentException if {@code name} or {@code parentClass} are {@code null}
    */
   public static <T> T make(String name, Class<T> parentClass, boolean rethrowRuntimeExceptions, Object... params)
         throws DLException {
      //
      // Make sure neither parameter is null
      //
      if (name == null) {
         throw new IllegalArgumentException("className cannot be null");
      }

      if (parentClass == null) {
         throw new IllegalArgumentException("parentClass cannot be null");
      }

      if (params == null) {
         throw new IllegalArgumentException(String.format(NULL_PARAMETER_LIST, name));
      }

      //
      // Get the Class object that describes the class to be instantiated.
      // If the class isn't found, getClass throws a DLException that is
      // automatically passed up the calling method.
      //
      Class<T> classObject = ClassFinder.getClass(name, parentClass);

      //
      // Now attempt to instantiate the object.
      //
      String fullClassName = classObject.getName();
      String errorMessage = BASE;

      int modifiers = classObject.getModifiers();
      if (Modifier.isInterface(modifiers)) {
         throw new DLException(String.format(
               INSTANTIATION_EXCEPTION_INTERFACE, fullClassName), null);
      } else if (Modifier.isAbstract(modifiers)) {
         throw new DLException(String.format(
               INSTANTIATION_EXCEPTION_ABSTRACT_CLASS, fullClassName),
               null);
      } else if (!Modifier.isPublic(modifiers) && !Modifier.isProtected(modifiers)) {
         throw new DLException(String.format(
               ILLEGAL_ACCESS_NONPUBLIC_CLASS, fullClassName), null);
      }

      T typedObject = null;
      try {
         typedObject = instantiate(classObject, params);
      } catch (ClassCastException e) {
         throw new DLException(errorMessage
               + "it is not a subtype of the template parameter.", e);
      } catch (IllegalAccessException e) {
         throw new DLException(String.format(UNKNOWN_REASON,
               fullClassName), e);
         /*
         if (!ClassFinder.hasPublicNullaryConstructor(classObject)) {
            throw new DLException(String.format(
                  ILLEGAL_ACCESS_PRIVATE_CONSTRUCTOR, fullClassName), e);
         } else {
            throw new DLException(String.format(UNKNOWN_REASON,
                  fullClassName), e);
         }
         */
      } catch (InstantiationException e2) {
         /*
         if (!ClassFinder.hasPublicNullaryConstructor(classObject)) {
            throw new DLException(String.format(
                  INSTANTIATION_EXCEPTION_NO_NULLARY, fullClassName),
                  e2);
         }
         */
         // InstantiationExceptions can also get thrown for trying to
         // instantiate primitive types, void, or array types; but, I don't
         // think they can be specified using a string (I think you'd have to
         // use the Type or TypeVariable interface or something like that),
         // so I don't think we
         // need to check for that here.

         /*
             * errorMessage += "\"" + fullClassName +
             * "\" is either (1) an interface," +
             * " (2),  an abstract class, (3) does not have a " +
             * "constructor that takes no parameters, or (4) is a " +
             * "non-static inner class.";
             */
         throw new DLException(String.format(UNKNOWN_REASON,
               fullClassName), e2);
      } catch (InvocationTargetException e3) {
         // If this happens, it should be because the constructor of the
         // class being instantiated threw an exception.

         // If rethrowRuntime is true *and* the cause of this exception is a runtime exception, then
         // re-throw that exception.  Otherwise, create a DLException

         if (rethrowRuntimeExceptions && (e3.getCause() instanceof RuntimeException)) {
            throw (RuntimeException) e3.getCause();
         }

         throw new DLException(String.format(EXCEPTION_FROM_CONSTRUCTOR,
               fullClassName), e3);
      }

      return typedObject;
   }

   /**
    * Calls {@link #make(String, Class, boolean, Object...)} with a default value of {@code false} for {@code
    * rethrowsRuntimeExceptions}
    */
   public static <T> T make(String name, Class<T> parentClass, Object... params)
         throws DLException {
      return make(name, parentClass, DEFAULT_RUNTIMEPASSTHROUGH, params);
   }

   /**
    * Find the constructor whose parameter types match the types of the actual parameters <b>exactly</b>,
    * then instantiate an object using that constructor.
    *
    * <p>{@code multipleMatches} specifies what should be done if the desired constructor is not found.  If
    * {@code multipleMatches} is {@code false}, that means that any {@code NoSuchMethodException}s should be passed up the
    * call stack back to the user.  In this case, the error indicates that none of this class's constructors will
    * work given the actual parameters passed.  If {@code multipleMatches} is {@code true} that means that multiple
    * constructors could be invoked given the actual parameters passed.  In this case,
    * if there is one constructor whose parameters match exactly, we'll use it.  Otherwise,
    * we want to pass a special exception message to the user letting her know that several constructors will work,
    * but that this method is unable to decide which one is the most specific fit.</p>
    *
    * @param classObject     the {@code Class} object for the class to be instantiated.
    * @param multipleMatches indicates how to handle a {@code NoSuchMethodException}.  See comment above.
    * @param params          the list of parameters to pass to the constructor
    * @return the newly instantiated object.
    * @throws InvocationTargetException if {@code newInstance} throws this exception.
    * @throws IllegalAccessException    if {@code newInstance} throws this exception.
    * @throws InstantiationException    if {@code newInstance} throws this exception.
    * @throws DLException               if no constructor matches the set of parameters given
    */
   private static <T> T instantiateFromExactMatch(Class<T> classObject, boolean multipleMatches,
                                                  Object... params) throws InvocationTargetException, IllegalAccessException, InstantiationException, DLException {
      Class<?>[] actualTypes = new Class<?>[params.length];
      for (int i = 0; i < params.length; i++) {
         actualTypes[i] = params[i].getClass();
      }

      try {
         Constructor<T> constructor = classObject.getConstructor(actualTypes);
         return constructor.newInstance(params);
      } catch (NoSuchMethodException e) {
         if (multipleMatches) {
            throw new DLException(String.format(MULTIPLE_CONSTRUCTORS_MATCH, classObject.getName()), null);
         } else if (!Modifier.isStatic(classObject.getModifiers())
               && classObject.getEnclosingClass() != null) {
            throw new DLException(String.format(
                  INSTANTIATION_EXCEPTION_NON_STATIC_INNER,
                  classObject.getName()), null);
         } else {
            throw new DLException(String.format(NO_SUCH_PUBLIC_CONSTRUCTOR, classObject.getName()), e);
         }
      }
   }


   /**
    * Instantiate an instance of the class specified by {@code classObject} using the constructor for which the given
    * parameters apply.   Note:  This method will throw an exception if more than one constructor is applicable and
    * no constructor has parameters whose types exactly match the types of the actual parameters.
    *
    * @param classObject the {@code Class} object for the class to be instantiated.
    * @param params      the list of parameters to pass to the constructor
    * @return a new instance of the class specified by {@code classObject}
    * @throws InvocationTargetException if {@code newInstance} throws this exception.
    * @throws IllegalAccessException    if {@code newInstance} throws this exception.
    * @throws InstantiationException    if {@code newInstance} throws this exception.
    * @throws DLException               if no constructor matches the set of parameters given
    */
   private static <T> T instantiate(Class<T> classObject, Object... params) throws IllegalAccessException,
         InstantiationException, InvocationTargetException, DLException {

      // If there are no parameters, there is only one constructor that can possibly match:  The nullary constructor
      if (params.length == 0) {
         return instantiateFromExactMatch(classObject, false, params);
      }

      // Currently, none of the parameters may be null.
      for (Object param : params) {
         if (param == null) {
            throw new DLException(String.format(NULL_PARAMETERS, classObject.getName()), null);
         }
      }


      Constructor<?> matchingConstructor = null;
      boolean multipleMatches = false;
      for (Constructor<?> constructor : classObject.getConstructors()) {
         Class<?>[] constructorParameterTypes = constructor.getParameterTypes();
         boolean match = false;
         if (constructorParameterTypes.length == params.length) {
            match = true;
            for (int i = 0; i < constructorParameterTypes.length; i++) {
               if (!Util.convertToWrapper(constructorParameterTypes[i]).isAssignableFrom(params[i].getClass())) {
                  match = false;
                  i = constructorParameterTypes.length;
               }
            } // end inner for
         }
         if (match && matchingConstructor == null) {
            matchingConstructor = constructor;
         } else if (match) {
            matchingConstructor = null;
            multipleMatches = true;
         }
      } // end foreach constructor

      if (matchingConstructor != null) {
         try {
            Constructor<T> constructor = classObject.getConstructor(matchingConstructor.getParameterTypes());
            return constructor.newInstance(params);
         } catch (NoSuchMethodException e) {
            throw new DLException(String.format(UNKNOWN_REASON,
                  classObject.getName()), e);
         }
      }
      return instantiateFromExactMatch(classObject, multipleMatches, params);
   }

   //
   // Quit methods
   //
   private static void quit(String className, Class<?> parentClass, DLException e, PrintStream error, int exitValue) {
      error.printf("Cannot instantiate class %s and assign it to %s because %s",
            className, parentClass.getName(), e.getMessage());
      System.exit(exitValue);
   }

   /**
    * Try to {@link #make(String, Class, boolean, Object...)} a new object, or exit the program if the call to {@link
    * #make(String, Class, boolean, Object...)} throws a {@link DLException}.  (Note:  This method only quits when it
    * encounters a {@link DLException}.  It does not quit if {@code rethrowRuntimeException} is {@code true} and the
    * constructor throws a {@code RuntimeException}.
    *
    * @param <T>                      A super-type of the object to be created
    * @param name                     The fully qualified name of the class to be instantiated
    * @param parentClass              The {@code Class} object for {@code T} (needed to verify that
    *                                 the cast from {@code Object} to {@code T} is safe).
    * @param rethrowRuntimeExceptions when {@code true}, then this method re-throws any {@code RuntimeException}s
    *                                 that are the cause of any {@code InvocationTargetException}.  When {@code false},
    *                                 all {@code InvocationTargetExceptions} generate a {@link DLException} (the same
    *                                 as any other exception). (See note above).
    * @param error                    the stream to which to print any error messages
    * @param exitValue                the value the process will return on exit
    * @param params                   a list of parameters to the constructor
    * @return the newly created object
    */
   public static <T> T makeOrQuit(String name, Class<T> parentClass, boolean rethrowRuntimeExceptions,
                                  PrintStream error, int exitValue,
                                  Object... params) {
      try {
         return make(name, parentClass, rethrowRuntimeExceptions, params);
      } catch (DLException e) {
         quit(name, parentClass, e, error, exitValue);

         // Quit calls System.exit(0), so this line of code should never run.
         assert false : "This line of code should never run.";
         return null;
      }
   }

   /**
    * calls {@link #makeOrQuit(String, Class, boolean, java.io.PrintStream, int, Object...)} with the default error
    * stream an exit value.
    */
   public static <T> T makeOrQuit(String name, Class<T> parentClass, boolean rethrowRuntimeExceptions,
                                  Object... params) {
      return makeOrQuit(name, parentClass, rethrowRuntimeExceptions, Warszawa.DEFAULT_ERROR_STREAM,
            Warszawa.DEFAULT_EXIT_VALUE, params);
   }


   /**
    * Calls {@link #makeOrQuit(String, Class, boolean, java.io.PrintStream, int, Object...)} with default value of
    * {@code false} for {@code rethrowRuntimeExceptions}
    */
   public static <T> T makeOrQuit(String name, Class<T> parentClass,
                                  PrintStream error, int exitValue,
                                  Object... params) {
      return makeOrQuit(name, parentClass, DEFAULT_RUNTIMEPASSTHROUGH, error, exitValue, params);
   }

   /**
    * calls {@link #makeOrQuit(String, Class, java.io.PrintStream, int, Object...)} with the default error
    * stream an exit value.
    */
   public static <T> T makeOrQuit(String name, Class<T> parentClass,
                                  Object... params) {
      return makeOrQuit(name, parentClass, DEFAULT_RUNTIMEPASSTHROUGH, Warszawa.DEFAULT_ERROR_STREAM, Warszawa.DEFAULT_EXIT_VALUE, params);
   }

}