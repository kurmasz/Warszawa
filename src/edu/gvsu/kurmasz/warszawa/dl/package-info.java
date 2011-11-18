/*****************************************************************
 * Classes that simplify the instantiation of dynamically loaded Java
 * classes. (In other words, {@code WarszawaDL}'s purpose is to simplify the process of
 * instantiating an object of a class that is specified at run time instead of
 * at compile time.)
 *
 * <p>
 * In particular, the {@link edu.gvsu.kurmasz.warszawa.dl.SimpleFactory} class
 * <ul>
 * <li>Combines the dynamic loading of the class, instantiation of a new object,
 * and casting of that new object into one method.
 * <li>Watches for the myriad exceptions potentially thrown by the above steps
 * and re-throws a single exception:
 * {@link edu.gvsu.kurmasz.warszawa.dl.DLException}.
 * <li>Generates detailed, helpful error messages.
 * </ul>
 *******************************************************************/

//* <p>
// * Note: If you're wondering why
// * {@link edu.gvsu.kurmasz.warszawa.dl.SimpleFactory#make(String, Class)} doesn't
// * have an overloaded version {@code make(String name)} that calls {@code
// * make(name, Object.class)} , it's because there is not much point: The return
// * type of this method would be {@code Object}; thus, you wouldn't be able to
// * call any methods unique to the dynamically loaded object. (Actually, I
// * suppose somebody might want to dynamically load objects and only use {@code
// * Object} methods like {@code equals}. When that happens, e-mail me, and I'll
// * consider adding the overloaded method.)
// * </p>

package edu.gvsu.kurmasz.warszawa.dl;