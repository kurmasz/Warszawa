/**
 * Copyright (c) Zachary Kurmas 2010
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.gvsu.kurmasz.warszawa.deprecated.joswa;

import org.junit.Assert;
import org.junit.Test;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



/*****************************************************************
 *
 * Test class for {@code SimpleOptionParser}
 *
 *
 * @author Zachary Kurmas
 *
 ******************************************************************/
//(C) 2007 Zachary Kurmas

public class JoswaOptionParserTest
{

	public static class TestBase
	{
		// This is here to make sure that it doesn't trip anything up.
		// It shouldn't because it isn't annotated.
		@SuppressWarnings("unused")
		private int privateInteger = 735;
		
		public boolean equals(Object other)
		{

			for (Field field : this.getClass().getFields()) {
				if (!Modifier.isPublic(field.getModifiers())) {
					continue;
				}

				Field otherField = null;
				try {
					otherField = other.getClass().getField(field.getName());
				} catch (NoSuchFieldException e) {
					return false;
				} catch (SecurityException e) {
					return false;
				}

				try {
					Object myValue = field.get(this);
					Object otherValue = otherField.get(other);
					if (myValue == null && otherValue != null) {
						return false;
					}
					if (myValue != null && !myValue.equals(otherValue)) {
						return false;
					}
				} catch (IllegalAccessException e) {
					System.err.println("Programmer error: " + e.getMessage());
				}
			} // end for
			return true;
		} // end equals

		public String toString()
		{
			StringBuffer buffer = new StringBuffer("{");
			for (Field field : this.getClass().getFields()) {

				try {
					Object value = field.get(this);
					buffer.append(field.getName() + " = " + value + "; ");
				} catch (IllegalAccessException e) {
					System.err.println("Programmer error: " + e.getMessage());
				}
			}
			buffer.append("}");
			return buffer.toString();
		} // end toString


	} // end TestBase

	public static class Test1 extends TestBase
	{

		@JoswaOption
		public boolean flag = false;

		@JoswaOption
		public Integer intP = 17;

		@JoswaOption
		public String stringP = "Foobar";

		@JoswaOption
		public String ISLarg = "orig";

		/*
	public boolean equals(Object other_in) {
	    Test1 other = (Test1)other_in;
	    return flag == other.flag && intP.equals(other.intP) &&
		stringP.equals(other.stringP);
	}

	public String toString() {
	    String answer = "{";
	    answer += "flag = " + flag + "; ";
	    answer += "intP = " + intP + "; ";
	    answer += "stringP = " + stringP + "}";
	    return answer;
	}
		 */
	}
	
	public static class Test2 extends Test1
	{
		@JoswaOption 
		public boolean flag2 = false;
		
	}
	
	public static class TestLong extends TestBase
	{
		@JoswaOption(longName="longFlag")
		public boolean flag = false;
		
		@JoswaOption(longName="intFlag")
		public Integer flagI = 16;
	}
	
	public static class TestShort extends TestBase
	{
		@JoswaOption(shortName='g')
		public boolean myFlag = false;
	}

	public static class TestShort2 extends TestBase
	{
		@JoswaOption
		public boolean a = false;
		
		@JoswaOption 
		public boolean b = false;
		
		@JoswaOption
		public boolean c = false; 
		
		@JoswaOption
		public Integer d = 7;
	}

	public static class DemoHelp extends TestBase
	{
		@JoswaOption(usage="verbose output", shortName='v')
		public boolean verbose = false;

		@JoswaOption(usage="Max number of errors to print",
				argName="errors")
				public Integer maxError = 47;

		// argument intentionally omitted
		@JoswaOption(usage="Maximum simultor run time")
		public Integer timeLimit = 100;


		@JoswaOption(shortName='n', argName="your name here")
		public String name = "bob";

		@JoswaOption
		public boolean boringFlag = true;
		
		@JoswaOption(longName="debugLevel", shortName='d', 
				usage="Amount of debugging information", argName="level")
		public Integer debug;
		
		@JoswaOption(shortName='o')
		public boolean o = false;
		
