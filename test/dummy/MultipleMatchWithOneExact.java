package dummy;

/**
 * @author Zachary Kurmas
 */
// Created  12/16/11 at 3:10 PM
// (C) Zachary Kurmas 2011

public class MultipleMatchWithOneExact {
   public int multipleMatch2 = -1;

   public MultipleMatchWithOneExact(Number n) {
      multipleMatch2 = n.intValue() * -2;
   }

   public MultipleMatchWithOneExact(Integer o) {
      multipleMatch2 = o * 3;
   }
}
