package dummy;

/**
 * @author Zachary Kurmas
 */
// Created  12/16/11 at 2:13 PM
// (C) Zachary Kurmas 2011

public class MultiplePublicConstructors {

   public int multiplePublicConstructors;

   public MultiplePublicConstructors() {
      multiplePublicConstructors = 5;
   }

   public MultiplePublicConstructors(int i) {
      multiplePublicConstructors = i * 3;
   }

   public MultiplePublicConstructors(double d) {
      multiplePublicConstructors = (int) (2 * d);
   }

   public MultiplePublicConstructors(String s) {
      multiplePublicConstructors = Integer.parseInt(s);
   }

   public MultiplePublicConstructors(int i, int j) {
      multiplePublicConstructors = i * j;
   }

   public MultiplePublicConstructors(String s1, String s2) {
      multiplePublicConstructors = Integer.parseInt(s1) + 2 * Integer.parseInt(s2);
   }

   public MultiplePublicConstructors(int i1, String s2) {
      multiplePublicConstructors = i1 + 3 * Integer.parseInt(s2);
   }

   public MultiplePublicConstructors(String s1, int i2) {
      multiplePublicConstructors = Integer.parseInt(s1) + 4*i2;

   }
}
