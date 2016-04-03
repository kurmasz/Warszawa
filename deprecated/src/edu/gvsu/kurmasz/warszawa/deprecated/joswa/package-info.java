/*******************************************************************************
 * 
 * Joswa (<u>JO</u>pt <u>S</u>imple <u>W</u>ith <u>A</u>nnotations) is a wrapper
 * for Paul Hosler's <a href="http://jopt-simple.sourceforge.net/"
 * target="_top"><code>JOpt Simple</code></a> command-line parser.
 * 
 * Joswa behaves in much the same way as JOpt Simple, except
 * <ol>
 * 
 * <li>Users use annotations to describe command-line parameters instead of
 * forming "sentences" or chains of method calls.
 * 
 * <li>Users can specify that options matching a given pattern be ignored.
 * 
 * </ol>
 * 
 * <h3>Use of annotations</h3>
 * 
 * Users specify options by annotating <em>{@code public}</em> instance
 * variables with {@link edu.gvsu.kurmasz.warszawa.deprecated.joswa.JoswaOption}. For example,
 * 
 * 
 * <pre>
 * public class MyOptions
 * {
 * 	&#064;JoswaOption
 * 	public boolean verbose = false;
 * 
 * 	&#064;JoswaOption
 * 	public Integer debugLevel = 6;
 * }
 * </pre>
 * 
 * defines two options: A flag named "verbose", and an option named "debugLevel"
 * that requires an integer parameter. The following code will set the instance
 * variables based on command-line parameters:
 * 
 * <pre>
 * public static void main(String[] args)
 * {
 * 	MyOptions mo = new MyOptions();
 * 	JoswaOptionParser parser = new JoswaOptionParser(mo);
 * 	parser.parse(args);
 * 
 * 	// The object mo now contains values based on command-line parameters.
 * }
 * </pre>
 * 
 * See {@link edu.gvsu.kurmasz.warszawa.deprecated.joswa.JoswaOption} and
 * {@link edu.gvsu.kurmasz.warszawa.deprecated.joswa.JoswaOptionParser} for more details.
 * 
 * <h3>Ignore options</h3>
 * 
 * <p>
 * In some cases, the complete list of options is not known until after the
 * command-line has been parsed. For example, <a
 * href="http://www.cis.gvsu.edu/~kurmasz/JLSCircuitTester" target="_top">
 * <code>JLSCircuitTester</code></a> allows users to specify an external module
 * (called an <code>InputSetLoader</code>) to parse data files. These external
 * modules may themselves require command line parameters. Of course, the
 * challenge is that we don't know what the command line parameters for a
 * particular module are until we have parsed the command like to determine
 * which external module is in use.
 * </p>
 * 
 * <p>
 * To handle this circular dependency, one can require that all options for the
 * external module exhibit some pattern (e.g., they all begin with "--ISL").
 * {@code JoswaOptionParser} allows the user to specify that all options
 * matching the given pattern be ignored during parsing. The ignored options can
 * be obtained from the parser and passed to the external module once
 * instantiated. (The external module can even have its own Joswa parser.) Note
 * that "ignored" options with parameters must be specified like this:
 * <code>--ISLmaxLines=17</code> because the parser has no way of knowing which
 * options have parameters and which do not.
 * </p>
 * 
 * <h3>Limitations</h3>
 * 
 * <ul>
 * 
 * <li>Options must correspond to public instance variables.
 * 
 * <li>It is not possible to give a single instance variable multiple long or
 * short names. Each annotated instance variable may have at most one long and
 * one short name.
 * 
 * <li>It is not possible to have options with optional parameters.
 * 
 * <li>It is not possible to have options with multiple parameters.
 * 
 * <li>It is not possible to make an option required (i.e., to require that the
 * user specify a --debugLevel).
 * 
 * </ul>
 * 
 * The requirement that options correspond to public instance variables comes
 * from Java's security mechanism. (There are ways around this limitation. See
 * <a target="_top" href="http://jewelcli.sourceforge.net">JewelCLI</a> for one
 * example.) I'm pretty sure the remaining limitations can be addressed easily,
 * I just haven't gotten to them yet.
 * 
 * 
 * <h3>Why JOpt Simple?</h3>
 * 
 * There are many Java option parsing libraries available. (The <a
 * href="http://jopt-simple.sourceforge.net/">JOpt Simple</a> web page contains
 * one of many lists.) I chose <code>JOpt Simple</code> because it was the only
 * one that supported <em>all</em> of these features:
 * 
 * <ul>
 * 
 * <li>support for giving each option a long and short name.
 * 
 * <li>automatic help generation (i.e., automatic generation of option list)
 * 
 * <li>automatically allows users to abbreviate long options (i.e., users can
 * type "--verb" and "--vers" instead of "--verbose" and "--verson").
 * 
 * </ul>
 * 
 * <h3>Acknowledgments</h3>
 * 
 * Many thanks to Paul Hosler for <a href="http://jopt-simple.sourceforge.net/"
 * target="_top"><code>JOpt Simple</code></a> and for making a couple of
 * adjustments to <code>JOpt Simple</code> to improve <code>Joswa</code>'s
 * ability to report errors.
 * 
 * <p>
 * 
 * I am not the first person to figure out how to use annotations to define
 * command-line parameters.
 * <ul>
 * <li>Kohsuke Kawaguchi developed a good package called <a
 * href="https://args4j.dev.java.net/" target="_top">args4j</a>.
 * <li>I am also impressed by <a target="_top"
 * href="http://jewelcli.sourceforge.net/">JewelCLI</a>.
 * </ul>
 * 
 * However, as of December, 2009, neither package supported abbreviated option
 * names. I also needed the "filtering" feature.
 * 
 * 
 ******************************************************************************/
package edu.gvsu.kurmasz.warszawa.deprecated.joswa;

