package edu.gvsu.kurmasz.warszawa.deprecated.joswa;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*******************************************************************************
 * 
 * Annotates a field that will take the value of a command-line option. The
 * command line-option must either
 * 
 * <ul>
 * 
 * <li>be a flag associated with a <code>boolean</code> variable, or
 * 
 * <li>have a required has a required parameter and be associated with a Java
 * object that can be constructed from a <code>String</code>. In particular, as
 * explained by <a target="_top" href="http://jopt-simple.sourceforge.net/apidocs/joptsimple/ArgumentAcceptingOptionSpec.html#ofType(java.lang.Class)"
 * >{@code ArgumentAcceptingOptionSpec.ofType(Class argumentType)}</a> in the <a target="_top"
 * href="http://jopt-simple.sourceforge.net">{@code JOpt Simple}</a> package,
 * the class associated with a non-flag option must either have
 * 
 * <ul>
 * 
 * <li>a {@code public static} method called {@code valueOf} that accepts a
 * single argument of type {@code String} and whose return type is the same as
 * the class on which the method is declared (the {@code java.lang} primitive
 * wrapper classes have such methods), or
 * 
 * <li>a constructor which accepts a single argument of type {@code String}.
 * 
 * </ul>
 * 
 * </ul>
 * 
 * Other notes:
 * 
 * <ul>
 * <li>Annotations may be on <em>{@code public}</em> fields only. Annotating
 * {@code private}, {@code protected} or package protected fields will probably
 * result in a run time error. In the future, I may add the ability to set
 * protected / private fields through the use of public methods. (I found code
 * on the web that will let me do this; but, it appears to be a little fragile.)
 * 
 * <li>Annotations are inherited. This means that you can add to a set of
 * options in class {@code A}, by defining class {@code B} to extend {@code A};
 * however, you cannot override an option name in a parent class. For example,
 * if {@code A} contains an annotation creating an option "help", {@code B}
 * cannot also have an option named "help". Attempting to do so will result in a
 * "duplicate option" exception. You can override fields, but they must be
 * annotated with different names. (I suppose I could change the implementation
 * so that when a name collision is detected, and the annotations are from a
 * parent and child, only the annotation from the child is used. However, that
 * looks like a lot of work, so I will wait to do it until there appears to be a
 * need.)
 * 
 * <li>To create an option with a short name only, simply set {@code longName}
 * to be the desired one-character name.
 * </ul>
 * 
 * <h3>Sample</h3>
 * 
 * <pre>
 * public class MyOptions
 * {
 *    &#064;JoswaOption(usage=&quot;request verbose output&quot;, shortName='v')
 *    public boolean verbose = false;
 * 
 *    &#064;JoswaOption(usage=&quot;display version&quot;)
 *    public boolean version = false;
 *    
 *    &#064;JoswaOption(usage=&quot;Max number of errors to print&quot;,argName=&quot;errors&quot;)
 *    public Integer maxError = 47; // note use of Integer instead of int
 * 
 *    // Notice that the option name need not match variable name
 *    &#064;JoswaOption(shortName='u', longName=&quot;userName&quot;, argName=&quot;user's name&quot;)
 *    public String name = &quot;Bob&quot;
 *    
 *     &#064;JoswaOption(longName=&quot;q&quot;, usage=&quot;option with short name only)
 *     public boolean activateQueue = false;
 * 
 * </pre>
 * 
 * @author Zachary Kurmas
 * 
 ******************************************************************************/
// (C) 2007 Zachary Kurmas
// We want to examine this at runtime
@Retention(RetentionPolicy.RUNTIME)
// This annotation applies to fields only
@Target(value = { ElementType.FIELD })
public @interface JoswaOption {

	/** Default value for {@code String}s used by this annotation */
	public static final String NONE = "";

	/** Default value for {@code char}s used by this annotation */
	public static final char NO_SHORT = '\0';

	/** Describes usage of option when printing help */
	String usage() default NONE;

	/**
	 * An optional single-letter, single-dash short-cut for an option (e.g., -d)
	 */
	char shortName() default NO_SHORT;

	/**
	 * Long option name. If not specified, defaults to the name of the field.
	 * Long option names begin with two dashes (e.g., --verbose).
	 */
	String longName() default NONE;

	/** Name of the argument (if present); used when printing help */
	String argName() default NONE;
} // end Annotation Option
