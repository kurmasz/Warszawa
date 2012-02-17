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

import org.junit.*;

import java.lang.reflect.InvocationTargetException;

import static edu.gvsu.kurmasz.warszawa.dl.SimpleFactory.make;

/**
 * Test the SimpleFactory class
 *
 * @author Zachary Kurmas
 */
// (C) 2010 Zachary Kurmas
// Created Jan 4, 2010
public class SimpleFactoryTest {

   private static <T> void verifyMakeException(String name, Class<T> parent_class,
                                               Class<? extends Throwable> expected_exception,
                                               String expected_message,
                                               final Object... params) {
      // Verify that we get the expected exception regardless of whether rethrowRuntime is true or false

      ClassFinderTest.verifyException(name, parent_class, expected_exception, expected_message,
            new ClassFinderTest.MethodRunner() {
               public <T> void run(String name, Class<T> parent_class) throws DLException {
                  make(name, parent_class, false, params);
               }
            });

      ClassFinderTest.verifyException(name, parent_class, expected_exception, expected_message,
            new ClassFinderTest.MethodRunner() {
               public <T> void run(String name, Class<T> parent_class) throws DLException {
                  make(name, parent_class, true, params);
               }
            });

      ClassFinderTest.verifyException(name, parent_class, expected_exception, expected_message,
            new ClassFinderTest.MethodRunner() {
               public <T> void run(String name, Class<T> parent_class) throws DLException {
                  make(name, parent_class, params);
               }
            });
   }


   /**
    * Verify that we get the correct error when we attempt to instantiate a class that
    * isn't public.
    *
    * @param name name of class to load
    */
   private void verifyExceptionWhenClassNotAccessible(String name) {
      verifyMakeException(name, Object.class, null,
            String.format(SimpleFactory.ILLEGAL_ACCESS_NONPUBLIC_CLASS, name));
   }

   /**
    * Verify that we get the correct error when we attempt to instantiate a class
    * whose nullary constructor is private
    *
    * @param name   name of class to load
    * @param params parameters to pass to the constructor
    */
   private void verifyExceptionWhenConstructorNotPublic(String name, Object... params) {
      verifyMakeException(name, Object.class, NoSuchMethodException.class,
            String.format(SimpleFactory.NO_SUCH_PUBLIC_CONSTRUCTOR, name), params);
   }

   /**
    * Verify that we get the correct error when we attempt to instantiate a class
    * whose nullary constructor is private
    *
    * @param name   name of class to load
    * @param params parameters to pass to the constructor
    */
   private void verifyExceptionWhenNoAppropriateConstructor(String name, Object... params) {
      verifyMakeException(name, Object.class, NoSuchMethodException.class,
            String.format(SimpleFactory.NO_SUCH_PUBLIC_CONSTRUCTOR, name), params);
   }


   /**
    * Verify that we get the correct error when we attempt to instantiate a class
    * where multiple constructors match the given parameters.
    *
    * @param name   name of class to load
    * @param params parameters to pass to the constructor
    */
   private void verifyExceptionWhenAmbiguousConstructor(String name, Object... params) {
      verifyMakeException(name, Object.class, null,
            String.format(SimpleFactory.MULTIPLE_CONSTRUCTORS_MATCH, name), params);
   }

   /**
    * Verify that we get the correct error when we go to instantiate a class that
    * is abstract.
    *
    * @param name name of class to load
    */
   private void verifyExceptionWhenAbstract(String name) {
      verifyMakeException(name, Object.class, null,
            String.format(SimpleFactory.INSTANTIATION_EXCEPTION_ABSTRACT_CLASS, name));
   }

   /**
    * Verify that we get the correct error when we go to instantiate a class that
    * is an interface.
    *
    * @param name name of class to load
    */
   private void verifyExceptionWhenInterface(String name) {
      verifyMakeException(name, Object.class, null,
            String.format(SimpleFactory.INSTANTIATION_EXCEPTION_INTERFACE, name));
   }


   /**
    * Verify that we get the correct error when we go to instantiate a class that
    * is non-static inner class.
    *
    * @param name name of class to load
    */
   private void verifyNonStaticInner(String name) {
      verifyMakeException(name, Object.class, null,
            String.format(SimpleFactory.INSTANTIATION_EXCEPTION_NON_STATIC_INNER, name));
   }

