package edu.gvsu.kurmasz.warszawa.io;

import org.junit.Test;

import java.io.File;

import static junit.framework.Assert.*;

/**
 * @author Zachary Kurmas
 */
// Created  9/21/11 at 1:40 PM
// (C) Zachary Kurmas 2011

public class FileHelperTest {

   @Test
   public void getSuffixReturnsSuffixWithSingleDot() throws Throwable {
      assertEquals("bz2", FileHelper.getSuffix("fred.bz2"));
   }

   @Test
   public void getSuffixReturnsSuffixWithMultipleDots() throws Throwable {
      assertEquals("bz2", FileHelper.getSuffix("fred.george.bob.bz2"));
   }

   @Test
   public void getSuffixReturnsSuffixWithMultipleConsecutiveDots() throws Throwable {
      assertEquals("bz2", FileHelper.getSuffix("bob......bz2"));
   }

   @Test
   public void getSuffixReturnsSuffixForDotFileWithSingleDot() throws Throwable {
      assertEquals("bz2", FileHelper.getSuffix(".fred.bz2"));
   }

   @Test
   public void getSuffixReturnsSuffixForDotFileWithMultipleDots() throws Throwable {
      assertEquals("bz2", FileHelper.getSuffix(".fred.george.bob.bz2"));
   }

   @Test
   public void getSuffixReturnsSuffixForDotFileWithMultipleConsecutiveDots() throws Throwable {
      assertEquals("bz2", FileHelper.getSuffix(".fred.george..........bob.bz2"));
   }

   @Test
   public void getSuffixReturnsNullIfNoDot() throws Throwable {
      assertNull(FileHelper.getSuffix("theFileWithNoDot"));
   }

   @Test
   public void getSuffixReturnsNullForEmptyString() throws Throwable {
      assertNull(FileHelper.getSuffix(""));
   }

   @Test
   public void getSuffixReturnsEmptyStringIfTrailingDot() throws Throwable {
      assertEquals("", FileHelper.getSuffix("trailingDot."));
   }

   @Test
   public void getSuffixReturnsEmptyStringIfDotFileWithTrailingDot() throws Throwable {
      assertEquals("", FileHelper.getSuffix(".trailingDot."));
      assertEquals("", FileHelper.getSuffix(".t."));
   }

   @Test
   public void getSuffixReturnsNullOnNullInput() throws Throwable {
      assertNull(FileHelper.getSuffix(null));
   }

   @Test
   public void getSuffix_LeadingDotDoesntDefineSuffix() throws Throwable {
      assertNull(FileHelper.getSuffix(".file"));
      assertNull(FileHelper.getSuffix(".f"));
   }

   @Test
   public void getSuffix_MultipleLeadingDotsDontDefineSuffix() throws Throwable {
      assertNull(FileHelper.getSuffix("..file"));
      assertNull(FileHelper.getSuffix("....file"));
      assertNull(FileHelper.getSuffix("..f"));
      assertNull(FileHelper.getSuffix("....f"));
   }

   @Test
   public void getSuffixReturnsNullForDotsOnly() throws Throwable {
      assertNull(FileHelper.getSuffix("."));
      assertNull(FileHelper.getSuffix(".."));
      assertNull(FileHelper.getSuffix("....."));
   }

   private void verifyException(String filename) {
      try {
         FileHelper.getSuffix(filename);
         fail("Filename " + filename + " should have resulted in an exception.");
      } catch (IllegalArgumentException e) {
         ;
      }
   }

   @Test
   public void getSuffixThrowsExceptionIfFilenameContainsDirectorySeparator() throws Throwable {

      verifyException("abc" + File.separator + "def.ghi");
      verifyException(File.separator + "");
      verifyException(File.separator + ".fred");
      verifyException("fred" + File.separator + "barney");
      verifyException(".fred" + File.separator + "barney");
      verifyException("fred\\/barney");
      verifyException(".fred\\/barney");
   }

}
