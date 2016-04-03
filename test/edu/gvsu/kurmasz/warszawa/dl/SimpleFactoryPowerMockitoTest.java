package edu.gvsu.kurmasz.warszawa.dl;

// Created  2/17/12 at 5:33 PM
// (C) Zachary Kurmas 2012

import edu.gvsu.kurmasz.warszawa.Warszawa;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SimpleFactory.class})
public class SimpleFactoryPowerMockitoTest {

  //
  // make
  //
  @Test
  public void makeCallsMakeWithFalse() throws Throwable {
    Object expected = mock(Object.class);
    String className = "clsNme";

    String one = "1";
    String two = "2";

    spy(SimpleFactory.class);

    doReturn(expected).when(SimpleFactory.class);
    SimpleFactory.make(className, Object.class, false, one, two);

    Object observed = SimpleFactory.make(className, Object.class, false, one, two);
    assertEquals(expected, observed);

    verifyStatic();
    SimpleFactory.make(className, Object.class, false, one, two);
  }

  //
  // makeOrQuit
  //

  @Test
  public void makeOrQuitCallsAndReturnsMake() throws Throwable {

    String name = "name";
    Class<?> parent = Object.class;

    mockStatic(System.class);

    spy(SimpleFactory.class);

    Integer expected = 7;

    doReturn(expected).when(SimpleFactory.class);
    SimpleFactory.make(name, parent, true);

    Object observed = SimpleFactory.makeOrQuit(name, parent, Boolean.TRUE, mock(PrintStream.class), (Integer) 443);

    assertEquals(expected, observed);
    verifyStatic();
    SimpleFactory.make(name, parent, true);
  }

  @Test
  public void makeOrQuitQuitsIfDLException() throws Throwable {

    String name = "name";
    Class<?> parent = Integer.class;

    mockStatic(System.class);

    spy(SimpleFactory.class);

    DLException e = mock(DLException.class);

    doThrow(e).when(SimpleFactory.class);
    SimpleFactory.make(name, parent, true);

    SimpleFactory.makeOrQuit(name, parent, Boolean.TRUE, mock(PrintStream.class), 343);

    verifyStatic();
    System.exit(343);
  }

  @Test(expected = IllegalArgumentException.class)
  public void makeOrQuitPassesThroughRuntimeExceptions() throws Throwable {
    SimpleFactory.makeOrQuit("dummy.ClassWithBadConstructor", Object.class, Boolean.TRUE, mock(PrintStream.class), 6);
  }

  @Ignore
  @Test
  public void makeOrQuitQuitsIfRuntimeNotPassedThrough() throws Throwable {

    mockStatic(System.class);
    SimpleFactory.makeOrQuit("dummy.ClassWithBadConstructor", Object.class, Boolean.TRUE, mock(PrintStream.class), 889);

    verifyStatic();
    System.exit(889);
  }

  @Test
  public void makeOrQuitQuitsOnCheckedException() throws Throwable {
    mockStatic(System.class);
    SimpleFactory.makeOrQuit("dummy.ClassWithBadCheckedConstructor", Object.class, Boolean.FALSE, mock(PrintStream.class), 9809);

    verifyStatic();
    System.exit(9809);
  }

  @Test
  public void makeOrQuitWritesMessageWhenQuitting() throws Throwable {
    PrintStream opt = mock(PrintStream.class);

    String name = "name";
    Class<?> parent = Integer.class;

    mockStatic(System.class);

    spy(SimpleFactory.class);

    DLException e = mock(DLException.class);

    doThrow(e).when(SimpleFactory.class);
    SimpleFactory.make(name, parent, Boolean.TRUE);

    SimpleFactory.makeOrQuit(name, parent, Boolean.TRUE, opt, 343);

    verify(opt).printf("Cannot instantiate class %s and assign it to %s because %s", name,
        parent.getName(), null);
  }

  @Test
  public void makeOrQuitWithDefaultsUsesDefaultsAndReturnsAnswer() throws Throwable {

    String name = "java.lang.Integer";
    Class<Integer> parentClass = Integer.class;
    spy(SimpleFactory.class);

    Integer observed = SimpleFactory.makeOrQuit(name, parentClass, true, "6");

    assertEquals(new Integer("6"), observed);

    verifyStatic();
    SimpleFactory.makeOrQuit(name, parentClass, Boolean.TRUE, Warszawa.DEFAULT_ERROR_STREAM, Warszawa.DEFAULT_EXIT_VALUE,
        "6");
  }

  @Test
  public void makeOrQuitNoPassthroughCallsOtherVersionAndReturnsAnswer() throws Throwable {
    String name = "java.lang.Integer";
    Class<Integer> parentClass = Integer.class;
    PrintStream err = mock(PrintStream.class);
    Integer exit_val = 988374;

    Integer expected = new Integer("6");


    spy(SimpleFactory.class);
    doReturn(expected).when(SimpleFactory.class);
    SimpleFactory.makeOrQuit(name, parentClass, Boolean.FALSE, err, exit_val, "6");

    Integer observed = SimpleFactory.makeOrQuit(name, parentClass, err, exit_val, "6");

    assertEquals(expected, observed);

    verifyStatic();
    SimpleFactory.makeOrQuit(name, parentClass, Boolean.FALSE, err, exit_val, "6");
  }

  @Test
  public void makeOrQuitNoPassthroughCallsWithDefaultParametersAndReturnsAnswer() throws Throwable {
    String name = "java.lang.Integer";
    Class<Integer> parentClass = Integer.class;

    Integer expected = new Integer("6");


    spy(SimpleFactory.class);
    doReturn(expected).when(SimpleFactory.class);
    SimpleFactory.makeOrQuit(name, parentClass, Boolean.FALSE, Warszawa.DEFAULT_ERROR_STREAM, Warszawa.DEFAULT_EXIT_VALUE,
        "6");

    Integer observed = SimpleFactory.makeOrQuit(name, parentClass, "6");

    assertEquals(expected, observed);

    verifyStatic();
    SimpleFactory.makeOrQuit(name, parentClass, Boolean.FALSE, Warszawa.DEFAULT_ERROR_STREAM, Warszawa.DEFAULT_EXIT_VALUE, "6");
  }

}
