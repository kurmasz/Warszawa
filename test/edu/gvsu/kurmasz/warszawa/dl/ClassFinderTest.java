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

import dummy.ChildOfDummy_PrivateConstructorOnly;
import org.junit.*;

/**
 * Test class for ClassFinder
 *
 * @author Zachary Kurmas
 */
// (C) 2009 Zachary Kurmas
// Created Dec 31, 2009
public class ClassFinderTest {

   private static final String THIS_PACKAGE = "edu.gvsu.kurmasz.warszawa.dl";
   private static final String THIS_CLASS = "edu.gvsu.kurmasz.warszawa.dl.ClassFinderTest";

   public static interface MethodRunner {
      public <T> void run(String name, Class<T> parent_class) throws DLException;
   }

   // /////////////////////////////////////////
   //
   // Inner classes for testing purposes
   //
   // ////////////////////////////////////////

   public static class Inner1 {
   }

   private static class Inner1_Child extends Inner1 {
   }

   public class Inner2 {
   }

   public interface Interf {
   }

   // /////////////////////////////////////////
   //
   // "Helper" methods
   //
   // ////////////////////////////////////////


   //
   // Good, Checked
   //

   /**
    * Verify that getClass throws the expected exception and contains the expected message
    *
    * @param name               name of class loaded
    * @param parent_class       parent class
    * @param expected_exception exception expected
    * @param expected_message   message expected in exception
    */
   public static void verifyException(String name, Class<?> parent_class,
                                      Class<? extends Throwable> expected_exception,
                                      String expected_message,
                                      MethodRunner runner) {
      try {
         runner.run(name, parent_class);
         Assert.fail("Exception Expected when trying to find " + name
               + " as " + parent_class);
      } catch (DLException e) {
         Throwable cause = e.getCause();
         Class<?> cause_class = null;
         if (cause != null) {
            cause_class = cause.getClass();
         }
         Assert.assertEquals(
               "Wrong exception type",
               expected_exception, cause_class);
         Assert.assertEquals("Wrong message", expected_message, e
               .getMessage());
      }
   }

   public static <T> void verifyGetClassException(final String name, final Class<T> parent_class,
                                                  Class<? extends Throwable> expected_exception,
                                                  String expected_message) {
      verifyException(name, parent_class, expected_exception, expected_message,
            new MethodRunner() {
               public <T> void run(String lname, Class<T> lparent_class) throws DLException {
                  ClassFinder.getClass(lname, lparent_class);
               }
            });
   }


   /**
    * Test that searching for a class that doesn't exist returns the correct
    * error message.
    *
    * @param name
    */
   public void verifyExceptionForMissingClass(String name) {
      verifyGetClassException(name, Object.class, ClassNotFoundException.class,
            String.format(ClassFinder.CLASS_NOT_FOUND1, name));
   }

   /**
    * Test that searching for an incorrectly capitalized class returns the
    * expected error message.
    *
    * @param name
    */
   public void verifyExceptionForMiscapitalizedClass(String name) {
      if (System.getProperty("os.name").equals("Linux")) {
          verifyExceptionForMissingClass(name);
      } else {
      verifyGetClassException(name, Object.class,
            NoClassDefFoundError.class, String.format(
            ClassFinder.CLASS_NOT_FOUND_BAD_CAPS, name));
      }
   }

   /**
    * Test that using an incorrect superclass generates the expected error
    * message.
    *
    * @param name
    * @param parent
    */
   public void verifyExceptionForIncorrectSuperclass(String name, Class<?> parent) {
      verifyGetClassException(name, parent, null, String.format(
            ClassFinder.CLASS_NOT_ASSIGNABLE_TO, name, parent.getName()));
   }

   /**
    * Test the searching for a class with the fully qualified name {@code name}
    * generates a Class object that (1) matches {@code expectedAnswer}, and is
    * compatible with the {@code parentClass}
    *
    * @param name        fully qualified class name
    * @param expected    class type expected
    * @param parentClass parent class
    */
   public void verifyClassFound(String name, Class<?> expected, Class<?> parentClass) {
      try {
         Class<?> observed = ClassFinder.getClass(name, parentClass);
         Assert.assertEquals("getClass(" + name + ")", expected, observed);
      } catch (DLException e) {
         Assert.fail("DLException unexpectedly thrown when searching for " + name
               + ": " + e.getMessage());
      }
   }

