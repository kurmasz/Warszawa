/**
 * Copyright (c) Zachary Kurmas 2007
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

import joptsimple.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*******************************************************************************
 * 
 * A command-line parser whose options are described by {@link JoswaOption}
 * annotations on an object's fields (as opposed to fully specified in code).
 * 
 * @author Zachary Kurmas
 * 
 ******************************************************************************/
// (C) 2007 Zachary Kurmas
public class JoswaOptionParser
{
	/*************************************************************************
	 * Exception thrown if user tries to use the same option name for two
	 * different options.
	 * 
	 * @author Zachary Kurmas
	 * 
	 **************************************************************************/
	protected static class DuplicateOptionException extends OptionException
	{
		/***********************************************************************
		 * Constructor
		 * 
		 * @param duplicate
		 *            name of duplicate option
		 **********************************************************************/
		public DuplicateOptionException(String duplicate) {
			super(java.util.Collections.singletonList(duplicate));
		}

		/**
		 * {@inheritDoc}
		 */
		public String getMessage()
		{
			return "The option " + options() + " was used previously";
		}
	}

	protected Object o; // The object containing the annotated fields into which
	// the
	// command line data is to be placed.
	protected joptsimple.OptionParser parser; // The object that actually does
	// the parser.
	protected List<String> excludedOptions = new ArrayList<String>();
	protected List<String> leftovers = new ArrayList<String>();

	protected List<String> usedOptionNames = new ArrayList<String>();

	/***************************************************************************
	 * 
	 * Constructor.
	 * 
	 * @param o_in
	 *            The object containing the annotated fields into which the
	 *            command line data is to be placed.
	 * 
	 * @throws joptsimple.OptionException
	 *             if {@code o_in} doesn't specify a valid set of options. (In
	 *             practice, this is a programmer error, not a run-time
	 *             problem.)
	 * 
	 * @throws IllegalArgumentException
	 *             if the user annotates a field that is not {@code public}.
	 * 
	 **************************************************************************/

	public JoswaOptionParser(Object o_in) throws joptsimple.OptionException,
			IllegalArgumentException {
		o = o_in;
		parser = new OptionParser();

		// This method can throw an OptionException, but only if the
		// programmer doesn't set up the object correctly.
		makeOptionSet();
	}

	/***************************************************************************
	 * Recursively generate an array of <em>all</em> fields. (Calling
	 * getFields() returns the public fields only.)
	 * 
	 * @param c
	 * @param list
	 **************************************************************************/

	protected void getAllFields(Class<? extends Object> c, List<Field> list)
	{
		Class<? extends Object> parent = c.getSuperclass();
		if (parent != null) {
			getAllFields(parent, list);
		}
		list.addAll(Arrays.asList(c.getDeclaredFields()));
	}

	/***************************************************************************
	 * 
	 * Construct the object that describes the command line options.
	 * 
	 * @throws OptionException
	 *             if the Object describing the command-line arguments is not
	 *             well-formed.
	 * 
	 * @throws IllegalArgumentException
	 *             if the type of an annotated field is not valid.
	 **************************************************************************/
	protected void makeOptionSet() throws OptionException,
			IllegalArgumentException
	{
		// Get the list of fields in the object

		// The commented line below will get the list of public fields only. We
		// chose to process *all* fields so we could throw an exception when the
		// user incorrectly annotated a private filed. (Had we looped through
		// public fields only, then an annotated private field would become a
		// "silent error".)
		// Field[] fields = o.getClass().getFields();

		List<Field> fields = new ArrayList<Field>();
		getAllFields(o.getClass(), fields);

		// try to generate a field for each object. (Some fields may
		// not correspond to command-line options.)
		for (Field field : fields) {

			// System.out.println("Checking field: " + field);
			// May throw an OptionException if Object is not set up properly
			addOption(field);
		} // end for
	} // end makeOptionSet

	/***************************************************************************
	 * 
	 * Tell the parser about the command-line option corresponding to the given
	 * {@code Field}.
	 * 
	 * @param field
	 *            a {@code Field} object for the instance variable annotated as
	 *            a command-line parameter.
	 * 
	 * @throws OptionException
	 *             if the annotation for {@code field} is invalid.
	 * 
	 * @throws IllegalArgumentException
	 *             if the type of an annotated field is not valid.
	 **************************************************************************/

