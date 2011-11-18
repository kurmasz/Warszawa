package warszawaTest;

import edu.gvsu.kurmasz.warszawa.dl.DLException;
import edu.gvsu.kurmasz.warszawa.dl.SimpleFactory;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class DLTest {
   public interface TestI {
      int getMagicNumber();
   }

   public static class Test1 implements TestI {
      public int getMagicNumber() {
         return 47;
      }
   }

   private static class Test2 implements TestI {
      public int getMagicNumber() {
         return 51;
      }
   }

   public abstract class Test3 {
      public abstract String foo();
   }

   public static void tryFail(String name, PrintWriter output) {
      try {
         Object o = SimpleFactory.make(name, Object.class);
      } catch (DLException e) {
         output.println(e.getMessage());
      }
   }

   public static void allTests() throws FileNotFoundException {
      PrintWriter output = new PrintWriter(SystemTest.output("DLTest.txt"));
      try {
         TestI t1 = SimpleFactory.make("warszawaTest.DLTest$Test1", TestI.class);
         if (t1.getMagicNumber() == 47) {
            output.println("Test pass.  Magic number: " + t1.getMagicNumber());
         } else {
            output.println("Problem!  Magic number: " + t1.getMagicNumber() + " is not 47 as expected.");
         }
      } catch (DLException e) {
         output.println("problem: " + e);
      }

      output.println("Trying some stuff that should fail.");
      tryFail("fred", output);
      tryFail("warszawaTest.DLTest$Test2", output);
      tryFail("warszawaTest.DLTest$Test3", output);
      tryFail("warszawaTest.DLTest$TestI", output);

      output.close();
   }
}