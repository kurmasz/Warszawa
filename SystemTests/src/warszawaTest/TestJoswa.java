package warszawaTest;

import edu.gvsu.kurmasz.warszawa.deprecated.joswa.JoswaOption;
import edu.gvsu.kurmasz.warszawa.deprecated.joswa.JoswaOptionParser;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Just a quick program to make sure that the .jar file contains the necessary components.
 */
public class TestJoswa {
   public static class MyOptions {
      @JoswaOption
      public boolean verbose = false;

      @JoswaOption
      public Integer debugLevel = 6;
   }


   public static void allTests() throws FileNotFoundException {
      PrintWriter output = new PrintWriter(SystemTest.output("joswa.txt"));

      MyOptions mo = new MyOptions();
      JoswaOptionParser parser = new JoswaOptionParser(mo);
      parser.parse(new String[]{"--verbose", "--debug", "3"});

      output.println("Verbose is " + mo.verbose);
      output.println("Debug level is " + mo.debugLevel);
      output.close();
   }
}
