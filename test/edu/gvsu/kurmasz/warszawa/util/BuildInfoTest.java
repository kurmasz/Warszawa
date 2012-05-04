package edu.gvsu.kurmasz.warszawa.util;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * @author Zachary Kurmas
 */
// Created  5/3/12 at 9:20 PM
// (C) Zachary Kurmas 2012

public class BuildInfoTest {

   private Date expectedDate;

   public BuildInfoTest() throws ParseException {
      expectedDate = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss")
            .parse("2012.02.19 15.32.32");
   }

   @Test
   public void loadFullBuildInfo() throws Throwable {
      BuildInfo bi = BuildInfo.make("fullBuildInfo.properties", this.getClass());
      assertEquals(expectedDate, bi.getBuildDate());
      assertEquals("1.2.6", bi.getVersion());
   }

   @Test
   public void loadMissingVersion() throws Throwable {
      BuildInfo bi = BuildInfo.make("missingVersion.properties", this.getClass());
      assertEquals(expectedDate, bi.getBuildDate());
      assertNull(bi.getVersion());
   }

   @Test
   public void loadMissingBuildDate() throws Throwable {
      BuildInfo bi = BuildInfo.make("missingBuildDate.properties", this.getClass());
      assertNull(bi.getBuildDate());
      assertEquals("1.2.6", bi.getVersion());
   }

   @Test
   public void loadUnparsableDate() {
      try {
         BuildInfo bi = BuildInfo.make("unparsableDate.properties", this.getClass());
         fail("Make should have thrown exception");
      } catch (BuildInfo.InvalidBuildInfoFile e) {
         assertEquals("Date in properties file won't parse: \"2012.02 15.32.32\"", e.getMessage());
      }
   }

   @Test
   public void missingFile() throws Throwable {
      try {
         BuildInfo bi = BuildInfo.make("noSuchFile.properties", this.getClass());
         fail("Make should have thrown exception");
      } catch (BuildInfo.InvalidBuildInfoFile e) {
         assertEquals("Build info resource file \"noSuchFile.properties\" does not exist for class " +
               "edu.gvsu.kurmasz.warszawa.util.BuildInfoTest.", e.getMessage());
      }
   }


}