   /**
    * Test the searching for a class with the fully qualified name {@code name}
    * generates a Class object that (1) matches {@code expectedAnswer}, and is
    * compatible with *each* of the {@code parentClases}
    *
    * @param name          name of class to load
    * @param expectedClass class expected
    * @param parentClasses list of parents to check
    */
   public void verifyClassFound(String name, Class<?> expectedClass,
                                Class<?>... parentClasses) {
      for (Class<?> this_class : parentClasses) {
         verifyClassFound(name, expectedClass, this_class);
      }
   }

   public void verifyClassFound(String name, Class<?> expectedClass) {
      verifyClassFound(name, expectedClass, Object.class, expectedClass);
   }

   /**
    * Test searching for a class in the default package. These tests require
    * special handling because there is no way to refer to them statically in
    * code that uses packages.  (In other words, we need to use Class.forName()
    * to create the expected Class object.)
    *
    * @param name
    * @param parents
    */
   public void verifyDefaultPackageClassFound(String name, String... parents) {
      try {
         verifyClassFound(name, Class.forName(name), Object.class);
         for (String parent : parents) {
            verifyClassFound(name, Class.forName(name), Class.forName(parent));
         }
         verifyClassFound(name, Class.forName(name), Object.class);
      } catch (ClassNotFoundException e) {
         Assert.fail("Class unexpectedly not found.");
      }
   }


   // /////////////////////////////////////////
   //
   // Actual tests
   //
   // ////////////////////////////////////////
   @Test(expected = IllegalArgumentException.class)
   public void getClassThrowsExceptionIfClassNameNull() throws Throwable {
      ClassFinder.getClass(null, Object.class);
   }

   @Test(expected = IllegalArgumentException.class)
   public void getClassThrowsExceptionIfParentClassNulll() throws Throwable {
      ClassFinder.getClass("fred", null);
   }

   @Test
   public void getClassThrowsExceptionIfNoSuchClass() throws Throwable {
      //
      // No such class anywhere
      //
      verifyExceptionForMissingClass("noSuchClass");
      verifyExceptionForMissingClass("fred.DummyClass");
      verifyExceptionForMissingClass(".DummyClass");

      // Class exists but in a different package
      verifyExceptionForMissingClass("String"); // should be "java.lang.String"
      verifyExceptionForMissingClass("java.lang.Arrays"); // should be "java.util.Arrays"
      verifyExceptionForMissingClass("java.lang.DummyClass"); // should be "java.util.Arrays"

      // missing because it isn't capitalized correctly.
      verifyExceptionForMiscapitalizedClass("DummyCLASS");
      verifyExceptionForMiscapitalizedClass("dummy.DummyCLASS");
      verifyExceptionForMiscapitalizedClass("Dummy.DummyClass");
   }

   @Test
   public void getClassThrowsExceptionIfIncorrectSuperclass() throws Throwable {
      // completely wrong superclass
      verifyExceptionForIncorrectSuperclass("java.lang.String", java.lang.Boolean.class);

      // same name, different package
      verifyExceptionForIncorrectSuperclass("Boolean", java.lang.Boolean.class);
      verifyExceptionForIncorrectSuperclass("Boolean", dummy.Boolean.class);
      verifyExceptionForIncorrectSuperclass("dummy.Boolean", java.lang.Boolean.class);
      verifyExceptionForIncorrectSuperclass("java.lang.Boolean", dummy.Boolean.class);

      // interface that doesn't apply
      verifyExceptionForIncorrectSuperclass("java.lang.NumberFormatException", Comparable.class);

      // Wrong direction:  Integer implements Number, not vice versa.
      verifyExceptionForIncorrectSuperclass("java.lang.Number", java.lang.Integer.class);
      // Wrong direction:  InputStream is the superclass of FileInputStream
      verifyExceptionForIncorrectSuperclass("java.io.InputStream", java.io.FileInputStream.class);
   }

