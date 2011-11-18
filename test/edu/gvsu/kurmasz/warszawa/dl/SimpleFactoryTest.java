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

/**
 * Test the SimpleFactory class
 *
 * @author Zachary Kurmas
 */
// (C) 2010 Zachary Kurmas
// Created Jan 4, 2010
public class SimpleFactoryTest {

   public static <T> void verifyMakeException(String name, Class<T> parent_class,
                                              Class<? extends Throwable> expected_exception,
                                              String expected_message) {
      ClassFinderTest.verifyException(name, parent_class, expected_exception, expected_message,
            new ClassFinderTest.MethodRunner() {
               public <T> void run(String name, Class<T> parent_class) throws DLException {
                  SimpleFactory.make(name, parent_class);
               }
            });
   }

   /**
    * Verify that we get the correct error when we attempt to instantiate a class that
    * isn't public.
    *
    * @param name name of class to load
    */
   public void verifyExceptionWhenClassNotPublic(String name) {
      verifyMakeException(name, Object.class, IllegalAccessException.class,
            String.format(SimpleFactory.ILLEGAL_ACCESS_NONPUBLIC_CLASS, name));
   }

   /**
    * Verify that we get the correct error when we attempt to instantiate a class
    * whose nullary constructor is private
    *
    * @param name name of class to load
    */
   public void verifyExceptionWhenConstructorNotPublic(String name) {
      verifyMakeException(name, Object.class, IllegalAccessException.class,
            String.format(SimpleFactory.ILLEGAL_ACCESS_PRIVATE_CONSTRUCTOR, name));
   }

   /**
    * Verify that we get the correct error when we go to instantiate a class that
    * is abstract.
    *
    * @param name name of class to load
    */
   public void verifyExceptionWhenAbstract(String name) {
      verifyMakeException(name, Object.class, InstantiationException.class,
            String.format(SimpleFactory.INSTANTIATION_EXCEPTION_ABSTRACT_CLASS, name));
   }

   /**
    * Verify that we get the correct error when we go to instantiate a class that
    * is an interface.
    *
    * @param name name of class to load
    */
   public void verifyExceptionWhenInterface(String name) {
      verifyMakeException(name, Object.class, InstantiationException.class,
            String.format(SimpleFactory.INSTANTIATION_EXCEPTION_INTERFACE, name));
   }


   /**
    * Verify that we get the correct error when we go to instantiate a class that
    * is non-static inner class.
    *
    * @param name name of class to load
    */
   public void verifyNonStaticInner(String name) {
      verifyMakeException(name, Object.class, InstantiationException.class,
            String.format(SimpleFactory.INSTANTIATION_EXCEPTION_NON_STATIC_INNER, name));
   }

   /**
    * Test that we get the correct error when we go to instantiate a class that
    * has no nullary construcor.
    *
    * @param name name of class to load
    */
   public void testNoDefaultConstructor(String name) {
      verifyMakeException(name, Object.class, InstantiationException.class,
            String.format(SimpleFactory.INSTANTIATION_EXCEPTION_NO_NULLARY, name));
   }

