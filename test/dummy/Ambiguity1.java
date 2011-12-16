package dummy;

/**
 * @author Zachary Kurmas
 */
// Created  12/16/11 at 3:19 PM
// (C) Zachary Kurmas 2011

public class Ambiguity1 {

   public int ambigValue = -1;

   public Ambiguity1(Object p1) {
      throw new RuntimeException("This should never run");
   }

   public Ambiguity1(Number p2) {
      ambigValue = p2.intValue();
   }

   public Ambiguity1(Number p2, int i2) {
      ambigValue = 3 * p2.intValue() + 4 * i2;
   }
}