		@JoswaOption(shortName='s', longName="s")
		public Integer shorty = 17;
	}
	
	
	public void testCorrect(String name, 
			Object empty, Object answer, 
			String commandLine, String leftovers,
			String filter)
	{

		JoswaOptionParser parser  = new JoswaOptionParser(empty);
		List<String> left_obs;
		String[] parts = new String[0];
		if (commandLine != null ) {
			parts = commandLine.split(" ");
		}
		if (filter == null) {
			
			/*
			System.out.println("Command line parts: ");
			for (String p : parts) {
				System.out.println("->" + p + "<-");
			}
			*/
						
			left_obs = parser.parse(parts);	    
		} else {
			java.util.regex.Pattern p = java.util.regex.Pattern.compile(filter);
			left_obs = parser.parse(parts, p);	    
		}

		Assert.assertEquals("Test " + name + " failed", 
				answer, empty);

		if (leftovers == null || leftovers.equals("")) {
			if (left_obs.size() != 0) {
				System.out.println("Unexpected leftovers:  ->" + left_obs.get(0) +"<-");
			}
			Assert.assertEquals("Leftovers for " + name,
					0, left_obs.size());
		} else {
			Assert.assertEquals("Leftovers for " + name,
					Arrays.asList(leftovers.split(" ")),
					left_obs);
		}

	}

	@Test public void test1()
	{
		Test1 t1 = new Test1();
		Test1 answer1 = new Test1();
		answer1.flag = true;
		answer1.intP = 43;
		answer1.stringP = "Boo";
		answer1.ISLarg = "harry";
		testCorrect("1", t1, answer1, 
				"--flag --intP 43 --stringP Boo fred --ISLarg=harry", 
				"fred" , null);

		Test1 t2 = new Test1();
		Test1 answer2 = new Test1();
		answer2.flag = false;
		answer2.intP = 43;
		answer2.stringP = "Boo";
		testCorrect("2", t2, answer2, "george --intP 43 bob --stringP Boo dave",
				"george bob dave", null);

		Test1 t3 = new Test1();
		Test1 answer3 = new Test1();
		answer3.flag = true;
		answer3.intP = 17;
		answer3.stringP = "Boo";
		testCorrect("3", t3, answer3, "--flag Henry --stringP Boo", "Henry", 
				null);


		Test1 t4 = new Test1();
		Test1 answer4 = new Test1();
		answer4.flag = true;
		answer4.intP = 43;
		answer4.stringP = "Foobar";
		testCorrect("4", t4, answer4, "--flag --intP 43", null, null);


		//
		// Include filtering
		// 

		Test1 t5 = new Test1();
		Test1 answer5 = new Test1();
		answer5.flag = true;
		answer5.intP = 43;
		answer5.stringP = "Boo";
		testCorrect("1", t5, answer5,
				"--flag --intP 43 --stringP Boo --ISLarg=changed fred", 
				"fred" , "^--ISL");

		Test1 t6 = new Test1();
		Test1 answer6 = new Test1();
		answer6.flag = true;
		answer6.intP = 43;
		answer6.stringP = "Boo";
		testCorrect("1", t6, answer6,
				"--flag --intP 43 --stringP Boo --ISLarg changed fred", 
				"changed fred" , "^--ISL");


	}
	
	@Test public void testInherit()
	{
		Test2 t1 = new Test2();
		Test2 answer1 = new Test2();
		answer1.flag = true;
		answer1.intP = 43;
		answer1.stringP = "Boo";
		answer1.ISLarg = "harry";
		testCorrect("1i", t1, answer1, 
				"--flag --intP 43 --stringP Boo fred --ISLarg=harry", 
				"fred" , null);
		
		Test2 t2 = new Test2();
		answer1.flag2 = true;
		testCorrect("2i", t2, answer1, 
				"--flag --intP 43 --stringP Boo fred --ISLarg=harry --flag2", 
				"fred" , null);
		
		
	}

	@Test public void testShort1()
	{
		TestShort ts = new TestShort();
		TestShort answer = new TestShort();
		answer.myFlag = true;
		testCorrect("short", ts, answer, "--m", "", null);
	}
	
	@Test public void testShort2()
	{
		TestShort2 ts2 = new TestShort2();
		TestShort2 answer = new TestShort2();
		answer.a = true;
		answer.c = true;
		answer.d = 9;
		testCorrect("Short2", ts2, answer, "-ac -d 9", "", null);
	}

	@Test public void testLong()
	{
		TestLong tl = new TestLong();
		TestLong answer = new TestLong();
		
		answer.flag = true;
		answer.flagI = 19;
		
		testCorrect("Long1", tl, answer, "--longFlag --intFlag 19", "", null);
	}
	
	
	public static class TestOneLetter extends TestBase
	{
		@JoswaOption(shortName='f')
		public boolean f = false;
		
		@JoswaOption(shortName='g', longName="g")
		public boolean flag = false;
	}
	