	protected void addOption(Field field) throws DuplicateOptionException,
			IllegalArgumentException
	{

		// Grab the annotation for this field
		JoswaOption annotation = field.getAnnotation(JoswaOption.class);

		// If there is no JoswaOption annotation, then we're done.
		if (annotation == null) {
			return;
		}

		// Make sure field is public. It may be possible to remove this
		// restriction:
		// See
		// http://www.onjava.com/pub/a/onjava/2003/11/12/reflection.html?page=1.
		// Note however, that this may make it difficult to use package with
		// applets.
		// For now, I'm going to avoid writing code that will work in one place
		// and not in another.
		if (!Modifier.isPublic(field.getModifiers())) {
			throw new IllegalArgumentException("Annotated field "
					+ field.getName() + " is not public");
		}

		// //////////////////
		//
		// Handle Long name
		//
		// //////////////////
		String optionName = getOptionName(field, annotation);

		// ///////////////////////////////////////////////////////
		//
		// The JOpt Simple package expects option descriptions to look
		// something like this:
		//
		// parser.accept("FOO").withRequiredArg().describedAs("bar").ofType(Integer.class);
		//
		// The code below splits this up and sets things one at a time
		// according to the values of the annotations.
		//
		// /////////////////////////////////////////////////////

		//
		// Make list of names
		//

		List<String> localOptionNames = new ArrayList<String>();

		// The "longName" is automatically one of the names.
		localOptionNames.add(optionName);

		// If there is a short name, add it as a "synonym" (so long as the short
		// name and long name aren't identical).
		if (annotation.shortName() != JoswaOption.NO_SHORT
				&& !optionName.equals(annotation.shortName() + "")) {
			localOptionNames.add(annotation.shortName() + "");
		}

		// Make sure the option names haven't been used before. If so, throw an
		// exception
		for (String n : localOptionNames) {
			if (usedOptionNames.contains(n)) {
				throw new DuplicateOptionException(n);
			}
		}
		usedOptionNames.addAll(localOptionNames);

		// /////////////////
		//
		// Handle description
		//
		// //////////////////

		// First, set the description, if present. (The description
		// is used only by help at present; so, I suppose the if
		// statement is unnecessary; however, I separated it, because
		// I don't want the code to break if the JOpt Simple
		// implementation changes.
		OptionSpecBuilder optionBuilder;
		if (annotation.usage().equals(JoswaOption.NONE)) {
			// optionBuilder = parser.accepts(optionName);
			optionBuilder = parser.acceptsAll(localOptionNames);
		} else {
			// optionBuilder = parser.accepts(optionName, annotation.usage());
			optionBuilder = parser.acceptsAll(localOptionNames, annotation
					.usage());
		}

		// //////////////////////////
		//
		// Handle boolean / switch
		//
		// ///////////////////////////

		// if argument is boolean, parameter is a switch. It takes no
		// arguments; and, thus, requires no further processing.
		if (field.getGenericType() == boolean.class
				|| field.getGenericType() == Boolean.class) {
			return;
		}

		/*
		 * Optional arguments are a future feature
		 * 
		 * //////////////////////////// // // Handle required status //
		 * /////////////////////////////
		 * 
		 * ArgumentAcceptingOptionSpec argumentOptionSpec;
		 * if(annotation.isRequired()) { argumentOptionSpec =
		 * optionBuilder.withRequiredArg(); } else { argumentOptionSpec =
		 * optionBuilder.withOptionalArg(); }
		 */
		ArgumentAcceptingOptionSpec<?> argumentOptionSpec = optionBuilder
				.withRequiredArg();

		// //////////////////////////
		//
		// Specify parameter name
		//
		// ///////////////////////////

		// set parameter name, if given
		if (!annotation.argName().equals(JoswaOption.NONE)) {
			argumentOptionSpec.describedAs(annotation.argName());
		}

		// /////////////////////////////
		//
		// Specify parameter data type
		//
		// /////////////////////////////

		// Specify field type
		argumentOptionSpec.ofType(field.getType());

	} // end makeOption

