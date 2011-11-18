package warszawaTest;

import edu.gvsu.kurmasz.warszawa.io.InputHelper;
import edu.gvsu.kurmasz.warszawa.io.OutputHelper;

import java.io.*;
import java.nio.charset.Charset;

public class IOHelperSystemTest {



   private static BufferedReader br(InputStream is) {
      return new BufferedReader(new InputStreamReader(is));
   }

   private static void testOutputHelper() throws Exception {
      PrintWriter writer1 = OutputHelper.openWriter(SystemTest.output("testFile1"), Charset.defaultCharset(), true);
      writer1.println("Line 1");
      writer1.println("Berlin");
      writer1.print("Line 2");
      writer1.close();


      PrintWriter writer2 = OutputHelper.openMappedWriter("-", Charset.defaultCharset(), true);
      writer2.println("Line 1 to stdout");
      writer2.println("Line 2 to stdout");


      PrintWriter writer3 = OutputHelper.openMappedWriter("stderr", Charset.defaultCharset(), true);
      writer3.println("Line 1 to stderr (Monkey)");
      writer3.println("Line 2 to stderr (Giraffe)");
   }

   private static void testBzip2InputFilter() throws Exception {
      BufferedReader input = br(InputHelper.openFilteredInputStream(SystemTest.input("hamlet.txt.bz2")));
      PrintWriter writer = new PrintWriter(new File(SystemTest.output("hamlet.txt")));
      String line = input.readLine();
      while (line != null) {
         writer.println(line);
         line = input.readLine();
      }
      input.close();
      writer.close();
   }

   private static void testInputHelper() throws Exception {
      BufferedReader input = br(InputHelper.openMappedInputStream("-"));
      String line1 = input.readLine();
      String line2 = input.readLine();

      PrintWriter writer1 = OutputHelper.openWriter(SystemTest.output("testFile2"), true);
      writer1.println(line1);
      writer1.println(line2);
      writer1.close();
   }

   public static void allTests() throws Exception {
      testOutputHelper();
      testInputHelper();
      testBzip2InputFilter();
   }

}