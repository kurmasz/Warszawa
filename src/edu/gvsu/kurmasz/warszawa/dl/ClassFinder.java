/**
 * Copyright (c) Zachary Kurmas 2009
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package edu.gvsu.kurmasz.warszawa.dl;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.regex.Pattern;

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
   * {@link SimpleFactory#make(String, Class, Object...)} for a discussion of the purpose
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
   * takes no parameters, {@code false} otherwise.
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


  /**
   * Loads the class contained in the specified .class file.
   *
   * @param path               The path of the class file.
   * @param err                the PrintStream to which any error messages should be written
   * @param programmer_error   the return value in the event of a programmer error
   * @param invalid_parameters the return value in the even that the specified file can't be found
   * @param invalid_class      the return value in the event that the specified file is not a valid java .class file
   * @return Returns the class object after the class has been loaded.
   */
  public static Class loadClassByFile(String path, PrintStream err,
                                      int programmer_error,
                                      int invalid_parameters,
                                      int invalid_class) {

    if (!path.endsWith(".class")) {
      err.println("Programmer Error! loadClassByFile called with a string not ending in '.class'");
      System.exit(programmer_error);
    }

    File test = new File(path);
    if (!test.exists()) {
      err.printf("Class file \"%s\" not found.\n", path);
      System.exit(invalid_parameters);
    } else if (!test.canRead()) {
      err.printf("Class file \"%s\" is not readable.\n", path);
      err.println("(Check the file permissions.)");
      System.exit(invalid_parameters);
    } else if (test.isDirectory()) {
      err.printf("\"%s\" is a directory and, therefore, cannot be a Java class file.\n", path);
      System.exit(invalid_parameters);
    }

    // split path into the directory and the filename
    String filename = test.getName();

    // The class name is the filename without the .class extension.
    String className = filename.substring(0, filename.lastIndexOf('.'));


    File parentDirectory = test.getParentFile();

    // If path is just a filename (e.g., Foo.class), then parentFile will be null
    // and the file is assumed to be in the current working directory (called ".").
    // Otherwise, parentFile is the directory containing filename.
    // This parent directory is generated as a URL for use by the URLClassLoader
    URL directoryURL = null;
    try {
      if (parentDirectory == null) {
        directoryURL = new File(".").toURI().toURL();
      } else {
        directoryURL = test.getParentFile().toURI().toURL();
      }
    } catch (MalformedURLException e) {
      // I don't think it is possible for user input to cause this exception.
      // It is possibly raised by the URI#toURL.  However, File#toURI creates a valid URI,
      // so it should be convertible to a URL without a problem.

      err.println("OOPS! We can't figure out how to intentionally raise this exception.");
      err.println("If you get this message, please e-mail us and tell us how to you did");
      err.println("it so we can handle the case appropriately.");
      err.println("(Thrown when calling .toURL.toURL() in loadClassByFile.)");
      e.printStackTrace();
      System.exit(programmer_error);
    }


    Class myClass = null;
    try {
      URLClassLoader ucl = new URLClassLoader(new URL[]{directoryURL});
      myClass = Class.forName(className, true, ucl);
    } catch (ClassNotFoundException e) {
      // I don't think it's possible for user input to cause this exception.  We have already
      // verified that the requested file exists.  If that file doesn't contain the class
      // it is supposed to, we get the NoClassDefFoundError handled below instead.
      // (I think the reason we get the NoClassDefFound is that the system has identified a place
      //  where the class *should* be -- the file specified by the user -- but the class isn't there.
      //  we would get the ClassNoFound exception, if the system couldn't identify a place where the
      //  class should be.)

      err.println("OOPS! We can't figure out how to intentionally raise this exception.");
      err.println("If you get this message, please e-mail us and tell us how to you did");
      err.println("it so we can handle the case appropriately.");
      err.println("(Thrown when calling Class.forName() in loadClassByFile.)");
      e.printStackTrace();
      System.exit(programmer_error);
    } catch (NoClassDefFoundError e) {
      String toMatch = ".*\\(wrong name:\\s+(.*)/" + className + "\\)";
      err.printf("Given .class file does not appear to contain a class named \"%s\".\n", className);
      err.println(e.toString());
      if (Pattern.matches(toMatch, e.toString())) {
        err.println("   (Test classes specified using their .class files must be in the default package.\n" +
            "    Test classes in other packages must be specified by class name and --classpath.)");
      }
      System.exit(invalid_class);
    } catch (ClassFormatError e) {
      err.println("Given .class file does not appear to be a valid Java class file.");
      err.println("(Make sure this file was generated by javac.)");
      err.println(e.toString());
      System.exit(invalid_class);
    } catch (Exception e) {
      err.println("Unknown error method loadClassByFile");
      e.printStackTrace();
      System.exit(programmer_error);
    }
    return myClass;
  }

  public static Class loadClassByName(String testClassName, String[] strings,
                                      PrintStream err,
                                      int programmer_error,
                                      int invalid_class) {

    URL[] urlList = new URL[strings.length];
    for (int i = 0; i < strings.length; i++) {
      try {
        urlList[i] = new File(strings[i]).toURI().toURL();
      } catch (MalformedURLException e) {
        // I don't think it is possible for user input to cause this exception.
        // It is possibly raised by the URI#toURL.  However, File#toURI creates a valid URI,
        // so it should be convertible to a URL without a problem.

        err.println("OOPS! We can't figure out how to intentionally raise this exception.");
        err.println("If you get this message, please e-mail us and tell us how to you did");
        err.println("it so we can handle the case appropriately.");
        err.println("(Thrown when calling .toURL.toURL() in loadClassByName.)");
        e.printStackTrace();
        System.exit(programmer_error);
      }
    }

    Class myClass = null;
    try {
      URLClassLoader ucl = new URLClassLoader(urlList);
      myClass = Class.forName(testClassName, true, ucl);
    } catch (ClassNotFoundException e) {
      err.printf("Class \"%s\" not found in given classpath.\n", testClassName);
      System.exit(invalid_class);
    } catch (NoClassDefFoundError e) {
      err.printf("Class \"%s\" not found in given classpath.\n", testClassName);
      err.println(e);
      err.printf("(This particular error tends to occur when there is a file\n");
      err.printf("named \"%s.class\" in the classpath, but that file\n", testClassName);
      err.printf("does not contain a class named \"%s\".\n", testClassName);
      System.exit(invalid_class);
    } catch (Exception e) {
      err.println("Unknown error in method loadClassByName");
      e.printStackTrace();
      System.exit(programmer_error);
    }
    return myClass;

  }


} // end ClassFinder
