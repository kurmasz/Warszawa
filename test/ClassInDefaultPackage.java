
/****************************************************************
 * @author Zachary Kurmas
 *
 *Just a simple class in the default package used to beta ClassFinder
 ****************************************************************/
// (C) 2008 Zachary Kurmas
// Created Jun 12, 2008
public class ClassInDefaultPackage
{
	public static class InnerPublic
	{
		public int innerPublic_classInDefaultPackage = 10;
	}
	
	protected static class InnerProtected
	{
		public int innerProtected_classInDefaultPackage = 11;
		
		public InnerProtected() {
			return;
		}
		
	}
	
	@SuppressWarnings("unused")
	private static class InnerPrivate
	{
		public int innerPrivate_classInDefaultPackage = 12;
		
		public InnerPrivate() {
			return;
		}
	}
	
	
	public int classInDefaultPackage = 9;
	protected String Dave = "Dave";
	
	public ClassInDefaultPackage()
	{
		return;
	}
}
