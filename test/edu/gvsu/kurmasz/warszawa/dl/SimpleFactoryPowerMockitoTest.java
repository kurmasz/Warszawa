package edu.gvsu.kurmasz.warszawa.dl;

// Created  2/17/12 at 5:33 PM
// (C) Zachary Kurmas 2012

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SimpleFactory.class})
public class SimpleFactoryPowerMockitoTest {

   @Test
   public void makeCallsMakeWithFalse() throws Throwable {
      Object expected = mock(Object.class);
      String className = "clsNme";

      String one = "1";
      String two = "2";

      spy(SimpleFactory.class);

      doReturn(expected).when(SimpleFactory.class);
      SimpleFactory.make(className, Object.class, false, one, two);

      Object observed = SimpleFactory.make(className, Object.class, one, two);
      assertEquals(expected, observed );

      verifyStatic();
      SimpleFactory.make(className, Object.class, false, one, two);
   }
}