   /**
    * Test that we get the correct error when we go to instantiate a class that
    * has no nullary construcor.
    *
    * @param name name of class to load
    */
   private void testNoDefaultConstructor(String name) {
      verifyMakeException(name, Object.class, NoSuchMethodException.class,
            String.format(SimpleFactory.NO_SUCH_PUBLIC_CONSTRUCTOR, name));
   }

   /**
    * Test that we get the correct error when we go to instantiate a class that
    * whose constructor throws an exception.
    *
    * @param name              name of class to load
    * @param expectedException exception constructor will throw
    */
   private void verifyBadConstructor(String name, Class<? extends Throwable> expectedException) {
      String expected_message = String.format(SimpleFactory.EXCEPTION_FROM_CONSTRUCTOR, name);

      ClassFinderTest.verifyException(name, Object.class, expectedException, expected_message,
            new ClassFinderTest.MethodRunner() {
               public <T> void run(String name, Class<T> parent_class) throws DLException {
                  make(name, parent_class, false);
               }
            });

      ClassFinderTest.verifyException(name, Object.class, expectedException, expected_message,
            new ClassFinderTest.MethodRunner() {
               public <T> void run(String name, Class<T> parent_class) throws DLException {
                  make(name, parent_class);
               }
            });
   }

   /**
    * Instantiate a class in the default package, then check that is has the correct
    * "magic" integer.  (Each class in the default package has a public int field.
    * We can verify that the correct class was loaded by checking the value of this field.)
    *
    * We need to use reflection to access the field because we don't want to require all of the test classes to
    * implement a specific interface.
    *
    * @param className    name of class to load
    * @param fieldName    field with "magic integer"
    * @param expected_int expected value of magic integer
    * @param parent       return type for make
    * @throws DLException if things aren't set up correctly
    */
   private void verifyCorrectClassInstantiated_inner(String className, String fieldName, int expected_int,
                                                     Class<?> parent, Object... params) throws DLException {

      Object o = make(className, parent, false, params);
      try {
         int value = o.getClass().getField(fieldName).getInt(o);
         Assert.assertEquals(expected_int, value);
      } catch (Exception e) {
         Assert.fail("Caught unexpected exception: " + e);
      }
   }

   private void verifyCorrectClassInstantiated(String className, String fieldName, int expected_int,
                                               Class<?>... parents) throws DLException, ClassNotFoundException {
      verifyCorrectClassInstantiated_inner(className, fieldName, expected_int, Object.class);
      verifyCorrectClassInstantiated_inner(className, fieldName, expected_int, Class.forName(className));
      for (Class<?> parent : parents) {
         verifyCorrectClassInstantiated_inner(className, fieldName, expected_int, parent);
      }
   }

   private void verifyCorrectConstructorUsed(String className, String fieldName, int expected_int, Object... params) throws DLException {
      verifyCorrectClassInstantiated_inner(className, fieldName, expected_int, Object.class, params);
   }


   // ////////////////////////////////////////////////////////////////////////////////////////
   //
   // Test methods
   //
   // ////////////////////////////////////////////////////////////////////////////////////////

   @Test(expected = IllegalArgumentException.class)
   public void makeThrowsExceptionIfNameIsNull() throws Throwable {
      make(null, Object.class, false);
   }

   @Test(expected = IllegalArgumentException.class)
   public void makeThrowsExceptionIfParentClassIsNull() throws Throwable {
      make("fred", null, false);
   }

   @Test(expected = DLException.class)
   public void throwsExceptionFromFindClass() throws Throwable {
      make("noSuchClass.a.b.c", Object.class, false);
   }

   @Test(expected = DLException.class)
   public void throwsExceptionFromFindClass2() throws Throwable {
      make("noSuchClass.a.b.c", Object.class, true);
   }

   @Test(expected = DLException.class)
   public void throwsExceptionFromFindClass3() throws Throwable {
      make("noSuchClass.a.b.c", Object.class);
   }

   @Test
   public void verifyExceptionsWhenClassNotAccessible() throws Throwable {
      verifyExceptionWhenClassNotAccessible("ClassInDefaultPackage$InnerDefault_ExplicitConstructor");
      verifyExceptionWhenClassNotAccessible("ClassInDefaultPackage$InnerPrivate_DefaultConstructor");
      verifyExceptionWhenClassNotAccessible("ClassInDefaultPackage$InnerPrivate_ExplicitConstructor");
      verifyExceptionWhenClassNotAccessible("ClassInDefaultPackage$InnerPrivate_DefaultConstructor");

      verifyExceptionWhenClassNotAccessible("dummy.DummyClass$InnerDefaultStatic_DefaultConstructor");
      verifyExceptionWhenClassNotAccessible("dummy.DummyClass$InnerDefaultStatic_ExplicitConstructor");
      verifyExceptionWhenClassNotAccessible("dummy.DummyClass$InnerPrivateStatic");
      verifyExceptionWhenClassNotAccessible("dummy.DummyClass$InnerPrivateStatic_ExplicitConstructor");

      verifyExceptionWhenClassNotAccessible("dummy.PrivateChildOfDummy_ExplicitConstructor");
      verifyExceptionWhenClassNotAccessible("dummy.PrivateChildOfDummy_DefaultConstructor");
   }

