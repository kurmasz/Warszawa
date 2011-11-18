package edu.gvsu.kurmasz.warszawa.dl;

/**
 * Thrown when an attempt at dynamically loading or instantiating a class
 * fails.
 *
 * @author Zachary Kurmas
 *
 */
// (C) 2009 Zachary Kurmas
// Created December 31, 2009
public class DLException extends Exception {
   public DLException(String message, Throwable cause) {
      super(message, cause);
   }
}
