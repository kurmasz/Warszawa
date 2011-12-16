/**
 * Copyright (c) Zachary Kurmas 2008
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
package dummy;

/**
 *
 *
 * @author kurmasz
 *
 *
 */
// (C) 2008 Zachary Kurmas
// Created Jun 12, 2008
public class DummyClass {
   public static class InnerStatic {
      public int dummyClass_InnerStatic = 934;

      public int getMagicNumber() {
         return dummyClass_InnerStatic;
      }
   }

   @SuppressWarnings("unused")
   protected static class InnerProtectedStatic_DefaultConstructor {
      public int dummyClass_InnerProtectedStatic = 222;
   }

   @SuppressWarnings("unused")
   protected static class InnerProtectedStatic_ExplicitConstructor {
      public int dummyClass_InnerProtectedStatic = 2221;

      public InnerProtectedStatic_ExplicitConstructor() {
      }
   }

   @SuppressWarnings("unused")
   static class InnerDefaultStatic_DefaultConstructor {
      public int dummyClass_InnerProtectedStatic = 223;
   }

   @SuppressWarnings("unused")
   static class InnerDefaultStatic_ExplicitConstructor {
      public int dummyClass_InnerProtectedStatic = 2231;

      public InnerDefaultStatic_ExplicitConstructor() {
      }
   }

   @SuppressWarnings("unused")
   private static class InnerPrivateStatic {
      public int dummyClass_InnerPrivateStatic = 242;
   }

   @SuppressWarnings("unused")
   private static class InnerPrivateStatic_ExplicitConstructor {
      public int dummyClass_InnerPrivateStatic = 242;

      public InnerPrivateStatic_ExplicitConstructor() {
      }
   }

   public class InnerNonStatic {
      public int innerNonStatic;

      public InnerNonStatic() {
         innerNonStatic = 8934;
      }
   }

   public int dummyClass_DummyPackage = 90;

   public DummyClass() {
      return;
   }

   public int getMagicNumber() {
      return dummyClass_DummyPackage;
   }

   public String toString() {
      return "Running class " + this.getClass().getName();
   }

   public static Class<?> getPrivateChild() {
      return PrivateChildOfDummy_ExplicitConstructor.class;
   }


   public static Class<?> getIPS_Default() {
      return InnerProtectedStatic_DefaultConstructor.class;
   }

   public static Class<?> getInnerPrivateStatic() {
      return InnerPrivateStatic.class;
   }

}