   @Test
   public void verifyExceptionWhenNullaryConstructorNotPublic() throws Throwable {
      verifyExceptionWhenConstructorNotPublic("dummy.ChildOfDummy_PrivateConstructorOnly");
      verifyExceptionWhenConstructorNotPublic("dummy.ChildOfDummy_PackageProtectedConstructorOnly");
      verifyExceptionWhenConstructorNotPublic("dummy.ChildOfDummy_ProtectedConstructorOnly");

      verifyExceptionWhenConstructorNotPublic("dummy.ChildOfDummy_NullaryConstructorPrivate");
   }

   @Test
   public void verifyExceptionWhenAbstract() throws Throwable {
      verifyExceptionWhenAbstract("dummy.AbstractClass");
      verifyExceptionWhenAbstract("java.io.InputStream");
   }

   @Test
   public void verifyExceptionWhenInterface() throws Throwable {
      verifyExceptionWhenInterface("MyInterface");
      verifyExceptionWhenInterface("dummy.MyOtherInterface");
      verifyExceptionWhenInterface("java.util.List");
   }

   @Test
   public void verifyExceptionWhenNoNullaryConstructor() throws Throwable {
      testNoDefaultConstructor("java.lang.Integer");
      testNoDefaultConstructor("dummy.ChildOfDummy_NoDefaultConstructor");
      testNoDefaultConstructor("dummy.Seasons");
   }

   @Test
   public void verifyExceptionWhenNonStaticInner() throws Throwable {
      verifyNonStaticInner("dummy.DummyClass$InnerNonStatic");
   }

   @Test
   public void verifyExceptionWhenConstructorThrowsException() {
      this.verifyBadConstructor("dummy.ClassWithBadConstructor", InvocationTargetException.class);
   }

   @Test(expected = IllegalArgumentException.class)
   public void verifyRuntimeExceptionWhenConstructorPassesRuntimeExceptions() throws DLException {
      make("dummy.ClassWithBadConstructor", Object.class, true);
   }

   @Test
   public void verifyCheckedExcpetionNotRethrown() throws Throwable {
      this.verifyBadConstructor("dummy.ClassWithBadCheckedConstructor", InvocationTargetException.class);
   }

   @Test
   public void canLoadStringAsString() throws Throwable {
      String s = make("java.lang.String", java.lang.String.class, false);
      Assert.assertEquals("", s);
   }

   @Test
   public void canLoadStringAsString2() throws Throwable {
      String s = make("java.lang.String", java.lang.String.class, true);
      Assert.assertEquals("", s);
   }

   @Test
   public void canLoadStringAsString3() throws Throwable {
      String s = make("java.lang.String", java.lang.String.class);
      Assert.assertEquals("", s);
   }

   @Test
   public void canLoadStringAsComparable() throws Throwable {
      @SuppressWarnings("unchecked")
      Comparable<String> comparable = (Comparable<String>) make("java.lang.String", Comparable.class, false);
      Assert.assertEquals(0, comparable.compareTo(""));
   }

    @Test
   public void canLoadStringAsComparable2() throws Throwable {
      @SuppressWarnings("unchecked")
      Comparable<String> comparable = (Comparable<String>) make("java.lang.String", Comparable.class, true);
      Assert.assertEquals(0, comparable.compareTo(""));
   }

    @Test
   public void canLoadStringAsComparable3() throws Throwable {
      @SuppressWarnings("unchecked")
      Comparable<String> comparable = (Comparable<String>) make("java.lang.String", Comparable.class);
      Assert.assertEquals(0, comparable.compareTo(""));
   }