	@Test public void testOneLetter()
	{
		TestOneLetter answer = new TestOneLetter();
		
		answer.f = true;
		answer.flag = true;
		
		testCorrect("Test One Letter (a)", new TestOneLetter(), answer, "-f -g", "", null);
		testCorrect("Test One Letter (b)", new TestOneLetter(), answer, "--f --g", "", null);
		
		answer.f = false;
		answer.flag = true;
		
		testCorrect("Test One Letter (c)", new TestOneLetter(), answer, "-g", "", null);
		testCorrect("Test One Letter (d)", new TestOneLetter(), answer, "--g", "", null);
		
		answer.f = true;
		answer.flag = false;
		
		testCorrect("Test One Letter (e)", new TestOneLetter(), answer, "-f", "", null);
		testCorrect("Test One Letter (f)", new TestOneLetter(), answer, "--f", "", null);
		
		answer.f = false;
		answer.flag = false;
		
		testCorrect("Test One Letter (g)", new TestOneLetter(), answer, null, "", null);
		
	}
	
	
	public void testFilter(String input, String included, 
			String excluded, String regexp)
	{

		// Parameter to parser doesn't matter
		JoswaOptionParser parser = new JoswaOptionParser(this);

		String[] args = input.split(" ");

		java.util.regex.Pattern p = java.util.regex.Pattern.compile(regexp);
		List<String> included_obs = parser.filter(args, p);

		List<String> exp_include = Arrays.asList(included.split(" "));
		if (included == null || included.equals("")) {
			exp_include = new ArrayList<String>();
		}
		Assert.assertEquals("Included differ", exp_include, included_obs );

		List<String> exp_exclude = Arrays.asList(excluded.split(" "));
		if (excluded == null || excluded.equals("")) {
			exp_exclude = new ArrayList<String>();
		}


		Assert.assertEquals("Excluded differs", exp_exclude, 
				parser.getExcludedOptions());
	}

	public void testFilter()
	{

		// typical usage
		testFilter("--arg1 foo --ISLarg2=foo --flag --ISLarg4 notarg",
				"--arg1 foo --flag notarg",
				"--ISLarg2=foo --ISLarg4",
		"^--ISL");

		// nothing included
		testFilter("--ISLarg2=foo --ISLarg4",
				"",
				"--ISLarg2=foo --ISLarg4",
		"^--ISL");


		// nothing excluded
		testFilter("--arg1 foo --flag notarg",
				"--arg1 foo --flag notarg",
				"",
		"^--ISL");


	}

	// From simpleopt testing code.
	public static final String LINE_SEPARATOR =
		System.getProperty( "line.separator" );
	public static String join( String[] pieces, String separator ) {
		StringBuffer buffer = new StringBuffer();

		for ( int i = 0; i < pieces.length; ++i ) {
			buffer.append( pieces[ i ] );

			if ( i < pieces.length - 1 )
				buffer.append( separator );
		}

		return buffer.toString();
	}


	@Test public void testPrintHelp() throws Exception
	{
		DemoHelp dh = new DemoHelp();
		JoswaOptionParser parser = new JoswaOptionParser(dh);

		StringWriter out = new StringWriter();
		parser.printHelp(out);
		
		String[] expectedLines = {
				"Option                                  Description                            ",
				"------                                  -----------                            ",
				"--boringFlag                                                                   ",
				"-d, --debugLevel <Integer: level>       Amount of debugging information        ",
				"--maxError <Integer: errors>            Max number of errors to print          ",
				"-n, --name <your name here>                                                    ",
				"-o                                                                             ",
				"-s <Integer>                                                                   ",
				"--timeLimit <Integer>                   Maximum simultor run time              ",
				"-v, --verbose                           verbose output                         ",
				""
		};

		// Replaced spaces with '*' so differences are easier to see.
		String expected = 
			join( expectedLines, LINE_SEPARATOR ).replace(' ', '*');
		
		final boolean PRINT_ME = false;
		
		if (PRINT_ME) {
			System.out.println("Expected:");
			System.out.println(expected);

			System.out.println("Observed");
			System.out.println(out.toString().replace(' ', '*'));
		}
		
		Assert.assertEquals(expected, out.toString().replace(' ', '*'));
	} // end testPrintHelp
	
	
	public void testBadParse(TestBase t, String[] commandLine, String expectedMessage)
	{
		Test1 t1 = new Test1();
		JoswaOptionParser parser  = new JoswaOptionParser(t1);
		
		try {
			parser.parse(commandLine);
			Assert.fail("Should not have parsed.");
		} catch (joptsimple.OptionException e) {
			Assert.assertEquals(expectedMessage, e.getMessage());
		}
		
	}
	
	@Test public void testUnreognizedOption()
	{
		testBadParse(new Test1(), new String[]{"--fred"}, "'fred' is not a recognized option");
	}
	
