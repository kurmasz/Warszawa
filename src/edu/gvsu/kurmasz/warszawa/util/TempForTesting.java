package edu.gvsu.kurmasz.warszawa.util;

import edu.gvsu.kurmasz.warszawa.dl.ClassFinder;

public class TempForTesting {
  public static void main(String[] args) {
    System.out.println("CWD:  " + new java.io.File(".").getAbsolutePath());
    ClassFinder.loadClassByFile("out/ant_test/dummy/DummyClass.class", System.err,  1, 2, 3);
  }
}
