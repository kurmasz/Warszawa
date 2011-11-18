package warszawaTest;

/**
 * @author Zachary Kurmas
 */
// Created  11/17/11 at 10:30 AM
// (C) Zachary Kurmas 2011

public class SystemTest {
   public static final String OUTPUT_DIR = "actual_output";
   public static final String INPUT_DIR = "input";

   public static String output(String name) {
      return OUTPUT_DIR + '/' + name;
   }

   public static String input(String name) {
      return INPUT_DIR + '/' + name;
   }

   public static void main(String[] args) throws Exception {
      System.out.println("First line to stdout");
      System.err.println("First line to stderr");
      IOHelperSystemTest.allTests();
      JCommanderSystemTests.allTests();
      DLTest.allTests();
      TestJoswa.allTests();
      System.out.println("Last line to stdout");
      System.err.println("Last line to stderr");
   }
}