	/***************************************************************************
	 * 
	 * Parse the command line
	 * 
	 * @param args
	 *            the command line
	 * 
	 * @return the {@code List} of arguments that were not part of any options.
	 * 
	 * @throws joptsimple.OptionException
	 *             if there is a problem while parsing.
	 * 
	 **************************************************************************/

	public List<String> parse(String[] args) throws joptsimple.OptionException
	{
		// Parse the command line
		// System.out.println("Args are: " + Arrays.toString(args));
		OptionSet options = parser.parse(args);

		//
		// Now, set values.
		//

		Field[] fields = o.getClass().getFields();
		for (Field field : fields) {
			setFieldValue(field, options);
		}

		// Return any unused parameters.
		return options.nonOptionArguments();
	}

	/***************************************************************************
	 * 
	 * Parse the command line ignoring {@code String}s that match the given
	 * pattern. Use ... to obtain the list of strings that match the exclusion
	 * pattern.
	 * 
	 * @param args
	 *            the command line
	 * 
	 * @param pattern
	 *            A regular expression {@code Pattern} describing the {@code
	 *            String}s to exclude.
	 * 
	 * @return the {@code List} of arguments that were not part of any options
	 *         and were not excluded.
	 * 
	 * @throws joptsimple.OptionException
	 *             if there is a problem while parsing.
	 * 
	 **************************************************************************/
	public List<String> parse(String[] args, Pattern pattern)
			throws joptsimple.OptionException
	{

		return parse(filter(args, pattern).toArray(new String[0]));
	}

	/***************************************************************************
	 * 
	 * Set the data in the given field based on the corresponding command line
	 * option.
	 * 
	 **************************************************************************/

	protected void setFieldValue(Field field, OptionSet options)
	{
		// If this field has no annotation, then it doesn't correspond
		// to a command-line parameter.
		JoswaOption annotation = field.getAnnotation(JoswaOption.class);
		if (annotation == null) {
			return;
		}

		String optName = getOptionName(field, annotation);

		// If the given value didn't appear on the command line, then
		// there is nothing to set.
		if (!options.has(optName)) {
			return;
		} // end if hasOption

		try {

			// Options with no arguments should be booleans
			if (!options.hasArgument(optName)) {
				field.setBoolean(o, true);
				return;
			}

			// Otherwise, we use valueOf to turn the parameter from a
			// String into the specified data type.
			field.set(o, options.valueOf(optName));
		} catch (IllegalAccessException e) {
			System.err.println("Joswa caught an IllegalAccessException on " + field + " because " + e.getMessage());
		}
	}

	/***************************************************************************
	 * 
	 * Determine the "long" name for the option corresponding to the specified
	 * {@code Field}.
	 * 
	 * @param field
	 *            the annotated {@code Field}.
	 * 
	 * @param annotation
	 *            the {@code Field}'s annotation
	 * 
	 * @return the "long" name for the option corresponding to {@code field}.
	 * 
	 **************************************************************************/

	protected String getOptionName(Field field, JoswaOption annotation)
	{
		assert field != null && annotation != null : "ERROR! Null values passed to getOptionName()";

		// The option name (i.e., the "long name") is the field name
		// by default. However, the user can override this by
		// providing a value to longName in the annotation.
		String optionName;
		if (!annotation.longName().equals(JoswaOption.NONE)) {
			optionName = annotation.longName();
		} else {
			// long name is field name
			optionName = field.getName();
		}
		return optionName;
	}

	/***************************************************************************
	 * 
	 * Prints usage information for the command line. This method calls
	 * {@link #printHelp(OutputStream)} and handles any resulting {@code
	 * IOException}.
	 * 
	 * @param out
	 *            where to print
	 * 
	 **************************************************************************/

	public void printHelp(PrintStream out)
	{
		try {
			printHelp((OutputStream) out);
		} catch (IOException e) {
			throw new AssertionError(
					"IOException thrown when using PrintStream.");
		}
	}

	/***************************************************************************
	 * 
	 * Prints usage information for the command line.
	 * 
	 * @param out
	 *            where to print
	 * 
	 * @throws IOException
	 *             if there's a problem writing to the {@code OutputStream}
	 **************************************************************************/
	public void printHelp(OutputStream out) throws IOException
	{
		parser.printHelpOn(out);
	}