   /**
    * Test that we get the correct error when we go to instantiate a class that
    * whose constructor throws an exception.
    *
    * @param name              name of class to load
    * @param expectedException exception constructor will throw
    */
   public void verifyBadConstructor(String name, Class<? extends Throwable> expectedException) {
      verifyMakeException(name, Object.class, expectedException,
            String.format(SimpleFactory.EXCEPTION_FROM_CONSTRUCTOR, name));
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
   public void verifyCorrectClassInstantiated(String className, String fieldName, int expected_int,
                                              Class<?> parent) throws DLException {

      Object o = SimpleFactory.make(className, parent);
      try {
         int value = o.getClass().getField(fieldName).getInt(o);
         Assert.assertEquals(expected_int, value);
      } catch (Exception e) {
         Assert.fail("Caught unexpected exception: " + e);
      }
   }

   public void verifyCorrectClassInstantiated(String className, String fieldName, int expected_int,
                                              Class<?>... parents) throws DLException, ClassNotFoundException {
      verifyCorrectClassInstantiated(className, fieldName, expected_int, Object.class);
      verifyCorrectClassInstantiated(className, fieldName, expected_int, Class.forName(className));
      for (Class<?> parent : parents) {
         verifyCorrectClassInstantiated(className, fieldName, expected_int, parent);
      }
   }


   // ////////////////////////////////////////////////////////////////////////////////////////
   //
   // Test methods
   //
   // ////////////////////////////////////////////////////////////////////////////////////////

   @Test(expected = IllegalArgumentException.class)
   public void makeThrowsExceptionIfNameIsNull() throws Throwable {
      SimpleFactory.make(null, Object.class);
   }

   @Test(expected = IllegalArgumentException.class)
   public void makeThrowsExceptionIfParentClassIsNull() throws Throwable {
      SimpleFactory.make("fred", null);
   }

   @Test(expected = DLException.class)
   public void throwsExceptionFromFindClass() throws Throwable {
      SimpleFactory.make("noSuchClass.a.b.c", Object.class);
   }

   @Test
   public void verifyExceptionsWhenClassNotPublic() throws Throwable {
      verifyExceptionWhenClassNotPublic("ClassInDefaultPackage$InnerPrivate");
      verifyExceptionWhenClassNotPublic("dummy.DummyClass$InnerProtectedStatic");
      verifyExceptionWhenClassNotPublic("dummy.DummyClass$InnerPrivateStatic");
      verifyExceptionWhenClassNotPublic("dummy.PrivateChildOfDummy");
   }

   @Test
   public void verifyExceptionWhenNullaryConstructorNotPublic() throws Throwable {
      verifyExceptionWhenConstructorNotPublic("dummy.ChildOfDummy_NoPublicConstructor");
      verifyExceptionWhenConstructorNotPublic("dummy.ChildOfDummy_NoPublicNullaryConstructor");
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
      this.verifyBadConstructor("dummy.ClassWithBadConstructor", IllegalArgumentException.class);
   }

   @Test
   public void canLoadStringAsString() throws Throwable {
      String s = SimpleFactory.make("java.lang.String",
            java.lang.String.class);
      Assert.assertEquals("", s);
   }

   @Test
   public void canLoadStringAsComparable() throws Throwable {
      @SuppressWarnings("unchecked")
      Comparable<String> comparable = (Comparable<String>) SimpleFactory.make("java.lang.String", Comparable.class);
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
            .forName("ClassInDefaultPackage$InnerProtected"));
      verifyCorrectClassInstantiated("ChildOfDummy", "childOfDummy_DefaultPackage", 892,
            Class.forName("DummyClass"));
      verifyCorrectClassInstantiated("ChildOfInnerDummyClass",
            "childOfInnerDummyClass_DefaultPackage", 9067,
            Class.forName("ClassInDefaultPackage$InnerPublic"));

      dummy.DummyClass.InnerStatic t1 = SimpleFactory.make(
            "ChildOfNamedPackageInnerClass",
            dummy.DummyClass.InnerStatic.class);
      Assert.assertEquals(7743, t1.getMagicNumber());

      verifyCorrectClassInstantiated("ClassInDefaultPackage", "classInDefaultPackage", 9);
      verifyCorrectClassInstantiated("ClassInDefaultPackage$InnerPublic",
            "innerPublic_classInDefaultPackage", 10);
      verifyCorrectClassInstantiated("ClassInDefaultPackage$InnerProtected",
            "innerProtected_classInDefaultPackage", 11);
      // Can't instantiate because it's private
      // verifyCorrectClassInstantiated("ClassInDefaultPackage$InnerPrivate",
      // "innerPrivate_classInDefaultPackage", 12);
      verifyCorrectClassInstantiated("DummyClass", "dummyClass_DefaultPackage", 18);
   }

   @Test
   public void correctClassInstantiatedFromNonDefaultPackage() throws DLException, ClassNotFoundException {

      dummy.Boolean b1 = SimpleFactory.make("dummy.Boolean", dummy.Boolean.class);
      Assert.assertEquals(89, b1.boolean_DummyPackage);

      dummy.ChildOfProtectedInnerDummyClass c1 = SimpleFactory.make(
            "dummy.ChildOfProtectedInnerDummyClass",
            dummy.ChildOfProtectedInnerDummyClass.class);
      Assert.assertEquals(1967, c1.childOfProtectedInnerDummyClass_DummyPackage);

      dummy.ChildOfDummy c2 = SimpleFactory.make("dummy.ChildOfDummy", dummy.ChildOfDummy.class);
      Assert.assertEquals(7357, c2.getMagicNumber());

      dummy.DummyClass c3 = SimpleFactory.make("dummy.ChildOfDummy", dummy.DummyClass.class);
      Assert.assertEquals(7357, c3.getMagicNumber());

      dummy.DummyClass c4 = SimpleFactory.make("dummy.DummyClass", dummy.DummyClass.class);
      Assert.assertEquals(90, c4.getMagicNumber());

      dummy.ChildOfInnerDummyClass d4 = SimpleFactory.make(
            "dummy.ChildOfInnerDummyClass",
            dummy.ChildOfInnerDummyClass.class);
      Assert.assertEquals(909, d4.getMagicNumber());

      dummy.DummyClass.InnerStatic d5 = SimpleFactory.make(
            "dummy.ChildOfInnerDummyClass",
            dummy.DummyClass.InnerStatic.class);
      Assert.assertEquals(909, d5.getMagicNumber());

      dummy.DummyClass.InnerStatic d6 = SimpleFactory.make("dummy.DummyClass$InnerStatic", dummy.DummyClass.InnerStatic.class);
      Assert.assertEquals(934, d6.getMagicNumber());

      // Can't do below, because class isn't public.
      //verifyCorrectClassInstantiated("dummy.DummyClass$InnerProtectedStatic", "dummyClass_InnerProtectedStati", 222);
   }

   private static void printError(String name) {
      try {
         SimpleFactory.make(name, Object.class);
      } catch (DLException e) {
         System.out.println(e.getMessage());
         System.out.println(e.getCause());
      }
   }

   public static void main(String[] args) {
      printError("dummy.PrivateChildOfDummy");
      System.out.println();
      printError("ClassInDefaultPackage$InnerPrivate");
      System.out.println();
      printError("java.io.InputStream");
      System.out.println();
      printError("java.lang.Integer");
      System.out.println();
      printError("dummy.ChildOfDummy_NoPublicNullaryConstructor");
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
}