	@Test public void testMissingParameter()
	{
		testBadParse(new Test1(), new String[]{"--intP"}, "Option ['intP'] requires an argument");
	}
	
	@Test public void testWrongArgumentType()
	{
		testBadParse(new Test1(), new String[]{"--intP", "oops"}, 
				"Cannot convert argument 'oops' of option ['intP'] to class java.lang.Integer");
	}
	
	//
	// Be sure duplicate option names are caught
	//
	
	// duplicate short names
	public static class Duplicate1
	{
		@JoswaOption(shortName='f')
		public boolean flag1 = false;
		
		@JoswaOption(shortName='f')
		public boolean flag2 = false;
		
	}
	
	public static class Duplicate1b
	{
		@JoswaOption(shortName='i')
		public Integer int1 = 17;
		
		@JoswaOption(shortName='i')
		public Integer int2 = 3;
		
	}
	
	
	// duplicate long names
	 public static class Duplicate2
	{
		 @JoswaOption
		 public boolean flag = false;
		 
		 @JoswaOption(longName="flag")
		 public boolean flag2;
	}
	
	 
	 public static class Duplicate2b
		{
			 @JoswaOption
			 public Integer int1 = 55;
			 
			 @JoswaOption(longName="int1")
			 public Integer int2;
		}
	
	public void testBadConfigure(Object o, String why, String exceptionMessage)
	{
		try {
			@SuppressWarnings("unused")
			JoswaOptionParser parser = new JoswaOptionParser(o);
			Assert.fail("class " + o.getClass().getName() + " should not configure because " + why); 
		} catch (joptsimple.OptionException e) {
			if (exceptionMessage != null) {
				Assert.assertEquals("Wrong exception message", exceptionMessage, e.getMessage());
			}
		}
		
	}
	
	public void testDuplicate(Object o, String message)
	{
		testBadConfigure(o, "the object has a duplicate flag", message);
	}
	
	@Test public void testDuplicates()
	{
		testDuplicate(new Duplicate1(), "The option [f] was used previously");
		testDuplicate(new Duplicate1b(), "The option [i] was used previously");
		testDuplicate(new Duplicate2(), "The option [flag] was used previously");
		
	}
	
	//
	// Test badly formed option objects
	//
	public void testBadOptionType(Object o, String why, String exceptionMessage)
	{
		try {
			@SuppressWarnings("unused")
			JoswaOptionParser parser = new JoswaOptionParser(o);
			Assert.fail("class " + o.getClass().getName() + " should not configure because " + why); 
		} catch (IllegalArgumentException e) {
			if (exceptionMessage != null) {
				Assert.assertEquals("Wrong exception message", exceptionMessage, e.getMessage());
			}
		}
		
	}
	
	public static class Bad1
	{
		@JoswaOption
		public int fred = 19;
	}
	
	public static class Bad2
	{
		@JoswaOption
		public char fred = 'g';
	}
	
	public static class Bad3
	{
		@JoswaOption
		public long bigNum= 100000000000L;
	}

	public static class Bad4
	{
		@SuppressWarnings("unused")
		@JoswaOption
		private boolean flag = false;
	}
	
	public static class Bad5 extends Bad4
	{
		@JoswaOption
		public Integer myInt = 19;
	}

	@Test public void testBadlyFormed()
	{
		testBadOptionType(new Bad1(), "the object has an option of type int instead of Integer.", null);
		testBadOptionType(new Bad2(), "the object has an option of type char instead of Char.", null);
		testBadOptionType(new Bad3(), "the object has an option of type long instead of Long.", null);
		

		testBadOptionType(new Bad4(), "the object tries to use a private member.", null);
		testBadOptionType(new Bad5(), "the object tries to use an inherited private member.", null);
	}

	public static void main(String[] args) 
	{
		
		/*
		Test1 t1 = new Test1();
		JoswaOptionParser jop = new JoswaOptionParser(t1);
		
		try {
			jop.parse(new String[]{"--intP", "floob"});
		} catch (joptsimple.OptionException e) {
			System.out.println("Problem: " + e.getMessage());
		}
		*/
		DemoHelp dh = new DemoHelp();
		JoswaOptionParser jop2 = new JoswaOptionParser(dh);
		jop2.printHelp(System.out);
		
		
		Duplicate2 d2 = new Duplicate2();
		try {
			@SuppressWarnings("unused")
			JoswaOptionParser jop3 = new JoswaOptionParser(d2);
		} catch (joptsimple.OptionException e) {
			System.out.println("Oops: " + e.getMessage());
		}
		
		
		
		
	}
	
	
}