	/***************************************************************************
	 * 
	 * Prints usage information for the command line.
	 * 
	 * @param out
	 *            where to print
	 *
	 * @throws IOException
	 *             if there's a problem writing to the {@code OutputStream}
	 **************************************************************************/
	public void printHelp(Writer out) throws IOException
	{
		parser.printHelpOn(out);
	}

	/***************************************************************************
	 * 
	 * Remove from {@code allArgs} all {@code Strings} that match the given
	 * {@code Pattern} and place them in {@code excludedOptions}.
	 * 
	 * @param allArgs
	 *            the array of {@code String}s to search.
	 * 
	 * @param pattern
	 *            A regex {@code Pattern} describing which {@code String}s to
	 *            exclude.
	 * 
	 * @return the {@code List} of included {@code String}s.
	 * 
	 **************************************************************************/
	protected List<String> filter(String[] allArgs, Pattern pattern)
	{
		// Copy all args into an ArrayList. The list chosen must
		// support the remove operation.
		LinkedList<String> include = new LinkedList<String>(Arrays
				.asList(allArgs));

		// System.out.println("Inclue in: "+ include.size() + " "+ include);
		Iterator<String> iter = include.listIterator(0);
		while (iter.hasNext()) {
			String next = iter.next();
			Matcher m = pattern.matcher(next);
			if (m.find()) {
				// System.out.println("Match! " + next + " " + m.group());
				excludedOptions.add(next);
				iter.remove();
			}
		}
		// System.out.println("Include out: " + include.size() +
		// " " + include);
		return include;
	}

	/***************************************************************************
	 * 
	 * Returns the {@code List} of options excluded by the previous parse.
	 * Return value is undefined if called before before {@code parse}. Note
	 * that all excluded options must be one word only. Thus, options taking
	 * arguments must use the "--debugLevel=17" format.
	 * 
	 * @return the {@code List} of options excluded by the previous parse.
	 * 
	 **************************************************************************/

	public List<String> getExcludedOptions()
	{
		return excludedOptions;
	}

	/***************************************************************************
	 * 
	 * Print the results of the last {@code parse} (used for debugging).
	 * 
	 * @param out
	 *            the {@code PrintStream} to which to write.
	 * 
	 * @param box
	 *            {@code true} if there should be a box around the output.
	 * 
	 **************************************************************************/
	public void printParseResults(PrintStream out, boolean box)
	{
		String start = "";
		if (box) {
			out
					.println("*****************************************************************");
			start = "* ";
		}

		// Print options
		out.println(start + "Options: ");
		for (Field field : o.getClass().getFields()) {

			// print annotated public fields only.
			if (!Modifier.isPublic(field.getModifiers())) {
				continue;
			}
			if (field.getAnnotation(JoswaOption.class) == null) {
				continue;
			}

			try {
				Object value = field.get(o);
				out.println(start + "\t" + field.getName() + ":\t" + value);
			} catch (IllegalAccessException e) {

				// If we catch this exception, then somehow we tried
				// to read a non-public field. The if statements
				// above should catch that.
				throw new AssertionError("There is a bug in "
						+ "SimpleOptionParser");
			}
		} // end for

		out.println(start);
		if (leftovers != null && leftovers.size() > 0) {
			out.println(start + "Remaining arguments:");
			for (String a : leftovers) {
				out.println(start + "\t" + a);
			}
			out.println(start);
		} // end if

		if (excludedOptions != null && excludedOptions.size() > 0) {
			out.println("Excluded options: ");
			for (String a : excludedOptions) {
				out.println(start + "\t" + a);
			}
			out.println(start);
		} // end if
		if (box) {
			out
					.println("*****************************************************************");
		}
	}// end printParseResult

	/***************************************************************************
	 * 
	 * Print the results of the last {@code parse} (used for debugging).
	 * 
	 * @param out
	 *            the {@code PrintStream} to which to write.
	 * 
	 **************************************************************************/
	public void printParseResults(PrintStream out)
	{
		printParseResults(out, false);
	}

} // end class
