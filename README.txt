Warszawa is a Java library containing routines I found myself using often in different projects.
The key features of this library include:

* Helper methods for opening PrintWriters and InputStreams.  These helper methods can (if desired)
   ^ configure the desired autoflush and Charset
   ^ map names like "-", "stdin", "stdout", and "stderr" onto System.in and System.out
   ^ automatically decompress files with a .bz2 suffix
   ^ display and error message and exit if the file cannot be opened.

* Helper methods for dynamically loading classes and instantiating instances of those classes

* Classes that generating lists of integers to be used as test cases

* A simple logging utility

Run "ant dist" to build the .jar file and the javadocs.

There are also a set of system tests.  These tests are primarily to verify that the .jar file correctly includes all
necessary dependencies.  From the SystemTest directory, run ./systemTest.sh
