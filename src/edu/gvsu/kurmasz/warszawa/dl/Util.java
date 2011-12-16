package edu.gvsu.kurmasz.warszawa.dl;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilities for dynamic class loading.
 *
 * @author Zachary Kurmas
 */
// Created  12/16/11 at 2:27 PM
// (C) Zachary Kurmas 2011

public class Util {

   private static Map<Class<?>, Class<?>> makeWrapperMap() {
      Map<Class<?>, Class<?>> map = new HashMap<Class<?>, Class<?>>();
      map.put(boolean.class, Boolean.class);
      map.put(byte.class, Byte.class);
      map.put(short.class, Short.class);
      map.put(char.class, Character.class);
      map.put(int.class, Integer.class);
      map.put(long.class, Long.class);
      map.put(float.class, Float.class);
      map.put(double.class, Double.class);
      return map;
   }

   private static Map<Class<?>, Class<?>> wrapperMap = null;

   /**
    * Return a {@code Map} that maps the {@code Class} object for a primitive type onto the
    * {@code Class} objects for its wrapper class.
    *
    * @return a {@code Map} that maps the {@code Class} object for a primitive types onto the
    *         {@code Class} objects for its wrapper class.
    */
   public static Map<Class<?>, Class<?>> wrapperMap() {
      if (wrapperMap == null) {
         wrapperMap = makeWrapperMap();
      }
      return wrapperMap;
   }


   /**
    * Convert the {@code Class} object for a primitive type to the
    * {@code Class} objects for its wrapper class.
    *
    * @param input a {@code Class} object to  convert.
    * @return if {@code input} is a primitive type, return the
    *         {@code Class} objects for its wrapper class, otherwise, return {@code input}.
    */

   public static Class<?> convertToWrapper(Class<?> input) {
      if (input == null) {
         return null;
      }
      if (input.isPrimitive()) {
         return wrapperMap().get(input);
      } else {
         return input;
      }
   }


}
