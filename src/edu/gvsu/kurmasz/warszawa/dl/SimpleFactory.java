package edu.gvsu.kurmasz.warszawa.dl;

import java.lang.reflect.Modifier;

/**
 * Contains methods for instantiating objects of a class specified at runtime.
 *
 * @author Zachary Kurmas
 */
// (C) 2009 Zachary Kurmas
// Created December 31, 2009
public class SimpleFactory {

   // These are not private so that SimpleFactoryTest can access them.
   static final String BASE = "Cannot instantiate an object of \"%s\" because ";
   static final String ILLEGAL_ACCESS_NONPUBLIC_CLASS = BASE
         + "it is not public.";
   static final String ILLEGAL_ACCESS_PRIVATE_CONSTRUCTOR = BASE
         + "the constructor that takes no parameters is not public.";
   static final String INSTANTIATION_EXCEPTION_ABSTRACT_CLASS = BASE
         + "it is abstract.";
   static final String INSTANTIATION_EXCEPTION_INTERFACE = BASE
         + "it is an interface.";
   static final String INSTANTIATION_EXCEPTION_NO_NULLARY = BASE
         + "it is has no public constructors that take no parameters.";
   static final String INSTANTIATION_EXCEPTION_NON_STATIC_INNER = BASE
         + "it is a non-static inner class.  "
         + "(Every constructor for a Non-static inner class implicitly takes "
         + "the enclosing object as a parameter.)";

   private static final String UNKNOWN_REASON = BASE
         + "Class.newInstance() threw a known exception for an unknown reason."
         + "(If this happens, please e-mail the developers so we can fix our code.)";

   static final String EXCEPTION_FROM_CONSTRUCTOR = BASE
         + "%1$s's constructor threw an exception.";

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
    * T}. (This check is not strictkly necessary because if we don't have this {@code Class} object,
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
    * @param <T>         A supertype of the object to be created
    * @param name        The fully qualified name of the class to be instantiated
    * @param parentClass The {@code Class} object for {@code T} (needed to verify that
    *                    the cast from {@code Object} to {@code T} is safe).
    * @return an instance of the desired object
    * @throws DLException              if anything goes wrong.
    * @throws IllegalArgumentException if {@code name} or {@code parentClass} are {@code null}
    */
   public static <T> T make(String name, Class<T> parentClass)
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
      T typedObject = null;
      try {
         typedObject = classObject.newInstance();
      } catch (ClassCastException e) {
         throw new DLException(errorMessage
               + "it is not a subtype of the template parameter.", e);
      } catch (IllegalAccessException e) {
         if (!Modifier.isPublic(modifiers))
            throw new DLException(String.format(
                  ILLEGAL_ACCESS_NONPUBLIC_CLASS, fullClassName), e);
         else if (!ClassFinder.hasPublicNullaryConstructor(classObject)) {
            throw new DLException(String.format(
                  ILLEGAL_ACCESS_PRIVATE_CONSTRUCTOR, fullClassName), e);
         } else {
            throw new DLException(String.format(UNKNOWN_REASON,
                  fullClassName), e);
         }
      } catch (InstantiationException e2) {
         if (Modifier.isInterface(modifiers)) {
            throw new DLException(String.format(
                  INSTANTIATION_EXCEPTION_INTERFACE, fullClassName), e2);
         } else if (Modifier.isAbstract(modifiers)) {
            throw new DLException(String.format(
                  INSTANTIATION_EXCEPTION_ABSTRACT_CLASS, fullClassName),
                  e2);
         } else if (!ClassFinder.hasPublicNullaryConstructor(classObject)) {
            if (!Modifier.isStatic(modifiers)
                  && classObject.getEnclosingClass() != null) {
               throw new DLException(String.format(
                     INSTANTIATION_EXCEPTION_NON_STATIC_INNER,
                     fullClassName), e2);
            } else {
               throw new DLException(String.format(
                     INSTANTIATION_EXCEPTION_NO_NULLARY, fullClassName),
                     e2);
            }
         }

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
      } catch (Exception e3) {
         // If this happens, it should be because the constructor of the
         // class being instantiated threw an exception.

         throw new DLException(String.format(EXCEPTION_FROM_CONSTRUCTOR,
               fullClassName), e3);
      }

      return typedObject;
   }
}
