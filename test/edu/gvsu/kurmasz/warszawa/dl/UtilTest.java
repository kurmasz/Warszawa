package edu.gvsu.kurmasz.warszawa.dl;

import org.junit.Test;

import java.lang.Boolean;
import java.sql.SQLTransactionRollbackException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Zachary Kurmas
 */
// Created  12/16/11 at 2:38 PM
// (C) Zachary Kurmas 2011

public class UtilTest {
   @Test
   public void testWrapperMap() throws Exception {
      Map<Class<?>, Class<?>> map = Util.wrapperMap();
      assertEquals(Integer.class, map.get(int.class));
      assertEquals(Boolean.class, map.get(boolean.class));
      assertEquals(Byte.class, map.get(byte.class));
      assertEquals(Short.class, map.get(short.class));
      assertEquals(Character.class, map.get(char.class));
      assertEquals(Long.class, map.get(long.class));
      assertEquals(Float.class, map.get(float.class));
      assertEquals(Double.class, map.get(double.class));
   }

   @Test
   public void convertToWrapperPassesNull() throws Exception {
      assertNull(Util.convertToWrapper(null));
   }

   @Test
   public void convertToWraperPassesNonPrimitive() throws Throwable {
      assertEquals(String.class, Util.convertToWrapper(String.class));
      assertEquals(Object.class, Util.convertToWrapper(Object.class));
      assertEquals(Number.class, Util.convertToWrapper(Number.class));
   }

   @Test
   public void convertToWrapperCovertsPrimitive() throws Throwable {
      assertEquals(Integer.class, Util.convertToWrapper(int.class));
      assertEquals(Boolean.class, Util.convertToWrapper(boolean.class));
      assertEquals(Byte.class, Util.convertToWrapper(byte.class));
      assertEquals(Short.class, Util.convertToWrapper(short.class));
      assertEquals(Character.class, Util.convertToWrapper(char.class));
      assertEquals(Long.class, Util.convertToWrapper(long.class));
      assertEquals(Float.class, Util.convertToWrapper(float.class));
      assertEquals(Double.class, Util.convertToWrapper(double.class));
   }

}
