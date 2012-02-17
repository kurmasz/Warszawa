package dummy;

// Created  2/17/12 at 5:22 PM
// (C) Zachary Kurmas 2012

public class ClassWithBadCheckedConstructor {

   public ClassWithBadCheckedConstructor() throws Exception {
      throw new Exception("Don't you hate it when this happens/");
   }
}
