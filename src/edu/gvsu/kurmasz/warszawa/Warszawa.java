package edu.gvsu.kurmasz.warszawa;

import edu.gvsu.kurmasz.warszawa.util.BuildInfo;

import java.io.PrintStream;

/**
 * Container for constants.
 *
 * @author Zachary Kurmas
 */
// Created  2/17/12 at 7:04 PM
// (C) Zachary Kurmas 2012

public class Warszawa {

   /**
    * Value passed to {@code System.exit} by default
    */
   public static final int DEFAULT_EXIT_VALUE = 1;

   /**
    * Error messages are written here by default.
    */
   public static final PrintStream DEFAULT_ERROR_STREAM = System.err;

   /**
    * Prints the build date of this package.
    *
    * @param args not used
    */
   public static void main(String[] args) {
      BuildInfo bi = BuildInfo.make(Warszawa.class);
      System.out.println("Warszawa version " + bi.getVersion() + " built "
            + bi.getBuildDate() + ".");
   }
}