   @Test
   public void correctClassInstantiatedFromDefaultPackage() throws DLException, ClassNotFoundException {
      //
      // default package
      //
      verifyCorrectClassInstantiated("Boolean", "boolean_DefaultPackage", 89);
      verifyCorrectClassInstantiated("Child2OfInnerDummyClass",
            "child2OfInnerDummyClass_DefaultPackage", 8934, Class
            .forName("ClassInDefaultPackage$InnerProtected_DefaultConstructor"));
      verifyCorrectClassInstantiated("ChildOfDummy", "childOfDummy_DefaultPackage", 892,
            Class.forName("DummyClass"));
      verifyCorrectClassInstantiated("ChildOfInnerDummyClass",
            "childOfInnerDummyClass_DefaultPackage", 9067,
            Class.forName("ClassInDefaultPackage$InnerPublic"));

      dummy.DummyClass.InnerStatic t1 = make("ChildOfNamedPackageInnerClass",
            dummy.DummyClass.InnerStatic.class, false);
      Assert.assertEquals(7743, t1.getMagicNumber());

       dummy.DummyClass.InnerStatic t1b = make("ChildOfNamedPackageInnerClass",
            dummy.DummyClass.InnerStatic.class, true);
      Assert.assertEquals(7743, t1b.getMagicNumber());

       dummy.DummyClass.InnerStatic t1c = make("ChildOfNamedPackageInnerClass",
            dummy.DummyClass.InnerStatic.class);
      Assert.assertEquals(7743, t1c.getMagicNumber());

      verifyCorrectClassInstantiated("ClassInDefaultPackage", "classInDefaultPackage", 9);
      verifyCorrectClassInstantiated("ClassInDefaultPackage$InnerPublic",
            "innerPublic_classInDefaultPackage", 10);
      verifyCorrectClassInstantiated("ClassInDefaultPackage$InnerProtected_ExplicitConstructor",
            "innerProtected_classInDefaultPackage", 121);
      verifyCorrectClassInstantiated("DummyClass", "dummyClass_DefaultPackage", 18);
   }

   @Test
   public void correctClassInstantiatedFromNonDefaultPackage() throws DLException, ClassNotFoundException {

      dummy.Boolean b1 = make("dummy.Boolean", dummy.Boolean.class, false);
      Assert.assertEquals(89, b1.boolean_DummyPackage);

      dummy.ChildOfProtectedInnerDummyClass c1 = make(
            "dummy.ChildOfProtectedInnerDummyClass",
            dummy.ChildOfProtectedInnerDummyClass.class, false);
      Assert.assertEquals(1967, c1.childOfProtectedInnerDummyClass_DummyPackage);

      dummy.ChildOfDummy c2 = make("dummy.ChildOfDummy", dummy.ChildOfDummy.class, false);
      Assert.assertEquals(7357, c2.getMagicNumber());

      dummy.DummyClass c3 = make("dummy.ChildOfDummy", dummy.DummyClass.class, false);
      Assert.assertEquals(7357, c3.getMagicNumber());

      dummy.DummyClass c4 = make("dummy.DummyClass", dummy.DummyClass.class, false);
      Assert.assertEquals(90, c4.getMagicNumber());

      dummy.ChildOfInnerDummyClass d4 = make(
            "dummy.ChildOfInnerDummyClass",
            dummy.ChildOfInnerDummyClass.class, false);
      Assert.assertEquals(909, d4.getMagicNumber());

      dummy.DummyClass.InnerStatic d5 = make(
            "dummy.ChildOfInnerDummyClass",
            dummy.DummyClass.InnerStatic.class, false);
      Assert.assertEquals(909, d5.getMagicNumber());

      dummy.DummyClass.InnerStatic d6 = make("dummy.DummyClass$InnerStatic",
            dummy.DummyClass.InnerStatic.class, false);
      Assert.assertEquals(934, d6.getMagicNumber());


      verifyCorrectClassInstantiated("dummy.DummyClass$InnerProtectedStatic_ExplicitConstructor",
            "dummyClass_InnerProtectedStatic", 2221);
   }

   private static void printError(String name) {
      try {
         make(name, Object.class, false);
      } catch (DLException e) {
         System.out.println(e.getMessage());
         System.out.println(e.getCause());
      }
   }

