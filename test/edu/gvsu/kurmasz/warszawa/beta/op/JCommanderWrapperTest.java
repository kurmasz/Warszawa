package edu.gvsu.kurmasz.warszawa.beta.op;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Zachary Kurmas
 */
// Created  11/14/11 at 9:20 PM
// (C) Zachary Kurmas 2011

public class JCommanderWrapperTest {

   public static class OptionSet1 {
      @Parameter(names = "--alpha")
      private int alpha;

      @Parameter(names = {"--beta", "--gamma", "--tistadecthephobpa"})
      private String beta;

      @Parameter(names = "--bellamy")
      public boolean bellamy;


      public int getAlpha() {
         return alpha;
      }

      public String getBeta() {
         return beta;
      }
   }

   @Test
   public void parseHandlesFullOptionNames() throws Throwable {
      String[] args = {"--beta", "bs", "--alpha", "14", "--bellamy"};
      OptionSet1 opt = new OptionSet1();
      JCommanderWrapper.parse(opt, args);
      assertEquals("bs", opt.getBeta());
      assertEquals(14, opt.getAlpha());
      assertTrue(opt.bellamy);
   }

   @Test
   public void parseHandlesAbbreviatedOptionNames() throws Throwable {
      String[] args = {"--bet", "bs", "--a", "14", "--bel"};
      OptionSet1 opt = new OptionSet1();
      JCommanderWrapper.parse(opt, args);
      assertEquals("bs", opt.getBeta());
      assertEquals(14, opt.getAlpha());
      assertTrue(opt.bellamy);
   }

   @Test(expected = ParameterException.class)
   public void parseThrowsExceptionIfAbbreviationIsTooShort() throws Throwable {
      String[] args = {"--be", "bs", "--a", "14"};
      OptionSet1 opt = new OptionSet1();
      JCommanderWrapper.parse(opt, args);
   }

   @Test(expected = ParameterException.class)
   public void parseThrowsExceptionIfParameterNotRecognized() throws Throwable {
      String[] args = {"--noSuchParam"};
      OptionSet1 opt = new OptionSet1();
      JCommanderWrapper.parse(opt, args);
   }

   @Test (expected = ParameterException.class)
   public void parseThrowsExceptionIfParameterTooLong() throws Throwable {
      String[] args = {"--alphax"};
      OptionSet1 opt = new OptionSet1();
      JCommanderWrapper.parse(opt, args);
   }
}