   /**
    * Test that getClass returns the correct Class<?> object, or throws the
    * correct exception
    */
   @Test
   public void getClassFindsClasses() {

      //
      // Java library
      //

      // Normal
      verifyClassFound("java.lang.String", String.class, Object.class, String.class,
            Comparable.class, java.io.Serializable.class);
      verifyClassFound("java.lang.reflect.Method", java.lang.reflect.Method.class,
            Object.class, java.lang.reflect.AccessibleObject.class,
            java.lang.reflect.AnnotatedElement.class,
            java.lang.reflect.GenericDeclaration.class,
            java.lang.reflect.Method.class);

      verifyClassFound("java.lang.Integer", Integer.class, Object.class,
            java.lang.Number.class, Comparable.class, Integer.class);
      verifyClassFound("java.lang.Number", Number.class, Object.class,
            java.lang.Number.class, java.io.Serializable.class);

      // Normal and User-defined

      verifyClassFound(THIS_PACKAGE + ".ClassFinder", ClassFinder.class,
            Object.class, ClassFinder.class);

      verifyClassFound("dummy.AbstractClass", dummy.AbstractClass.class,
            Object.class, dummy.AbstractClass.class);
      verifyClassFound("dummy.Boolean", dummy.Boolean.class, Object.class,
            dummy.Boolean.class);
      verifyClassFound("dummy.ChildOfProtectedInnerDummyClass",
            dummy.ChildOfProtectedInnerDummyClass.class, Object.class,
            dummy.DummyClass.getIPS_Default(),
            dummy.ChildOfProtectedInnerDummyClass.class);
      verifyClassFound("dummy.ChildOfDummy_NoDefaultConstructor",
            dummy.ChildOfDummy_NoDefaultConstructor.class, Object.class,
            dummy.DummyClass.class,
            dummy.ChildOfDummy_NoDefaultConstructor.class);
      verifyClassFound("dummy.ChildOfDummy_PrivateConstructorOnly",
            ChildOfDummy_PrivateConstructorOnly.class, Object.class,
            dummy.DummyClass.class,
            ChildOfDummy_PrivateConstructorOnly.class);
      verifyClassFound("dummy.ChildOfDummy", dummy.ChildOfDummy.class, Object.class,
            dummy.DummyClass.class, dummy.ChildOfDummy.class);
      verifyClassFound("dummy.DummyClass", dummy.DummyClass.class, Object.class,
            dummy.DummyClass.class);
      verifyClassFound("dummy.ChildOfInnerDummyClass",
            dummy.ChildOfInnerDummyClass.class, Object.class,
            dummy.DummyClass.InnerStatic.class,
            dummy.ChildOfInnerDummyClass.class);
      verifyClassFound("dummy.PrivateChildOfDummy_ExplicitConstructor", dummy.DummyClass
            .getPrivateChild(), Object.class, dummy.DummyClass.class,
            dummy.DummyClass.getPrivateChild());

      // Shares name with class in a different package
      verifyClassFound("java.lang.Boolean", Boolean.class, Object.class,
            Comparable.class, Boolean.class);
      verifyClassFound("dummy.Boolean", dummy.Boolean.class, Object.class,
            dummy.Boolean.class);
      verifyClassFound("dummy.DummyClass", dummy.DummyClass.class, Object.class,
            dummy.DummyClass.class); // yes, this is a duplicate.
      verifyDefaultPackageClassFound("Boolean"); // yes, this is a duplicate

      //
      // Test loads from the default package.
      // This must be done specially because we can't make any compile-time
      // references to classes in the default package from the current
      // package. (In general, you can't access the default package from a
      // named package.)
      //
      verifyDefaultPackageClassFound("Boolean");
      verifyDefaultPackageClassFound("Child2OfInnerDummyClass",
            "ClassInDefaultPackage$InnerProtected_DefaultConstructor");
      verifyDefaultPackageClassFound("ChildOfDummy", "DummyClass");
      verifyDefaultPackageClassFound("ChildOfInnerDummyClass",
            "ClassInDefaultPackage$InnerPublic");
      verifyDefaultPackageClassFound("ClassInDefaultPackage");
      verifyDefaultPackageClassFound("DummyClass");
      verifyDefaultPackageClassFound("ChildOfNamedPackageInnerClass",
            "dummy.DummyClass$InnerStatic");

      // Make sure we don't get something from a different package.
      Class<?> bool = null;
      try {
         bool = ClassFinder.getClass("Boolean", Object.class);
      } catch (DLException e) {
         Assert.fail("Shouldn't get this exception.");
      }
      Assert.assertTrue(!bool.equals(Boolean.class));
      Assert.assertTrue(!bool.equals(java.lang.Boolean.class));
      Assert.assertTrue(!bool.equals(dummy.Boolean.class));

      // Generic
      verifyClassFound("java.util.ArrayList", java.util.ArrayList.class,
            Object.class, java.util.AbstractList.class,
            java.util.AbstractCollection.class, java.util.ArrayList.class);

      // Inner class
      verifyClassFound("java.lang.Character$Subset",
            java.lang.Character.Subset.class, Object.class,
            java.lang.Character.Subset.class);

      verifyClassFound(THIS_CLASS + "$Inner1", Inner1.class, Object.class,
            Inner1.class);
      verifyClassFound(THIS_CLASS + "$Inner2", Inner2.class, Object.class,
            Inner2.class);
      verifyClassFound(THIS_CLASS + "$Interf", Interf.class, Object.class,
            Interf.class);
      verifyClassFound(THIS_CLASS + "$Inner1_Child", Inner1_Child.class,
            Object.class, Inner1.class, Inner1_Child.class);

      verifyClassFound("dummy.DummyClass$InnerStatic",
            dummy.DummyClass.InnerStatic.class, Object.class,
            dummy.DummyClass.InnerStatic.class);
      verifyClassFound("dummy.DummyClass$InnerProtectedStatic_DefaultConstructor", dummy.DummyClass
            .getIPS_Default(), Object.class, dummy.DummyClass.getIPS_Default());
      verifyClassFound("dummy.DummyClass$InnerPrivateStatic", dummy.DummyClass
            .getInnerPrivateStatic(), Object.class, dummy.DummyClass
            .getInnerPrivateStatic());

      verifyClassFound("dummy.DummyClass$InnerNonStatic",
            dummy.DummyClass.InnerNonStatic.class, Object.class,
            dummy.DummyClass.InnerNonStatic.class);

      verifyDefaultPackageClassFound("ClassInDefaultPackage$InnerPublic");
      verifyDefaultPackageClassFound("ClassInDefaultPackage$InnerProtected_DefaultConstructor");
      verifyDefaultPackageClassFound("ClassInDefaultPackage$InnerDefault_DefaultConstructor");
      verifyDefaultPackageClassFound("ClassInDefaultPackage$InnerPrivate_DefaultConstructor");

      // superclasses in different package.
      verifyClassFound("java.awt.im.InputSubset", java.awt.im.InputSubset.class,
            Object.class, java.lang.Character.Subset.class,
            java.awt.im.InputSubset.class);

      // Interface
      verifyClassFound("java.util.List", java.util.List.class, Object.class,
            java.util.Collection.class, java.util.List.class);
      verifyClassFound("java.lang.Thread$UncaughtExceptionHandler",
            java.lang.Thread.UncaughtExceptionHandler.class, Object.class,
            java.lang.Thread.UncaughtExceptionHandler.class);
      verifyClassFound("dummy.MyOtherInterface", dummy.MyOtherInterface.class);
      verifyDefaultPackageClassFound("MyInterface");

      // Enum
      verifyClassFound("java.lang.Thread$State", java.lang.Thread.State.class,
            Object.class, java.lang.Thread.State.class);

   }

   private static void printError(String name, Class<?> parent) {
      try {
         @SuppressWarnings("unused")
         Class<?> dummy = ClassFinder.getClass(name, parent);
      } catch (DLException e) {
         System.out.println(e.getMessage());
      }
   }

   /**
    * Make sure the error messages look good.
    *
    * @param args
    */
   public static void main(String args[]) {

      printError("fred", Object.class);
      System.out.println();
      printError("DUMMYCLASS", Object.class);
      System.out.println();
      printError("java.lang.Boolean", java.lang.Number.class);
      System.out.println();
   }
}