   public static void main(String[] args) {
      printError("dummy.PrivateChildOfDummy_ExplicitConstructor");
      System.out.println();
      printError("ClassInDefaultPackage$InnerPrivate");
      System.out.println();
      printError("java.io.InputStream");
      System.out.println();
      printError("java.lang.Integer");
      System.out.println();
      printError("dummy.ChildOfDummy_NullaryConstructorPrivate");
      System.out.println();
      printError("dummy.DummyClass$InnerNonStatic");
      System.out.println();
      printError("dummy.ClassWithBadConstructor");
      System.out.println();
      printError("dummy.Seasons");
      System.out.println();

      /*
        try {
        Class<?> cls = ClassFinder.getClass("dummy.DummyClass$InnerStatic", Object.class);
        System.out.println(Modifier.isStatic(cls.getModifiers()));
        for (Constructor<?> c : cls.getConstructors()) {
           System.out.println(c);
        }
        } catch (Exception e) {
           System.out.println(e);
        }
        */
   }


   /////////////////////////////////////////////////////////////////////////////////////
   //
   // Test instantiating with parameters
   //
   ////////////////////////////////////////////////////////////////////////////////////

   @Test
   public void chosenConstructorIsNotPublic() throws Throwable {
      verifyExceptionWhenConstructorNotPublic("dummy.ChildOfDummy_NoPublicConstructor", "String");
      verifyExceptionWhenConstructorNotPublic("dummy.ChildOfDummy_NoPublicConstructor", 17);
      verifyExceptionWhenConstructorNotPublic("dummy.ChildOfDummy_NoPublicConstructor", true);
   }

   @Test
   public void noAppropriateConstructor() throws Throwable {
      verifyExceptionWhenNoAppropriateConstructor("java.lang.String", 14);
      verifyExceptionWhenNoAppropriateConstructor("java.lang.String", new Object());
      verifyExceptionWhenNoAppropriateConstructor("java.lang.String", "Bob", "John");
      verifyExceptionWhenNoAppropriateConstructor("java.lang.String", 14, "John");

      verifyExceptionWhenNoAppropriateConstructor("dummy.MultiplePublicConstructors", 3, 4, 5);
      verifyExceptionWhenNoAppropriateConstructor("dummy.MultiplePublicConstructors", "Torra", "Torra", "Torra");
   }

   @Test
   public void correctConstructorCalled() throws Throwable {
      verifyCorrectConstructorUsed("dummy.MultiplePublicConstructors", "multiplePublicConstructors", 5);
      verifyCorrectConstructorUsed("dummy.MultiplePublicConstructors", "multiplePublicConstructors", 45, 15);
      verifyCorrectConstructorUsed("dummy.MultiplePublicConstructors", "multiplePublicConstructors", 30, 15.25);
      verifyCorrectConstructorUsed("dummy.MultiplePublicConstructors", "multiplePublicConstructors", 25, "25");

      verifyCorrectConstructorUsed("dummy.MultiplePublicConstructors", "multiplePublicConstructors", 12, 3, 4);
      verifyCorrectConstructorUsed("dummy.MultiplePublicConstructors", "multiplePublicConstructors", 27,
            new Integer(3), 9);

      verifyCorrectConstructorUsed("dummy.MultiplePublicConstructors", "multiplePublicConstructors", 19, "5", "7");
      verifyCorrectConstructorUsed("dummy.MultiplePublicConstructors", "multiplePublicConstructors", 26, 5, "7");
      verifyCorrectConstructorUsed("dummy.MultiplePublicConstructors", "multiplePublicConstructors", 33, "5", 7);

      // Although the first parameter is ambiguous, the presence of a second should fix this.
      verifyCorrectConstructorUsed("dummy.Ambiguity1", "ambigValue", 51, 5, 9);
   }

   @Test
   public void createNonStaticInner() throws Throwable {
      verifyCorrectConstructorUsed("dummy.DummyClass$InnerNonStatic", "innerNonStatic", 8934,
            new dummy.DummyClass());
   }

   @Test
   public void selectsPublicVersion() throws Throwable {
      // Make sure the public constructor is used instead of the more specific private constructor
      verifyCorrectConstructorUsed("dummy.PublicPrivateMatchingConstructors", "multipleMatch", 94, 47);
   }

   @Test
   public void selectsExactMatchIfMultipleMatches() throws Throwable {
      verifyCorrectConstructorUsed("dummy.MultipleMatchWithOneExact", "multipleMatch2", 57, 19);

   }

   @Test
   public void detectAmbiguity() throws Throwable {
      verifyExceptionWhenAmbiguousConstructor("dummy.Ambiguity1", 17);

      // Note:  Changing the type of the parameters doesn't help.  The
      // algorithm uses the actutal type of the object, not the type of its variable
      Number n = 17;
      verifyExceptionWhenAmbiguousConstructor("dummy.Ambiguity1", n);
   }

}
