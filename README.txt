Warszawa is a Java library containing code I found that I was either frequently rewriting or copying between projects.
The key features of this library include the following packages:

* warszawa.io:
   Helper methods for opening PrintWriters and InputStreams.  These helper methods can (if desired)
   ^ configure the desired autoflush and Charset
   ^ map names like "-", "stdin", "stdout", and "stderr" onto System.in and System.out
   ^ automatically decompress files with a .bz2 suffix
   ^ display and error message and exit if the file cannot be opened.

* warszawa.dl:
  Classes for dynamically loading classes and instantiating instances of those classes. In particular, warszawa.dl
  ^ combines the dynamic loading of the class, instantiation of a new object, and casting of that new object into one method;
  ^ watches for the myriad exceptions potentially thrown by the above steps and re-throws a single exception: DyCLJException; and
  ^ generates detailed, helpful error messages.

* warszawa.intlistgen:
  Classes that generate lists of integers.  These classes are useful for generating test cases for student projects.

* warszawa.log:
  A simple logging utility

Run "ant dist" to build the .jar file and the javadocs.

There are also a set of system tests.  These tests are primarily to verify that the .jar file correctly includes all
necessary dependencies.  From the SystemTest directory, run ./systemTest.sh
