package dummy;

/**
 * @author Zachary Kurmas
 */
// Created  12/16/11 at 3:06 PM
// (C) Zachary Kurmas 2011

public class PublicPrivateMatchingConstructors {

   public int multipleMatch = -1;

   public PublicPrivateMatchingConstructors(Number n) {
      multipleMatch = n.intValue() *2;
   }

   private PublicPrivateMatchingConstructors(Integer o) {
      multipleMatch = o * -3;
   }
}
